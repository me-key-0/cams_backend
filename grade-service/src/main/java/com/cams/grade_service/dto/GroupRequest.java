package com.cams.grade_service.dto;

import com.cams.grade_service.model.StudentGroup;
import lombok.Data;

import java.util.List;

@Data
public class GroupRequest {
    private String name;
    private String description;
    private Long courseSessionId;
    private StudentGroup.GroupType type;
    private List<GroupMemberRequest> members;
}