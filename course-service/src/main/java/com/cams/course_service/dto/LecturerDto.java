package com.cams.course_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LecturerDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String department;

    public String getFullName() {
        return firstName + " " + lastName;
    }
} 