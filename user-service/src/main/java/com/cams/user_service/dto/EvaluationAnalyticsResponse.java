package com.cams.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationAnalyticsResponse {
    private Long lecturerId;
    private String lecturerName;
    private Long courseSessionId;
    private String courseCode;
    private String courseName;
    private int totalSubmissions;
    private double overallRating;
    
    // Category-wise average ratings
    private Map<String, Double> categoryRatings;
    
    // Question-wise analytics
    private List<QuestionAnalytics> questionAnalytics;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionAnalytics {
        private Long questionId;
        private String question;
        private String category;
        private double averageRating;
        private Map<String, Integer> ratingDistribution; // e.g., {"1": 5, "2": 10, ...}
    }
}