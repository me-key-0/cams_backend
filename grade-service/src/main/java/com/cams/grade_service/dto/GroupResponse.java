package com.cams.grade_service.dto;

import com.cams.grade_service.model.StudentGroup;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupResponse {
    private Long id;
    private String name;
    private String description;
    private Long courseSessionId;
    private Long createdBy;
    private String createdByName;
    private StudentGroup.GroupType type;
    private LocalDateTime createdAt;
    private Boolean isActive;
    private List<GroupMemberResponse> members;
    private Integer memberCount;
}