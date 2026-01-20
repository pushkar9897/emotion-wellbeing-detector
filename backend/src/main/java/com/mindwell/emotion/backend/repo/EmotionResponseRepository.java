package com.mindwell.emotion.backend.repo;


import com.mindwell.emotion.backend.entity.EmotionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmotionResponseRepository extends JpaRepository<EmotionResponse, Long> {
    List<EmotionResponse> findByUsernameAndAssessmentTypeOrderBySubmittedAtDesc(String username, String assessmentType);
    List<EmotionResponse> findByUsernameOrderBySubmittedAtDesc(String username);
}