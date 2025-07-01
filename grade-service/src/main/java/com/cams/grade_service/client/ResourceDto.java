package com.cams.grade_service.client;

import lombok.Data;

@Data
public class ResourceDto {
    private Long id;
    private String title;
    private String fileName;
    private String originalFileName;
    private String type;
    private Long fileSize;
    private String downloadUrl;
    private String fileSizeFormatted;
}