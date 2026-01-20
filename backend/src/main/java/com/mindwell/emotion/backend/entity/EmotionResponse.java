package com.mindwell.emotion.backend.entity;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "emotion_responses")
public class EmotionResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(name = "assessment_type", nullable = false)
    private String assessmentType;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(name = "selected_option", columnDefinition = "TEXT", nullable = false)
    private String selectedOption;

    @Column(name = "emotion_weight", nullable = false)
    private String emotionWeight;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    // Constructors
    public EmotionResponse() {
        this.submittedAt = LocalDateTime.now();
    }

    public EmotionResponse(String username, String assessmentType, String questionText,
                           String selectedOption, String emotionWeight) {
        this.username = username;
        this.assessmentType = assessmentType;
        this.questionText = questionText;
        this.selectedOption = selectedOption;
        this.emotionWeight = emotionWeight;
        this.submittedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}