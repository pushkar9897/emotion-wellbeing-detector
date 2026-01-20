package com.mindwell.emotion.backend.dto;



import java.util.List;

public class EmotionSubmissionRequest {
    private String username;
    private String assessmentType;
    private List<QuestionResponse> responses;

    // Constructors
    public EmotionSubmissionRequest() {}

    public EmotionSubmissionRequest(String username, String assessmentType, List<QuestionResponse> responses) {
        this.username = username;
        this.assessmentType = assessmentType;
        this.responses = responses;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public List<QuestionResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<QuestionResponse> responses) {
        this.responses = responses;
    }

    // Nested class for individual question responses
    public static class QuestionResponse {
        private String questionText;
        private String selectedOption;
        private String emotionWeight;

        // Constructors
        public QuestionResponse() {}

        public QuestionResponse(String questionText, String selectedOption, String emotionWeight) {
            this.questionText = questionText;
            this.selectedOption = selectedOption;
            this.emotionWeight = emotionWeight;
        }

        // Getters and Setters
        public String getQuestionText() {
            return questionText;
        }

        public void setQuestionText(String questionText) {
            this.questionText = questionText;
        }

        public String getSelectedOption() {
            return selectedOption;
        }

        public void setSelectedOption(String selectedOption) {
            this.selectedOption = selectedOption;
        }

        public String getEmotionWeight() {
            return emotionWeight;
        }

        public void setEmotionWeight(String emotionWeight) {
            this.emotionWeight = emotionWeight;
        }
    }
}