package com.mindwell.emotion.backend.service;



import com.mindwell.emotion.backend.entity.EmotionQuestion;
import com.mindwell.emotion.backend.repo.EmotionQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmotionQuestionService {

    @Autowired
    private EmotionQuestionRepository questionRepository;

    public List<EmotionQuestion> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<EmotionQuestion> getQuestionsByCategory(String emotionCategory) {
        return questionRepository.findByEmotionCategory(emotionCategory);
    }

    public EmotionQuestion saveQuestion(EmotionQuestion question) {
        return questionRepository.save(question);
    }

    public EmotionQuestion getQuestionById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }
}

// ============================================================================

