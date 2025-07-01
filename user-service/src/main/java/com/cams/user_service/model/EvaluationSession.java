package com.cams.user_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long courseSessionId;
    private boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long departmentId;
    private Long activatedBy; // Admin ID who activated the session

    @OneToMany(mappedBy = "session")
    private List<Evaluation> evaluations;
}