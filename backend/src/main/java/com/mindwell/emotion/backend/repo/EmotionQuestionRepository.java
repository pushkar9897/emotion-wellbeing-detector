package com.mindwell.emotion.backend.repo;



import com.mindwell.emotion.backend.entity.EmotionQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmotionQuestionRepository extends JpaRepository<EmotionQuestion, Long> {
    List<EmotionQuestion> findByEmotionCategory(String emotionCategory);
}

// ============================================================================

