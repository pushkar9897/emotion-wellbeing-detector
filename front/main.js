// Main navigation and assessment starter

function startAssessment(assessmentType) {
    console.log(`Starting ${assessmentType} assessment`);
    
    // Store assessment type in localStorage
    localStorage.setItem('currentAssessment', assessmentType);
    
    // Redirect to assessment page
    window.location.href = 'assessment.html';
}

// Smooth scrolling for navigation
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});