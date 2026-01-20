package com.mindwell.emotion.backend.service;



import com.mindwell.emotion.backend.entity.EmotionResponse;
import com.mindwell.emotion.backend.repo.EmotionResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmotionResponseService {

    @Autowired
    private EmotionResponseRepository responseRepository;

    public EmotionResponse saveResponse(EmotionResponse response) {
        return responseRepository.save(response);
    }

    public List<EmotionResponse> saveAllResponses(List<EmotionResponse> responses) {
        return responseRepository.saveAll(responses);
    }

    public List<EmotionResponse> getResponsesByUser(String username) {
        return responseRepository.findByUsernameOrderBySubmittedAtDesc(username);
    }

    public List<EmotionResponse> getResponsesByUserAndAssessment(String username, String assessmentType) {
        return responseRepository.findByUsernameAndAssessmentTypeOrderBySubmittedAtDesc(username, assessmentType);
    }
}