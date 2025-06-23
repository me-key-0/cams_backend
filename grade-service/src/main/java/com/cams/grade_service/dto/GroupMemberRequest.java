package com.cams.grade_service.dto;

import com.cams.grade_service.model.GroupMember;
import lombok.Data;

@Data
public class GroupMemberRequest {
    private Long studentId;
    private String studentName;
    private GroupMember.MemberRole role;
}