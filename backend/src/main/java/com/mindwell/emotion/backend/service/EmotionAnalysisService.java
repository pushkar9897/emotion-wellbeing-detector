//package com.mindwell.emotion.backend.service;
//
//import com.mindwell.emotion.backend.entity.EmotionResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class EmotionAnalysisService {
//
//    @Value("${gemini.api.key}")
//    private String apiKey;
//
//    @Value("${gemini.api.url}")
//    private String apiUrl;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public String generateEmotionAnalysis(List<EmotionResponse> responses) {
//        try {
//            String prompt = buildEmotionAnalysisPrompt(responses);
//            String aiAnalysis = callGeminiAPI(prompt);
//
//            if (aiAnalysis != null && !aiAnalysis.equals("Emotion analysis could not be generated.")) {
//                System.out.println("✅ AI Analysis Generated Successfully (" + aiAnalysis.length() + " characters)");
//                return formatAnalysisResponse(aiAnalysis);
//            } else {
//                System.out.println("⚠️ AI analysis failed, using fallback");
//                return generateFallbackAnalysis(responses);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("❌ Error in emotion analysis: " + e.getMessage());
//            return generateFallbackAnalysis(responses);
//        }
//    }
//
//    private String callGeminiAPI(String promptText) {
//        // ---- Build Request Body (Same as your working code) ----
//        Map<String, Object> textPart = new HashMap<>();
//        textPart.put("text", promptText);
//
//        Map<String, Object> content = new HashMap<>();
//        content.put("parts", List.of(textPart));
//
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("contents", List.of(content));
//
//        // ---- Headers ----
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
//
//        // ---- Final URL with API KEY ----
//        String finalUrl = apiUrl + "?key=" + apiKey;
//
//        System.out.println("🧠 Calling Gemini API for Emotion Analysis...");
//        System.out.println("📡 API URL: " + apiUrl);
//
//        try {
//            ResponseEntity<Map> response = restTemplate.postForEntity(finalUrl, request, Map.class);
//
//            // ---- Parse Response (Same as your working code) ----
//            List candidates = (List) response.getBody().get("candidates");
//            Map candidate = (Map) candidates.get(0);
//            Map contentMap = (Map) candidate.get("content");
//            List parts = (List) contentMap.get("parts");
//            Map part = (Map) parts.get(0);
//
//            String analysisText = part.get("text").toString();
//            System.out.println("✅ Gemini API Response Received");
//
//            return analysisText;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("❌ Gemini API Error: " + e.getMessage());
//            return "Emotion analysis could not be generated.";
//        }
//    }
//
//    private String buildEmotionAnalysisPrompt(List<EmotionResponse> responses) {
//        StringBuilder prompt = new StringBuilder();
//
//        prompt.append("You are an expert clinical psychologist and compassionate emotional wellness coach. ");
//        prompt.append("Provide a comprehensive, deeply personalized psychological analysis based on the assessment below.\n\n");
//
//        prompt.append("═══════════════════════════════════════════════════════════════\n");
//        prompt.append("PARTICIPANT INFORMATION:\n");
//        prompt.append("═══════════════════════════════════════════════════════════════\n");
//        prompt.append("Name: ").append(responses.get(0).getUsername()).append("\n");
//        prompt.append("Assessment Type: ").append(formatAssessmentType(responses.get(0).getAssessmentType())).append("\n");
//        prompt.append("Questions Answered: ").append(responses.size()).append("\n");
//        prompt.append("Date: ").append(responses.get(0).getSubmittedAt().toLocalDate()).append("\n\n");
//
//        prompt.append("═══════════════════════════════════════════════════════════════\n");
//        prompt.append("DETAILED ASSESSMENT RESPONSES:\n");
//        prompt.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        Map<String, Integer> emotionWeights = new HashMap<>();
//
//        for (int i = 0; i < responses.size(); i++) {
//            EmotionResponse resp = responses.get(i);
//            prompt.append("Question ").append(i + 1).append(":\n");
//            prompt.append("Q: ").append(resp.getQuestionText()).append("\n");
//            prompt.append("A: \"").append(resp.getSelectedOption()).append("\"\n");
//            prompt.append("Emotional Indicator: ").append(formatEmotionWeight(resp.getEmotionWeight())).append("\n\n");
//
//            String weight = resp.getEmotionWeight();
//            emotionWeights.put(weight, emotionWeights.getOrDefault(weight, 0) + 1);
//        }
//
//        prompt.append("═══════════════════════════════════════════════════════════════\n");
//        prompt.append("EMOTIONAL PATTERN SUMMARY:\n");
//        prompt.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        emotionWeights.entrySet().stream()
//                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
//                .forEach(entry -> {
//                    double percentage = (entry.getValue() * 100.0) / responses.size();
//                    prompt.append(String.format("• %s: %d responses (%.1f%%)\n",
//                            formatEmotionWeight(entry.getKey()), entry.getValue(), percentage));
//                });
//
//        prompt.append("\n\n");
//        prompt.append("═══════════════════════════════════════════════════════════════\n");
//        prompt.append("ANALYSIS INSTRUCTIONS:\n");
//        prompt.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        prompt.append("Based on the assessment responses above, create a COMPREHENSIVE, PERSONALIZED analysis.\n\n");
//
//        prompt.append("REQUIRED SECTIONS (Use these exact emoji headers):\n\n");
//
//        prompt.append("🧠 EMOTIONAL STATE OVERVIEW\n");
//        prompt.append("Provide a detailed 3-4 paragraph analysis covering:\n");
//        prompt.append("• Primary emotions and feelings currently experienced\n");
//        prompt.append("• Emotional stability and regulation patterns\n");
//        prompt.append("• Dominant mood trends identified in responses\n");
//        prompt.append("• Both emotional strengths AND areas of concern\n");
//        prompt.append("• Specific insights from their actual answers (quote or reference them)\n\n");
//
//        prompt.append("💭 PSYCHOLOGICAL WELLBEING ASSESSMENT\n");
//        prompt.append("Write 2-3 paragraphs analyzing:\n");
//        prompt.append("• Overall mental health indicators from responses\n");
//        prompt.append("• Stress and anxiety levels detected\n");
//        prompt.append("• Signs of burnout, exhaustion, or resilience\n");
//        prompt.append("• Coping mechanisms being employed\n");
//        prompt.append("• Self-esteem and confidence indicators\n\n");
//
//        prompt.append("📊 WELLBEING SCORES\n");
//        prompt.append("Rate each dimension from 1-10 with a 1-2 sentence explanation:\n");
//        prompt.append("• Overall Emotional Wellbeing: [X/10] - [explanation]\n");
//        prompt.append("• Mental Health Status: [X/10] - [explanation]\n");
//        prompt.append("• Stress Management: [X/10] - [explanation]\n");
//        prompt.append("• Resilience & Coping: [X/10] - [explanation]\n");
//        prompt.append("• Self-Confidence: [X/10] - [explanation]\n");
//        prompt.append("• Life Satisfaction: [X/10] - [explanation]\n\n");
//
//        prompt.append("🎯 PERSONALIZED RECOMMENDATIONS\n\n");
//        prompt.append("IMMEDIATE ACTIONS (Next 24-48 Hours):\n");
//        prompt.append("• List 3-4 specific, actionable steps tailored to their responses\n");
//        prompt.append("• Focus on immediate relief and self-care\n\n");
//
//        prompt.append("SHORT-TERM STRATEGIES (Next 1-2 Weeks):\n");
//        prompt.append("• List 3-4 practices to build momentum\n");
//        prompt.append("• Include concrete habits they can start\n\n");
//
//        prompt.append("LONG-TERM WELLBEING PLAN (Next 1-3 Months):\n");
//        prompt.append("• List 3-4 sustainable lifestyle changes\n");
//        prompt.append("• Focus on building resilience and emotional strength\n\n");
//
//        prompt.append("PROFESSIONAL SUPPORT:\n");
//        prompt.append("• Assess if therapy/counseling would be beneficial\n");
//        prompt.append("• Recommend specific therapeutic approaches if needed\n");
//        prompt.append("• Be honest but compassionate about when to seek help\n\n");
//
//        prompt.append("💪 STRENGTHS & GROWTH OPPORTUNITIES\n");
//        prompt.append("Write 2 paragraphs covering:\n");
//        prompt.append("• 3-4 emotional/psychological strengths you observed\n");
//        prompt.append("• 2-3 specific areas for personal growth\n");
//        prompt.append("• Skills or mindsets to develop\n");
//        prompt.append("• How their strengths can help address growth areas\n\n");
//
//        prompt.append("💚 CLOSING MESSAGE OF SUPPORT\n");
//        prompt.append("Write 2-3 heartfelt paragraphs that:\n");
//        prompt.append("• Validate their feelings and current experiences\n");
//        prompt.append("• Acknowledge the courage it takes to assess oneself honestly\n");
//        prompt.append("• Provide genuine encouragement and hope\n");
//        prompt.append("• Remind them of their capability for growth and healing\n");
//        prompt.append("• End with an empowering, uplifting statement\n\n");
//
//        prompt.append("═══════════════════════════════════════════════════════════════\n");
//        prompt.append("STYLE GUIDELINES:\n");
//        prompt.append("═══════════════════════════════════════════════════════════════\n");
//        prompt.append("• Use warm, empathetic, non-judgmental language throughout\n");
//        prompt.append("• Reference their SPECIFIC responses - make it personal, not generic\n");
//        prompt.append("• Use the emoji headers exactly as shown above\n");
//        prompt.append("• Write in second person (you/your) to make it direct and personal\n");
//        prompt.append("• Balance honest assessment with compassion and hope\n");
//        prompt.append("• Aim for 1000-1500 words total for comprehensive coverage\n");
//        prompt.append("• Use bullet points for lists but paragraphs for explanations\n\n");
//
//        prompt.append("IMPORTANT: This person trusted you with their emotional state. Honor that trust ");
//        prompt.append("by providing insights that are specific to THEIR responses, genuinely helpful, ");
//        prompt.append("and delivered with compassion. They need to feel seen, understood, and supported.\n\n");
//
//        prompt.append("Now provide the comprehensive psychological analysis:");
//
//        return prompt.toString();
//    }
//
//    private String formatAssessmentType(String type) {
//        return switch (type) {
//            case "stress_anxiety" -> "Stress & Anxiety Assessment";
//            case "mood_emotional" -> "Mood & Emotional State Assessment";
//            case "confidence_esteem" -> "Self-Confidence & Self-Esteem Assessment";
//            case "worklife_burnout" -> "Work-Life Balance & Burnout Assessment";
//            default -> type.replace("_", " ").toUpperCase();
//        };
//    }
//
//    private String formatEmotionWeight(String weight) {
//        return weight.replace("_", " ").toUpperCase();
//    }
//
//    private String formatAnalysisResponse(String aiAnalysis) {
//        StringBuilder formatted = new StringBuilder();
//
//        formatted.append("═══════════════════════════════════════════════════════════════\n");
//        formatted.append("           🧠 AI-POWERED COMPREHENSIVE ANALYSIS\n");
//        formatted.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        formatted.append(aiAnalysis);
//
//        formatted.append("\n\n═══════════════════════════════════════════════════════════════\n");
//        formatted.append("⚠️  IMPORTANT DISCLAIMER\n");
//        formatted.append("═══════════════════════════════════════════════════════════════\n\n");
//        formatted.append("This AI-generated analysis is for self-reflection and personal\n");
//        formatted.append("insight only. It is NOT a medical diagnosis and should not replace\n");
//        formatted.append("professional mental health care.\n\n");
//        formatted.append("If you're experiencing:\n");
//        formatted.append("• Persistent distress lasting more than 2 weeks\n");
//        formatted.append("• Suicidal thoughts or self-harm urges\n");
//        formatted.append("• Severe anxiety or panic attacks\n");
//        formatted.append("• Difficulty functioning in daily life\n\n");
//        formatted.append("Please contact a qualified mental health professional immediately.\n\n");
//        formatted.append("Emergency Resources:\n");
//        formatted.append("• National Suicide Prevention Lifeline: 988\n");
//        formatted.append("• Crisis Text Line: Text HOME to 741741\n");
//        formatted.append("• SAMHSA Helpline: 1-800-662-4357\n");
//
//        return formatted.toString();
//    }
//
//    private String generateFallbackAnalysis(List<EmotionResponse> responses) {
//        StringBuilder analysis = new StringBuilder();
//
//        analysis.append("═══════════════════════════════════════════════════════════════\n");
//        analysis.append("           🧠 EMOTION & WELLBEING ANALYSIS\n");
//        analysis.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        analysis.append("⚠️  AI-powered analysis is temporarily unavailable.\n\n");
//
//        Map<String, Long> emotionCounts = responses.stream()
//                .collect(Collectors.groupingBy(EmotionResponse::getEmotionWeight, Collectors.counting()));
//
//        analysis.append("📊 YOUR EMOTIONAL PROFILE:\n");
//        analysis.append("══════════════════════════════════════\n\n");
//
//        emotionCounts.entrySet().stream()
//                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
//                .forEach(entry -> {
//                    double percentage = (entry.getValue() * 100.0) / responses.size();
//                    analysis.append(String.format("• %s: %d responses (%.1f%%)\n",
//                            formatEmotionWeight(entry.getKey()), entry.getValue(), percentage));
//                });
//
//        analysis.append("\n═══════════════════════════════════════════════════════════════\n");
//        analysis.append("💭 KEY INSIGHTS:\n");
//        analysis.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        String assessmentType = responses.get(0).getAssessmentType();
//        analysis.append(getDetailedAssessmentInsights(assessmentType, emotionCounts, responses));
//
//        analysis.append("\n═══════════════════════════════════════════════════════════════\n");
//        analysis.append("🎯 PERSONALIZED RECOMMENDATIONS:\n");
//        analysis.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        analysis.append("IMMEDIATE ACTIONS (Today):\n");
//        analysis.append("  ✓ Take 10 deep breaths and practice grounding techniques\n");
//        analysis.append("  ✓ Connect with a friend or loved one\n");
//        analysis.append("  ✓ Engage in a favorite relaxing activity\n");
//        analysis.append("  ✓ Journal about your feelings for 10 minutes\n\n");
//
//        analysis.append("THIS WEEK:\n");
//        analysis.append("  ✓ Establish a consistent sleep routine (7-9 hours)\n");
//        analysis.append("  ✓ Practice mindfulness meditation (start with 5 minutes daily)\n");
//        analysis.append("  ✓ Engage in physical movement you enjoy\n");
//        analysis.append("  ✓ Limit screen time, especially before bed\n\n");
//
//        analysis.append("ONGOING PRACTICES:\n");
//        analysis.append("  ✓ Build a support network you can rely on\n");
//        analysis.append("  ✓ Set clear boundaries in work and relationships\n");
//        analysis.append("  ✓ Practice self-compassion and positive self-talk\n");
//        analysis.append("  ✓ Consider therapy or counseling for additional support\n\n");
//
//        analysis.append("═══════════════════════════════════════════════════════════════\n");
//        analysis.append("💚 REMEMBER:\n");
//        analysis.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        analysis.append("Taking this assessment shows self-awareness and courage. Your feelings\n");
//        analysis.append("are valid, and seeking to understand yourself better is a powerful step\n");
//        analysis.append("toward growth and healing.\n\n");
//
//        analysis.append("You have the capacity for resilience and positive change. Small, consistent\n");
//        analysis.append("steps in the right direction create meaningful transformation over time.\n\n");
//
//        analysis.append("If you're struggling, please reach out to a mental health professional.\n");
//        analysis.append("Asking for help is a sign of strength, not weakness.\n");
//
//        return analysis.toString();
//    }
//
//    private String getDetailedAssessmentInsights(String assessmentType, Map<String, Long> emotionCounts, List<EmotionResponse> responses) {
//        StringBuilder insights = new StringBuilder();
//        int totalResponses = responses.size();
//
//        switch (assessmentType) {
//            case "stress_anxiety":
//                insights.append("🌿 STRESS & ANXIETY INSIGHTS:\n\n");
//
//                long highStressCount = emotionCounts.entrySet().stream()
//                        .filter(e -> e.getKey().toLowerCase().contains("high") ||
//                                e.getKey().toLowerCase().contains("severe") ||
//                                e.getKey().toLowerCase().contains("extreme"))
//                        .mapToLong(Map.Entry::getValue)
//                        .sum();
//
//                if (highStressCount >= totalResponses * 0.6) {
//                    insights.append("Your responses indicate significant stress levels that may be impacting\n");
//                    insights.append("your daily functioning and wellbeing. This level of stress, if sustained,\n");
//                    insights.append("can affect both mental and physical health.\n\n");
//                    insights.append("RECOMMENDATION: Consider consulting with a mental health professional\n");
//                    insights.append("who can provide personalized strategies and support.\n");
//                } else if (highStressCount >= totalResponses * 0.3) {
//                    insights.append("You're experiencing moderate stress levels. While stress is a normal part\n");
//                    insights.append("of life, it's important to develop healthy coping mechanisms to prevent\n");
//                    insights.append("it from becoming overwhelming.\n\n");
//                    insights.append("Focus on identifying your primary stressors and implementing stress\n");
//                    insights.append("management techniques that work for you.\n");
//                } else {
//                    insights.append("Your stress levels appear relatively manageable, though everyone has room\n");
//                    insights.append("for improvement in stress management. Continue building your toolkit of\n");
//                    insights.append("healthy coping strategies.\n");
//                }
//                break;
//
//            case "mood_emotional":
//                insights.append("😊 MOOD & EMOTIONAL STATE INSIGHTS:\n\n");
//                insights.append("Your responses reveal natural variations in emotional states, which is\n");
//                insights.append("completely normal. Emotional awareness - the ability to recognize and\n");
//                insights.append("understand your feelings - is the foundation of emotional intelligence.\n\n");
//                insights.append("Pay attention to:\n");
//                insights.append("• What situations or people trigger different emotional responses\n");
//                insights.append("• Patterns in when you feel most positive or challenged\n");
//                insights.append("• How your emotions influence your thoughts and behaviors\n");
//                break;
//
//            case "confidence_esteem":
//                insights.append("💪 SELF-CONFIDENCE & ESTEEM INSIGHTS:\n\n");
//
//                long lowConfidenceCount = emotionCounts.entrySet().stream()
//                        .filter(e -> e.getKey().toLowerCase().contains("low") ||
//                                e.getKey().toLowerCase().contains("poor"))
//                        .mapToLong(Map.Entry::getValue)
//                        .sum();
//
//                if (lowConfidenceCount >= totalResponses * 0.5) {
//                    insights.append("Your responses suggest challenges with self-confidence and self-esteem.\n");
//                    insights.append("This is common and can be improved through consistent practice and,\n");
//                    insights.append("if needed, professional support.\n\n");
//                    insights.append("Remember: Self-worth is inherent, not earned. You are valuable simply\n");
//                    insights.append("because you exist, not because of what you achieve or how others see you.\n");
//                } else {
//                    insights.append("You show awareness of both strengths and areas for growth in self-esteem.\n");
//                    insights.append("Building confidence is a journey that involves practicing self-compassion,\n");
//                    insights.append("celebrating small wins, and challenging negative self-talk.\n");
//                }
//                break;
//
//            case "worklife_burnout":
//                insights.append("⚖️  WORK-LIFE BALANCE & BURNOUT INSIGHTS:\n\n");
//                insights.append("Work-life balance is crucial for sustainable wellbeing and performance.\n");
//                insights.append("Burnout is increasingly common in today's always-connected culture, but\n");
//                insights.append("recognizing the signs is the essential first step toward change.\n\n");
//                insights.append("Key areas to address:\n");
//                insights.append("• Setting clear boundaries between work and personal time\n");
//                insights.append("• Prioritizing rest and recovery as much as productivity\n");
//                insights.append("• Aligning daily activities with your core values\n");
//                insights.append("• Building in regular breaks and disconnection periods\n");
//                break;
//        }
//
//        return insights.toString();
//    }
//}




/// /////// second


//package com.mindwell.emotion.backend.service;
//
//import com.mindwell.emotion.backend.entity.EmotionResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class EmotionAnalysisService {
//
//    @Value("${gemini.api.key}")
//    private String apiKey;
//
//    @Value("${gemini.api.url}")
//    private String apiUrl;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public String generateEmotionAnalysis(List<EmotionResponse> responses) {
//        try {
//            String prompt = buildEmotionAnalysisPrompt(responses);
//            String aiAnalysis = callGeminiAPI(prompt);
//
//            if (aiAnalysis != null && !aiAnalysis.equals("Emotion analysis could not be generated.")) {
//                System.out.println("✅ AI Analysis Generated Successfully (" + aiAnalysis.length() + " characters)");
//                return formatAnalysisResponse(aiAnalysis);
//            } else {
//                System.out.println("⚠️ AI analysis failed, using fallback");
//                return generateFallbackAnalysis(responses);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("❌ Error in emotion analysis: " + e.getMessage());
//            return generateFallbackAnalysis(responses);
//        }
//    }
//
//    private String callGeminiAPI(String promptText) {
//        // ---- Build Request Body (Same as your working code) ----
//        Map<String, Object> textPart = new HashMap<>();
//        textPart.put("text", promptText);
//
//        Map<String, Object> content = new HashMap<>();
//        content.put("parts", List.of(textPart));
//
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("contents", List.of(content));
//
//        // ---- Headers ----
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
//
//        // ---- Final URL with API KEY ----
//        String finalUrl = apiUrl + "?key=" + apiKey;
//
//        System.out.println("🧠 Calling Gemini API for Emotion Analysis...");
//        System.out.println("📡 API URL: " + apiUrl);
//        System.out.println("📊 Prompt Length: " + promptText.length() + " characters");
//        System.out.println("📤 Sending prompt to Gemini (first 200 chars): ");
//        System.out.println("   " + promptText.substring(0, Math.min(200, promptText.length())) + "...");
//
//        // Retry logic for rate limits
//        int maxRetries = 2;
//        int retryDelay = 2000; // 2 seconds
//
//        for (int attempt = 1; attempt <= maxRetries; attempt++) {
//            try {
//                ResponseEntity<Map> response = restTemplate.postForEntity(finalUrl, request, Map.class);
//
//                // ---- Parse Response (Same as your working code) ----
//                List candidates = (List) response.getBody().get("candidates");
//                Map candidate = (Map) candidates.get(0);
//                Map contentMap = (Map) candidate.get("content");
//                List parts = (List) contentMap.get("parts");
//                Map part = (Map) parts.get(0);
//
//                String analysisText = part.get("text").toString();
//                System.out.println("✅ Gemini API Response Received (" + analysisText.length() + " characters)");
//                System.out.println("📥 Response Preview: " + analysisText.substring(0, Math.min(150, analysisText.length())) + "...");
//
//                return analysisText;
//
//            } catch (org.springframework.web.client.HttpClientErrorException e) {
//                if (e.getStatusCode().value() == 429) {
//                    // Rate limit error
//                    System.err.println("⚠️ Rate limit exceeded (429). Attempt " + attempt + " of " + maxRetries);
//
//                    if (attempt < maxRetries) {
//                        System.out.println("⏳ Waiting " + (retryDelay / 1000) + " seconds before retry...");
//                        try {
//                            Thread.sleep(retryDelay);
//                            retryDelay *= 2; // Exponential backoff
//                        } catch (InterruptedException ie) {
//                            Thread.currentThread().interrupt();
//                        }
//                    } else {
//                        System.err.println("❌ Rate limit exceeded. Daily quota exhausted.");
//                        System.err.println("💡 Solutions:");
//                        System.err.println("   1. Wait until tomorrow (quota resets daily)");
//                        System.err.println("   2. Get a new API key at: https://aistudio.google.com/app/apikey");
//                        System.err.println("   3. Use gemini-pro model: gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent");
//                        return "Emotion analysis could not be generated.";
//                    }
//                } else if (e.getStatusCode().value() == 404) {
//                    System.err.println("❌ Model not found (404). The model in your configuration doesn't exist.");
//                    System.err.println("💡 Use this in application.properties:");
//                    System.err.println("   gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent");
//                    return "Emotion analysis could not be generated.";
//                } else {
//                    // Other HTTP errors
//                    e.printStackTrace();
//                    System.err.println("❌ Gemini API Error: " + e.getMessage());
//                    return "Emotion analysis could not be generated.";
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.err.println("❌ Gemini API Error: " + e.getMessage());
//                return "Emotion analysis could not be generated.";
//            }
//        }
//
//        return "Emotion analysis could not be generated.";
//    }
//
//    private String buildEmotionAnalysisPrompt(List<EmotionResponse> responses) {
//        StringBuilder prompt = new StringBuilder();
//
//        prompt.append("You are an expert clinical psychologist and compassionate emotional wellness coach. ");
//        prompt.append("Provide a comprehensive, deeply personalized psychological analysis based on the assessment below.\n\n");
//
//        prompt.append("═══════════════════════════════════════════════════════════════\n");
//        prompt.append("PARTICIPANT INFORMATION:\n");
//        prompt.append("═══════════════════════════════════════════════════════════════\n");
//        prompt.append("Name: ").append(responses.get(0).getUsername()).append("\n");
//        prompt.append("Assessment Type: ").append(formatAssessmentType(responses.get(0).getAssessmentType())).append("\n");
//        prompt.append("Questions Answered: ").append(responses.size()).append("\n");
//        prompt.append("Date: ").append(responses.get(0).getSubmittedAt().toLocalDate()).append("\n\n");
//
//        prompt.append("═══════════════════════════════════════════════════════════════\n");
//        prompt.append("DETAILED ASSESSMENT RESPONSES:\n");
//        prompt.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        Map<String, Integer> emotionWeights = new HashMap<>();
//
//        for (int i = 0; i < responses.size(); i++) {
//            EmotionResponse resp = responses.get(i);
//            prompt.append("Question ").append(i + 1).append(":\n");
//            prompt.append("Q: ").append(resp.getQuestionText()).append("\n");
//            prompt.append("A: \"").append(resp.getSelectedOption()).append("\"\n");
//            prompt.append("Emotional Indicator: ").append(formatEmotionWeight(resp.getEmotionWeight())).append("\n\n");
//
//            String weight = resp.getEmotionWeight();
//            emotionWeights.put(weight, emotionWeights.getOrDefault(weight, 0) + 1);
//
//            // LOG EACH QUESTION AND ANSWER
//            System.out.println("📝 Q" + (i+1) + ": " + resp.getQuestionText().substring(0, Math.min(50, resp.getQuestionText().length())) + "...");
//            System.out.println("   ✓ Answer: " + resp.getSelectedOption().substring(0, Math.min(40, resp.getSelectedOption().length())) + "...");
//        }
//
//        prompt.append("═══════════════════════════════════════════════════════════════\n");
//        prompt.append("EMOTIONAL PATTERN SUMMARY:\n");
//        prompt.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        emotionWeights.entrySet().stream()
//                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
//                .forEach(entry -> {
//                    double percentage = (entry.getValue() * 100.0) / responses.size();
//                    prompt.append(String.format("• %s: %d responses (%.1f%%)\n",
//                            formatEmotionWeight(entry.getKey()), entry.getValue(), percentage));
//                });
//
//        prompt.append("\n\n");
//        prompt.append("═══════════════════════════════════════════════════════════════\n");
//        prompt.append("ANALYSIS INSTRUCTIONS:\n");
//        prompt.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        prompt.append("Based on the assessment responses above, create a COMPREHENSIVE, PERSONALIZED analysis.\n\n");
//
//        prompt.append("REQUIRED SECTIONS (Use these exact emoji headers):\n\n");
//
//        prompt.append("🧠 EMOTIONAL STATE OVERVIEW\n");
//        prompt.append("Provide a detailed 3-4 paragraph analysis covering:\n");
//        prompt.append("• Primary emotions and feelings currently experienced\n");
//        prompt.append("• Emotional stability and regulation patterns\n");
//        prompt.append("• Dominant mood trends identified in responses\n");
//        prompt.append("• Both emotional strengths AND areas of concern\n");
//        prompt.append("• Specific insights from their actual answers (quote or reference them)\n\n");
//
//        prompt.append("💭 PSYCHOLOGICAL WELLBEING ASSESSMENT\n");
//        prompt.append("Write 2-3 paragraphs analyzing:\n");
//        prompt.append("• Overall mental health indicators from responses\n");
//        prompt.append("• Stress and anxiety levels detected\n");
//        prompt.append("• Signs of burnout, exhaustion, or resilience\n");
//        prompt.append("• Coping mechanisms being employed\n");
//        prompt.append("• Self-esteem and confidence indicators\n\n");
//
//        prompt.append("📊 WELLBEING SCORES\n");
//        prompt.append("Rate each dimension from 1-10 with a 1-2 sentence explanation:\n");
//        prompt.append("• Overall Emotional Wellbeing: [X/10] - [explanation]\n");
//        prompt.append("• Mental Health Status: [X/10] - [explanation]\n");
//        prompt.append("• Stress Management: [X/10] - [explanation]\n");
//        prompt.append("• Resilience & Coping: [X/10] - [explanation]\n");
//        prompt.append("• Self-Confidence: [X/10] - [explanation]\n");
//        prompt.append("• Life Satisfaction: [X/10] - [explanation]\n\n");
//
//        prompt.append("🎯 PERSONALIZED RECOMMENDATIONS\n\n");
//        prompt.append("IMMEDIATE ACTIONS (Next 24-48 Hours):\n");
//        prompt.append("• List 3-4 specific, actionable steps tailored to their responses\n");
//        prompt.append("• Focus on immediate relief and self-care\n\n");
//
//        prompt.append("SHORT-TERM STRATEGIES (Next 1-2 Weeks):\n");
//        prompt.append("• List 3-4 practices to build momentum\n");
//        prompt.append("• Include concrete habits they can start\n\n");
//
//        prompt.append("LONG-TERM WELLBEING PLAN (Next 1-3 Months):\n");
//        prompt.append("• List 3-4 sustainable lifestyle changes\n");
//        prompt.append("• Focus on building resilience and emotional strength\n\n");
//
//        prompt.append("PROFESSIONAL SUPPORT:\n");
//        prompt.append("• Assess if therapy/counseling would be beneficial\n");
//        prompt.append("• Recommend specific therapeutic approaches if needed\n");
//        prompt.append("• Be honest but compassionate about when to seek help\n\n");
//
//        prompt.append("💪 STRENGTHS & GROWTH OPPORTUNITIES\n");
//        prompt.append("Write 2 paragraphs covering:\n");
//        prompt.append("• 3-4 emotional/psychological strengths you observed\n");
//        prompt.append("• 2-3 specific areas for personal growth\n");
//        prompt.append("• Skills or mindsets to develop\n");
//        prompt.append("• How their strengths can help address growth areas\n\n");
//
//        prompt.append("💚 CLOSING MESSAGE OF SUPPORT\n");
//        prompt.append("Write 2-3 heartfelt paragraphs that:\n");
//        prompt.append("• Validate their feelings and current experiences\n");
//        prompt.append("• Acknowledge the courage it takes to assess oneself honestly\n");
//        prompt.append("• Provide genuine encouragement and hope\n");
//        prompt.append("• Remind them of their capability for growth and healing\n");
//        prompt.append("• End with an empowering, uplifting statement\n\n");
//
//        prompt.append("═══════════════════════════════════════════════════════════════\n");
//        prompt.append("STYLE GUIDELINES:\n");
//        prompt.append("═══════════════════════════════════════════════════════════════\n");
//        prompt.append("• Use warm, empathetic, non-judgmental language throughout\n");
//        prompt.append("• Reference their SPECIFIC responses - make it personal, not generic\n");
//        prompt.append("• Use the emoji headers exactly as shown above\n");
//        prompt.append("• Write in second person (you/your) to make it direct and personal\n");
//        prompt.append("• Balance honest assessment with compassion and hope\n");
//        prompt.append("• Aim for 1000-1500 words total for comprehensive coverage\n");
//        prompt.append("• Use bullet points for lists but paragraphs for explanations\n\n");
//
//        prompt.append("IMPORTANT: This person trusted you with their emotional state. Honor that trust ");
//        prompt.append("by providing insights that are specific to THEIR responses, genuinely helpful, ");
//        prompt.append("and delivered with compassion. They need to feel seen, understood, and supported.\n\n");
//
//        prompt.append("Now provide the comprehensive psychological analysis:");
//
//        return prompt.toString();
//    }
//
//    private String formatAssessmentType(String type) {
//        return switch (type) {
//            case "stress_anxiety" -> "Stress & Anxiety Assessment";
//            case "mood_emotional" -> "Mood & Emotional State Assessment";
//            case "confidence_esteem" -> "Self-Confidence & Self-Esteem Assessment";
//            case "worklife_burnout" -> "Work-Life Balance & Burnout Assessment";
//            default -> type.replace("_", " ").toUpperCase();
//        };
//    }
//
//    private String formatEmotionWeight(String weight) {
//        return weight.replace("_", " ").toUpperCase();
//    }
//
//    private String formatAnalysisResponse(String aiAnalysis) {
//        StringBuilder formatted = new StringBuilder();
//
//        formatted.append("═══════════════════════════════════════════════════════════════\n");
//        formatted.append("           🧠 AI-POWERED COMPREHENSIVE ANALYSIS\n");
//        formatted.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        formatted.append(aiAnalysis);
//
//        formatted.append("\n\n═══════════════════════════════════════════════════════════════\n");
//        formatted.append("⚠️  IMPORTANT DISCLAIMER\n");
//        formatted.append("═══════════════════════════════════════════════════════════════\n\n");
//        formatted.append("This AI-generated analysis is for self-reflection and personal\n");
//        formatted.append("insight only. It is NOT a medical diagnosis and should not replace\n");
//        formatted.append("professional mental health care.\n\n");
//        formatted.append("If you're experiencing:\n");
//        formatted.append("• Persistent distress lasting more than 2 weeks\n");
//        formatted.append("• Suicidal thoughts or self-harm urges\n");
//        formatted.append("• Severe anxiety or panic attacks\n");
//        formatted.append("• Difficulty functioning in daily life\n\n");
//        formatted.append("Please contact a qualified mental health professional immediately.\n\n");
//        formatted.append("Emergency Resources:\n");
//        formatted.append("• National Suicide Prevention Lifeline: 988\n");
//        formatted.append("• Crisis Text Line: Text HOME to 741741\n");
//        formatted.append("• SAMHSA Helpline: 1-800-662-4357\n");
//
//        return formatted.toString();
//    }
//
//    private String generateFallbackAnalysis(List<EmotionResponse> responses) {
//        StringBuilder analysis = new StringBuilder();
//
//        analysis.append("═══════════════════════════════════════════════════════════════\n");
//        analysis.append("           🧠 EMOTION & WELLBEING ANALYSIS\n");
//        analysis.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        analysis.append("⚠️  AI-powered analysis is temporarily unavailable.\n\n");
//
//        Map<String, Long> emotionCounts = responses.stream()
//                .collect(Collectors.groupingBy(EmotionResponse::getEmotionWeight, Collectors.counting()));
//
//        analysis.append("📊 YOUR EMOTIONAL PROFILE:\n");
//        analysis.append("══════════════════════════════════════\n\n");
//
//        emotionCounts.entrySet().stream()
//                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
//                .forEach(entry -> {
//                    double percentage = (entry.getValue() * 100.0) / responses.size();
//                    analysis.append(String.format("• %s: %d responses (%.1f%%)\n",
//                            formatEmotionWeight(entry.getKey()), entry.getValue(), percentage));
//                });
//
//        analysis.append("\n═══════════════════════════════════════════════════════════════\n");
//        analysis.append("💭 KEY INSIGHTS:\n");
//        analysis.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        String assessmentType = responses.get(0).getAssessmentType();
//        analysis.append(getDetailedAssessmentInsights(assessmentType, emotionCounts, responses));
//
//        analysis.append("\n═══════════════════════════════════════════════════════════════\n");
//        analysis.append("🎯 PERSONALIZED RECOMMENDATIONS:\n");
//        analysis.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        analysis.append("IMMEDIATE ACTIONS (Today):\n");
//        analysis.append("  ✓ Take 10 deep breaths and practice grounding techniques\n");
//        analysis.append("  ✓ Connect with a friend or loved one\n");
//        analysis.append("  ✓ Engage in a favorite relaxing activity\n");
//        analysis.append("  ✓ Journal about your feelings for 10 minutes\n\n");
//
//        analysis.append("THIS WEEK:\n");
//        analysis.append("  ✓ Establish a consistent sleep routine (7-9 hours)\n");
//        analysis.append("  ✓ Practice mindfulness meditation (start with 5 minutes daily)\n");
//        analysis.append("  ✓ Engage in physical movement you enjoy\n");
//        analysis.append("  ✓ Limit screen time, especially before bed\n\n");
//
//        analysis.append("ONGOING PRACTICES:\n");
//        analysis.append("  ✓ Build a support network you can rely on\n");
//        analysis.append("  ✓ Set clear boundaries in work and relationships\n");
//        analysis.append("  ✓ Practice self-compassion and positive self-talk\n");
//        analysis.append("  ✓ Consider therapy or counseling for additional support\n\n");
//
//        analysis.append("═══════════════════════════════════════════════════════════════\n");
//        analysis.append("💚 REMEMBER:\n");
//        analysis.append("═══════════════════════════════════════════════════════════════\n\n");
//
//        analysis.append("Taking this assessment shows self-awareness and courage. Your feelings\n");
//        analysis.append("are valid, and seeking to understand yourself better is a powerful step\n");
//        analysis.append("toward growth and healing.\n\n");
//
//        analysis.append("You have the capacity for resilience and positive change. Small, consistent\n");
//        analysis.append("steps in the right direction create meaningful transformation over time.\n\n");
//
//        analysis.append("If you're struggling, please reach out to a mental health professional.\n");
//        analysis.append("Asking for help is a sign of strength, not weakness.\n");
//
//        return analysis.toString();
//    }
//
//    private String getDetailedAssessmentInsights(String assessmentType, Map<String, Long> emotionCounts, List<EmotionResponse> responses) {
//        StringBuilder insights = new StringBuilder();
//        int totalResponses = responses.size();
//
//        switch (assessmentType) {
//            case "stress_anxiety":
//                insights.append("🌿 STRESS & ANXIETY INSIGHTS:\n\n");
//
//                long highStressCount = emotionCounts.entrySet().stream()
//                        .filter(e -> e.getKey().toLowerCase().contains("high") ||
//                                e.getKey().toLowerCase().contains("severe") ||
//                                e.getKey().toLowerCase().contains("extreme"))
//                        .mapToLong(Map.Entry::getValue)
//                        .sum();
//
//                if (highStressCount >= totalResponses * 0.6) {
//                    insights.append("Your responses indicate significant stress levels that may be impacting\n");
//                    insights.append("your daily functioning and wellbeing. This level of stress, if sustained,\n");
//                    insights.append("can affect both mental and physical health.\n\n");
//                    insights.append("RECOMMENDATION: Consider consulting with a mental health professional\n");
//                    insights.append("who can provide personalized strategies and support.\n");
//                } else if (highStressCount >= totalResponses * 0.3) {
//                    insights.append("You're experiencing moderate stress levels. While stress is a normal part\n");
//                    insights.append("of life, it's important to develop healthy coping mechanisms to prevent\n");
//                    insights.append("it from becoming overwhelming.\n\n");
//                    insights.append("Focus on identifying your primary stressors and implementing stress\n");
//                    insights.append("management techniques that work for you.\n");
//                } else {
//                    insights.append("Your stress levels appear relatively manageable, though everyone has room\n");
//                    insights.append("for improvement in stress management. Continue building your toolkit of\n");
//                    insights.append("healthy coping strategies.\n");
//                }
//                break;
//
//            case "mood_emotional":
//                insights.append("😊 MOOD & EMOTIONAL STATE INSIGHTS:\n\n");
//                insights.append("Your responses reveal natural variations in emotional states, which is\n");
//                insights.append("completely normal. Emotional awareness - the ability to recognize and\n");
//                insights.append("understand your feelings - is the foundation of emotional intelligence.\n\n");
//                insights.append("Pay attention to:\n");
//                insights.append("• What situations or people trigger different emotional responses\n");
//                insights.append("• Patterns in when you feel most positive or challenged\n");
//                insights.append("• How your emotions influence your thoughts and behaviors\n");
//                break;
//
//            case "confidence_esteem":
//                insights.append("💪 SELF-CONFIDENCE & ESTEEM INSIGHTS:\n\n");
//
//                long lowConfidenceCount = emotionCounts.entrySet().stream()
//                        .filter(e -> e.getKey().toLowerCase().contains("low") ||
//                                e.getKey().toLowerCase().contains("poor"))
//                        .mapToLong(Map.Entry::getValue)
//                        .sum();
//
//                if (lowConfidenceCount >= totalResponses * 0.5) {
//                    insights.append("Your responses suggest challenges with self-confidence and self-esteem.\n");
//                    insights.append("This is common and can be improved through consistent practice and,\n");
//                    insights.append("if needed, professional support.\n\n");
//                    insights.append("Remember: Self-worth is inherent, not earned. You are valuable simply\n");
//                    insights.append("because you exist, not because of what you achieve or how others see you.\n");
//                } else {
//                    insights.append("You show awareness of both strengths and areas for growth in self-esteem.\n");
//                    insights.append("Building confidence is a journey that involves practicing self-compassion,\n");
//                    insights.append("celebrating small wins, and challenging negative self-talk.\n");
//                }
//                break;
//
//            case "worklife_burnout":
//                insights.append("⚖️  WORK-LIFE BALANCE & BURNOUT INSIGHTS:\n\n");
//                insights.append("Work-life balance is crucial for sustainable wellbeing and performance.\n");
//                insights.append("Burnout is increasingly common in today's always-connected culture, but\n");
//                insights.append("recognizing the signs is the essential first step toward change.\n\n");
//                insights.append("Key areas to address:\n");
//                insights.append("• Setting clear boundaries between work and personal time\n");
//                insights.append("• Prioritizing rest and recovery as much as productivity\n");
//                insights.append("• Aligning daily activities with your core values\n");
//                insights.append("• Building in regular breaks and disconnection periods\n");
//                break;
//        }
//
//        return insights.toString();
//    }
//}

///////////// third


// package com.mindwell.emotion.backend.service;

// import com.mindwell.emotion.backend.entity.EmotionResponse;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.*;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.HttpClientErrorException;
// import org.springframework.web.client.RestTemplate;

// import java.util.*;
// import java.util.stream.Collectors;

// @Service
// public class EmotionAnalysisService {

//     private static final Logger logger = LoggerFactory.getLogger(EmotionAnalysisService.class);

//     @Value("${gemini.api.key}")
//     private String apiKey;

//     @Value("${gemini.api.url}")
//     private String apiUrl;

//     private final RestTemplate restTemplate = new RestTemplate();

//     /**
//      * Entry point: Generates AI analysis with a retry mechanism for rate limits.
//      */
//     public String generateEmotionAnalysis(List<EmotionResponse> responses) {
//         if (responses == null || responses.isEmpty()) {
//             return "No response data available for analysis.";
//         }

//         try {
//             String prompt = buildEmotionAnalysisPrompt(responses);
//             // Attempt to call API with retry logic for 429 errors
//             String aiAnalysis = callGeminiAPIWithRetry(prompt);

//             if (aiAnalysis != null && !aiAnalysis.trim().isEmpty()) {
//                 logger.info("✅ AI Analysis Generated Successfully for user: {}", responses.get(0).getUsername());
//                 return formatAnalysisResponse(aiAnalysis);
//             }
//         } catch (Exception e) {
//             logger.error("❌ Critical error in emotion analysis flow: {}", e.getMessage());
//         }

//         logger.warn("⚠️ Falling back to manual analysis patterns.");
//         return generateFallbackAnalysis(responses);
//     }

//     /**
//      * Implements Exponential Backoff to handle '429 Too Many Requests'
//      */
//     private String callGeminiAPIWithRetry(String promptText) {
//         int maxRetries = 3;
//         long waitTime = 2000; // Start with 2 seconds

//         for (int i = 0; i < maxRetries; i++) {
//             try {
//                 return callGeminiAPI(promptText);
//             } catch (HttpClientErrorException.TooManyRequests e) {
//                 logger.warn("⚠️ Rate limit (429) hit. Attempt {} of {}. Retrying in {}ms...", i + 1, maxRetries, waitTime);
//                 try {
//                     Thread.sleep(waitTime);
//                     waitTime *= 2; // Exponentially increase wait time
//                 } catch (InterruptedException ie) {
//                     Thread.currentThread().interrupt();
//                 }
//             } catch (Exception e) {
//                 logger.error("📡 API Call failed with non-429 error: {}", e.getMessage());
//                 break;
//             }
//         }
//         return null;
//     }



//     /**
//      * Core API call logic with Safety Settings
//      */
//     private String callGeminiAPI(String promptText) {
//         Map<String, Object> requestBody = new HashMap<>();

//         // 1. Setup Content
//         Map<String, Object> textPart = Collections.singletonMap("text", promptText);
//         Map<String, Object> content = Collections.singletonMap("parts", Collections.singletonList(textPart));
//         requestBody.put("contents", Collections.singletonList(content));

//         // 2. Setup Safety Settings (BLOCK_NONE to allow psychological discussion)
//         List<Map<String, String>> safetySettings = Arrays.asList(
//                 createSafetyMap("HARM_CATEGORY_HARASSMENT"),
//                 createSafetyMap("HARM_CATEGORY_HATE_SPEECH"),
//                 createSafetyMap("HARM_CATEGORY_SEXUALLY_EXPLICIT"),
//                 createSafetyMap("HARM_CATEGORY_DANGEROUS_CONTENT")
//         );
//         requestBody.put("safetySettings", safetySettings);

//         // 3. Setup Config
//         Map<String, Object> generationConfig = new HashMap<>();
//         generationConfig.put("temperature", 0.7);
//         generationConfig.put("maxOutputTokens", 2048);
//         requestBody.put("generationConfig", generationConfig);

//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_JSON);

//         HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
//         String finalUrl = apiUrl + "?key=" + apiKey;

//         ResponseEntity<Map> response = restTemplate.postForEntity(finalUrl, request, Map.class);

//         if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//             return extractTextFromResponse(response.getBody());
//         }
//         return null;
//     }

//     private String extractTextFromResponse(Map<String, Object> body) {
//         try {
//             List<?> candidates = (List<?>) body.get("candidates");
//             if (candidates == null || candidates.isEmpty()) return null;

//             Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
//             Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
//             List<?> parts = (List<?>) content.get("parts");
//             Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);

//             return (String) firstPart.get("text");
//         } catch (Exception e) {
//             logger.error("🧩 JSON Parsing error: {}", e.getMessage());
//             return null;
//         }
//     }

//     private Map<String, String> createSafetyMap(String category) {
//         Map<String, String> map = new HashMap<>();
//         map.put("category", category);
//         map.put("threshold", "BLOCK_NONE");
//         return map;
//     }

//     private String buildEmotionAnalysisPrompt(List<EmotionResponse> responses) {
//         StringBuilder prompt = new StringBuilder();
//         EmotionResponse first = responses.get(0);

//         prompt.append("You are an expert clinical psychologist. Provide a comprehensive, empathetic analysis for: ")
//                 .append(first.getUsername()).append("\n")
//                 .append("Assessment Type: ").append(formatAssessmentType(first.getAssessmentType())).append("\n\n")
//                 .append("USER RESPONSES:\n");

//         for (int i = 0; i < responses.size(); i++) {
//             EmotionResponse r = responses.get(i);
//             prompt.append(i + 1).append(". Question: ").append(r.getQuestionText())
//                     .append("\n   User Answer: ").append(r.getSelectedOption())
//                     .append("\n   Emotional Indicator: ").append(r.getEmotionWeight()).append("\n\n");
//         }

//         prompt.append("STRUCTURE YOUR RESPONSE WITH THESE HEADERS:\n")
//                 .append("🧠 EMOTIONAL STATE OVERVIEW\n")
//                 .append("💭 PSYCHOLOGICAL WELLBEING ASSESSMENT\n")
//                 .append("📊 WELLBEING SCORES (1-10)\n")
//                 .append("🎯 PERSONALIZED RECOMMENDATIONS\n")
//                 .append("💪 STRENGTHS & GROWTH\n")
//                 .append("💚 CLOSING MESSAGE\n\n")
//                 .append("Be direct, compassionate, and reference their specific answers. Write in the second person ('you').");

//         return prompt.toString();
//     }

//     private String formatAssessmentType(String type) {
//         if (type == null) return "GENERAL ASSESSMENT";
//         return type.replace("_", " ").toUpperCase();
//     }

//     private String formatAnalysisResponse(String aiAnalysis) {
//         return "══════════════════════════════════════════════════════\n" +
//                 "           🧠 AI-POWERED EMOTIONAL INSIGHTS\n" +
//                 "══════════════════════════════════════════════════════\n\n" +
//                 aiAnalysis +
//                 "\n\n══════════════════════════════════════════════════════\n" +
//                 "⚠️ DISCLAIMER: This is an AI-generated reflection tool, not a medical diagnosis.";
//     }

//     private String generateFallbackAnalysis(List<EmotionResponse> responses) {
//         Map<String, Long> counts = responses.stream()
//                 .collect(Collectors.groupingBy(EmotionResponse::getEmotionWeight, Collectors.counting()));

//         StringBuilder sb = new StringBuilder("### Assessment Summary (Fallback Mode)\n\n");
//         sb.append("We noticed patterns in your emotional indicators:\n");
//         counts.forEach((k, v) -> sb.append("- ").append(k.replace("_", " ")).append(": ").append(v).append(" responses\n"));
//         sb.append("\nTake time to focus on mindfulness and self-care today. Small steps lead to big changes.");
//         return sb.toString();
//     }
// }







//////////
/// 
/// 
/// 
/// 
/// 
/// 
/// 
/// 
/// 
/// 
///  
/// 
package com.mindwell.emotion.backend.service;

import com.mindwell.emotion.backend.entity.EmotionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmotionAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(EmotionAnalysisService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Entry point: Generates AI analysis with a retry mechanism for rate limits.
     */
    public String generateEmotionAnalysis(List<EmotionResponse> responses) {
        if (responses == null || responses.isEmpty()) {
            return "No response data available for analysis.";
        }

        try {
            String prompt = buildEmotionAnalysisPrompt(responses);
            // Attempt to call API with retry logic for 429 errors
            String aiAnalysis = callGeminiAPIWithRetry(prompt);

            if (aiAnalysis != null && !aiAnalysis.trim().isEmpty()) {
                logger.info("✅ AI Analysis Generated Successfully for user: {}", responses.get(0).getUsername());
                return formatAnalysisResponse(aiAnalysis);
            }
        } catch (Exception e) {
            logger.error("❌ Critical error in emotion analysis flow: {}", e.getMessage());
        }

        logger.warn("⚠️ Falling back to manual analysis patterns.");
        return generateFallbackAnalysis(responses);
    }

    /**
     * Implements Exponential Backoff to handle '429 Too Many Requests'
     */
    private String callGeminiAPIWithRetry(String promptText) {
        int maxRetries = 5; // Increased from 3 to 5 to handle stricter rate limits
        long waitTime = 2000; // Start with 2 seconds

        for (int i = 0; i < maxRetries; i++) {
            try {
                return callGeminiAPI(promptText);
            } catch (HttpClientErrorException.TooManyRequests e) {
                logger.warn("⚠️ Rate limit (429) hit. Attempt {} of {}. Retrying in {}ms...", i + 1, maxRetries,
                        waitTime);
                try {
                    Thread.sleep(waitTime);
                    waitTime *= 2; // Exponentially increase wait time
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().value() == 429) {
                    // Catch 429 if not caught by specific TooManyRequests exception
                    logger.warn("⚠️ Rate limit (429) hit. Attempt {} of {}. Retrying in {}ms...", i + 1, maxRetries,
                            waitTime);
                    try {
                        Thread.sleep(waitTime);
                        waitTime *= 2;
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    logger.error("📡 API Call failed with HTTP error: {}", e.getMessage());
                    break;
                }
            } catch (Exception e) {
                logger.error("📡 API Call failed with non-429 error: {}", e.getMessage());
                break;
            }
        }
        return null; // Return null to trigger fallback
    }

    /**
     * Core API call logic with Safety Settings
     */
    private String callGeminiAPI(String promptText) {
        Map<String, Object> requestBody = new HashMap<>();

        // 1. Setup Content
        Map<String, Object> textPart = Collections.singletonMap("text", promptText);
        Map<String, Object> content = Collections.singletonMap("parts", Collections.singletonList(textPart));
        requestBody.put("contents", Collections.singletonList(content));

        // 2. Setup Safety Settings (BLOCK_NONE to allow psychological discussion)
        List<Map<String, String>> safetySettings = Arrays.asList(
                createSafetyMap("HARM_CATEGORY_HARASSMENT"),
                createSafetyMap("HARM_CATEGORY_HATE_SPEECH"),
                createSafetyMap("HARM_CATEGORY_SEXUALLY_EXPLICIT"),
                createSafetyMap("HARM_CATEGORY_DANGEROUS_CONTENT"));
        requestBody.put("safetySettings", safetySettings);

        // 3. Setup Config
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 2048);
        requestBody.put("generationConfig", generationConfig);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        String finalUrl = apiUrl + "?key=" + apiKey;

        ResponseEntity<Map> response = restTemplate.postForEntity(finalUrl, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return extractTextFromResponse(response.getBody());
        }
        return null;
    }

    private String extractTextFromResponse(Map<String, Object> body) {
        try {
            List<?> candidates = (List<?>) body.get("candidates");
            if (candidates == null || candidates.isEmpty())
                return null;

            Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
            Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
            List<?> parts = (List<?>) content.get("parts");
            Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);

            return (String) firstPart.get("text");
        } catch (Exception e) {
            logger.error("🧩 JSON Parsing error: {}", e.getMessage());
            return null;
        }
    }

    private Map<String, String> createSafetyMap(String category) {
        Map<String, String> map = new HashMap<>();
        map.put("category", category);
        map.put("threshold", "BLOCK_NONE");
        return map;
    }

    private String buildEmotionAnalysisPrompt(List<EmotionResponse> responses) {
        StringBuilder prompt = new StringBuilder();
        EmotionResponse first = responses.get(0);

        prompt.append("You are an expert clinical psychologist. Provide a comprehensive, empathetic analysis for: ")
                .append(first.getUsername()).append("\n")
                .append("Assessment Type: ").append(formatAssessmentType(first.getAssessmentType())).append("\n\n")
                .append("USER RESPONSES:\n");

        for (int i = 0; i < responses.size(); i++) {
            EmotionResponse r = responses.get(i);
            prompt.append(i + 1).append(". Question: ").append(r.getQuestionText())
                    .append("\n   User Answer: ").append(r.getSelectedOption())
                    .append("\n   Emotional Indicator: ").append(r.getEmotionWeight()).append("\n\n");
        }

        prompt.append("STRUCTURE YOUR RESPONSE WITH THESE HEADERS:\n")
                .append("🧠 EMOTIONAL STATE OVERVIEW\n")
                .append("💭 PSYCHOLOGICAL WELLBEING ASSESSMENT\n")
                .append("📊 WELLBEING SCORES (1-10)\n")
                .append("🎯 PERSONALIZED RECOMMENDATIONS\n")
                .append("💪 STRENGTHS & GROWTH\n")
                .append("💚 CLOSING MESSAGE\n\n")
                .append("Be direct, compassionate, and reference their specific answers. Write in the second person ('you').");

        return prompt.toString();
    }

    private String formatAssessmentType(String type) {
        if (type == null)
            return "GENERAL ASSESSMENT";
        return type.replace("_", " ").toUpperCase();
    }

    private String formatAnalysisResponse(String aiAnalysis) {
        return "══════════════════════════════════════════════════════\n" +
                "           🧠 AI-POWERED EMOTIONAL INSIGHTS\n" +
                "══════════════════════════════════════════════════════\n\n" +
                aiAnalysis +
                "\n\n══════════════════════════════════════════════════════\n" +
                "⚠️ DISCLAIMER: This is an AI-generated reflection tool, not a medical diagnosis.";
    }

    private String generateFallbackAnalysis(List<EmotionResponse> responses) {
        Map<String, Long> counts = responses.stream()
                .collect(Collectors.groupingBy(EmotionResponse::getEmotionWeight, Collectors.counting()));

        StringBuilder sb = new StringBuilder("### Assessment Summary (Fallback Mode)\n\n");
        sb.append("We noticed patterns in your emotional indicators:\n");
        counts.forEach(
                (k, v) -> sb.append("- ").append(k.replace("_", " ")).append(": ").append(v).append(" responses\n"));
        sb.append("\nTake time to focus on mindfulness and self-care today. Small steps lead to big changes.");
        return sb.toString();
    }
}