package com.cams.grade_service.dto;

import com.cams.grade_service.model.GroupMember;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupMemberResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private GroupMember.MemberRole role;
    private LocalDateTime joinedAt;
}