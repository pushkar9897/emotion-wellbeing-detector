package com.mindwell.emotion.backend.controller;



import com.mindwell.emotion.backend.dto.EmotionSubmissionRequest;
import com.mindwell.emotion.backend.entity.EmotionQuestion;
import com.mindwell.emotion.backend.entity.EmotionResponse;
import com.mindwell.emotion.backend.service.EmotionAnalysisService;
import com.mindwell.emotion.backend.service.EmotionQuestionService;
import com.mindwell.emotion.backend.service.EmotionResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/emotion")
public class EmotionAssessmentController {

    @Autowired
    private EmotionQuestionService questionService;

    @Autowired
    private EmotionResponseService responseService;

    @Autowired
    private EmotionAnalysisService analysisService;

    @GetMapping("/questions/{assessmentType}")
    public ResponseEntity<List<EmotionQuestion>> getQuestions(@PathVariable String assessmentType) {
        try {
            List<EmotionQuestion> questions = questionService.getQuestionsByCategory(assessmentType);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitAssessment(@RequestBody EmotionSubmissionRequest request) {
        try {
            System.out.println("📝 Received assessment submission from: " + request.getUsername());
            System.out.println("📋 Assessment Type: " + request.getAssessmentType());
            System.out.println("💬 Total Responses: " + request.getResponses().size());

            // Save all responses
            List<EmotionResponse> responses = new ArrayList<>();

            for (EmotionSubmissionRequest.QuestionResponse qr : request.getResponses()) {
                EmotionResponse response = new EmotionResponse(
                        request.getUsername(),
                        request.getAssessmentType(),
                        qr.getQuestionText(),
                        qr.getSelectedOption(),
                        qr.getEmotionWeight()
                );
                responses.add(response);
            }

            responseService.saveAllResponses(responses);
            System.out.println("✅ Responses saved to database");

            // Generate emotion analysis using Gemini API
            System.out.println("🧠 Generating AI emotion analysis...");
            String emotionAnalysis = analysisService.generateEmotionAnalysis(responses);
            System.out.println("✅ Analysis generated successfully");

            // Prepare response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("emotionAnalysis", emotionAnalysis);
            responseData.put("totalResponses", request.getResponses().size());
            responseData.put("assessmentType", request.getAssessmentType());
            responseData.put("message", "Assessment completed successfully!");

            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to process assessment: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/analysis/{username}/{assessmentType}")
    public ResponseEntity<Map<String, String>> getAnalysis(
            @PathVariable String username,
            @PathVariable String assessmentType) {
        try {
            List<EmotionResponse> responses = responseService.getResponsesByUserAndAssessment(
                    username, assessmentType
            );

            if (responses.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "No assessment data found");
                return ResponseEntity.notFound().build();
            }

            String analysis = analysisService.generateEmotionAnalysis(responses);
            Map<String, String> response = new HashMap<>();
            response.put("analysis", analysis);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to generate analysis: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<EmotionResponse>> getUserHistory(@PathVariable String username) {
        try {
            List<EmotionResponse> history = responseService.getResponsesByUser(username);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}