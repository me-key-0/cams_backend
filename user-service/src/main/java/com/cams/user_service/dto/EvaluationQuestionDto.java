package com.cams.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationQuestionDto {
    private Long id;
    private String question;
    private Long categoryId;
    private String categoryName;
}