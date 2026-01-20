package com.mindwell.emotion.backend.entity;



import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "emotion_question")
public class EmotionQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text", length = 1000, nullable = false)
    private String questionText;

    @Column(name = "emotion_category", length = 100, nullable = false)
    private String emotionCategory;

    @ElementCollection
    @CollectionTable(name = "emotion_options", joinColumns = @JoinColumn(name = "question_id"))
    private List<EmotionOption> options;

    // Constructors
    public EmotionQuestion() {}

    public EmotionQuestion(String questionText, String emotionCategory) {
        this.questionText = questionText;
        this.emotionCategory = emotionCategory;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getEmotionCategory() {
        return emotionCategory;
    }

    public void setEmotionCategory(String emotionCategory) {
        this.emotionCategory = emotionCategory;
    }

    public List<EmotionOption> getOptions() {
        return options;
    }

    public void setOptions(List<EmotionOption> options) {
        this.options = options;
    }

    // Embedded class for options
    @Embeddable
    public static class EmotionOption {
        @Column(name = "option_text", length = 500)
        private String optionText;

        @Column(name = "emotion_weight", length = 100)
        private String emotionWeight;

        public EmotionOption() {}

        public EmotionOption(String optionText, String emotionWeight) {
            this.optionText = optionText;
            this.emotionWeight = emotionWeight;
        }

        public String getOptionText() {
            return optionText;
        }

        public void setOptionText(String optionText) {
            this.optionText = optionText;
        }

        public String getEmotionWeight() {
            return emotionWeight;
        }

        public void setEmotionWeight(String emotionWeight) {
            this.emotionWeight = emotionWeight;
        }
    }
}