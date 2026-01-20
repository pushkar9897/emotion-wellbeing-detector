// Assessment page logic
let currentQuestionIndex = 0;
let questions = [];
let assessmentResponses = [];
let totalQuestions = 0;
const username = "user_" + Date.now(); // Generate unique user ID
let assessmentType = "";

// Assessment type configurations
const assessmentConfig = {
    'stress_anxiety': {
        title: 'Stress & Anxiety Assessment',
        description: 'Understand your stress levels and anxiety patterns',
        icon: '😰'
    },
    'mood_emotional': {
        title: 'Mood & Emotional State Assessment',
        description: 'Explore your emotional landscape and mood patterns',
        icon: '😊'
    },
    'confidence_esteem': {
        title: 'Self-Confidence & Self-Esteem Assessment',
        description: 'Assess your self-perception and confidence levels',
        icon: '💪'
    },
    'worklife_burnout': {
        title: 'Work-Life Balance & Burnout Assessment',
        description: 'Evaluate your work-life balance and wellbeing',
        icon: '⚖️'
    }
};

// Fetch questions from API
async function fetchQuestions() {
    try {
        // Get assessment type from localStorage
        assessmentType = localStorage.getItem('currentAssessment');
        
        if (!assessmentType) {
            alert('No assessment selected. Redirecting to home page.');
            window.location.href = 'index.html';
            return;
        }

        // Update page header
        const config = assessmentConfig[assessmentType];
        document.getElementById('assessment-title').textContent = 
            config.icon + ' ' + config.title;
        document.getElementById('assessment-description').textContent = config.description;

        console.log(`Fetching questions for: ${assessmentType}`);
        
        const response = await fetch(`http://localhost:8080/api/emotion/questions/${assessmentType}`);
        
        if (!response.ok) {
            throw new Error('Failed to load questions');
        }
        
        questions = await response.json();
        totalQuestions = questions.length;
        
        console.log(`Loaded ${totalQuestions} questions`);
        
        document.getElementById('question-counter').textContent = 
            `Question 1 of ${totalQuestions}`;
        
        displayQuestion();
        
    } catch (error) {
        console.error('Error fetching questions:', error);
        document.getElementById('question-text').textContent = 
            "Failed to load questions. Please check your connection and try again.";
        document.getElementById('assessment-description').textContent = 
            "Error: " + error.message;
    }
}

// Display current question
function displayQuestion() {
    if (questions.length === 0) return;

    const question = questions[currentQuestionIndex];
    const questionText = document.getElementById('question-text');
    const optionsContainer = document.getElementById('options-container');
    
    // Update question text
    questionText.textContent = question.questionText;
    
    // Clear previous options
    optionsContainer.innerHTML = '';
    
    // Create option buttons
    question.options.forEach((option, index) => {
        const optionButton = document.createElement('button');
        optionButton.className = 'option-button';
        optionButton.textContent = option.optionText;
        optionButton.onclick = () => selectOption(option.optionText, option.emotionWeight);
        
        // If this question was already answered, highlight the selected option
        const existingResponse = assessmentResponses[currentQuestionIndex];
        if (existingResponse && existingResponse.selectedOption === option.optionText) {
            optionButton.classList.add('selected');
        }
        
        optionsContainer.appendChild(optionButton);
    });
    
    // Update progress bar
    updateProgressBar();
    
    // Update navigation buttons
    updateNavigationButtons();
}

// Handle option selection
function selectOption(optionText, emotionWeight) {
    const question = questions[currentQuestionIndex];
    
    // Store or update response
    assessmentResponses[currentQuestionIndex] = {
        questionText: question.questionText,
        selectedOption: optionText,
        emotionWeight: emotionWeight
    };
    
    console.log(`Selected: ${optionText} (${emotionWeight})`);
    
    // Highlight selected option
    document.querySelectorAll('.option-button').forEach(btn => {
        btn.classList.remove('selected');
        if (btn.textContent === optionText) {
            btn.classList.add('selected');
        }
    });
    
    // Enable next button
    document.getElementById('next-button').disabled = false;
    
    // Auto-advance after short delay (optional - remove if you want manual navigation)
    setTimeout(() => {
        if (currentQuestionIndex < questions.length - 1) {
            nextQuestion();
        } else {
            showSubmitButton();
        }
    }, 800);
}

// Navigate to next question
function nextQuestion() {
    if (!assessmentResponses[currentQuestionIndex]) {
        alert('Please select an option before continuing.');
        return;
    }
    
    if (currentQuestionIndex < questions.length - 1) {
        currentQuestionIndex++;
        displayQuestion();
        document.getElementById('question-counter').textContent = 
            `Question ${currentQuestionIndex + 1} of ${totalQuestions}`;
    } else {
        showSubmitButton();
    }
}

// Navigate to previous question
function previousQuestion() {
    if (currentQuestionIndex > 0) {
        currentQuestionIndex--;
        displayQuestion();
        document.getElementById('question-counter').textContent = 
            `Question ${currentQuestionIndex + 1} of ${totalQuestions}`;
    }
}

// Update progress bar
function updateProgressBar() {
    const progress = ((currentQuestionIndex + 1) / totalQuestions) * 100;
    document.getElementById('progress-fill').style.width = progress + '%';
}

// Update navigation button states
function updateNavigationButtons() {
    const backButton = document.getElementById('back-button');
    const nextButton = document.getElementById('next-button');
    
    backButton.disabled = currentQuestionIndex === 0;
    nextButton.disabled = !assessmentResponses[currentQuestionIndex];
    
    // Hide/show submit button
    if (currentQuestionIndex === questions.length - 1 && 
        assessmentResponses[currentQuestionIndex]) {
        showSubmitButton();
    } else {
        document.getElementById('submit-button').style.display = 'none';
        document.getElementById('next-button').style.display = 'inline-block';
    }
}

// Show submit button
function showSubmitButton() {
    document.getElementById('next-button').style.display = 'none';
    document.getElementById('submit-button').style.display = 'block';
}

// Submit assessment to backend
async function submitAssessment() {
    // Verify all questions are answered
    if (assessmentResponses.length !== totalQuestions) {
        alert('Please answer all questions before submitting.');
        return;
    }
    
    try {
        // Show loading state
        const submitButton = document.getElementById('submit-button');
        submitButton.disabled = true;
        submitButton.textContent = 'Analyzing... 🧠';
        
        console.log('Submitting assessment...');
        console.log('Responses:', assessmentResponses);
        
        const response = await fetch('http://localhost:8080/api/emotion/submit', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: username,
                assessmentType: assessmentType,
                responses: assessmentResponses
            })
        });

        if (!response.ok) {
            throw new Error('Failed to submit assessment');
        }

        const result = await response.json();
        
        console.log('Assessment submitted successfully!');
        console.log('Result:', result);
        
        // Store results in localStorage
        localStorage.setItem('emotionAnalysis', result.emotionAnalysis);
        localStorage.setItem('assessmentType', result.assessmentType);
        localStorage.setItem('totalResponses', result.totalResponses);
        
        // Redirect to results page
        window.location.href = 'results.html';
        
    } catch (error) {
        console.error('Error submitting assessment:', error);
        alert('Failed to submit assessment. Please check your connection and try again.');
        
        // Reset button
        const submitButton = document.getElementById('submit-button');
        submitButton.disabled = false;
        submitButton.textContent = 'Complete Assessment 🧠';
    }
}

// Initialize on page load
window.onload = fetchQuestions;