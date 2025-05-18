package com.cams.resource_service.exception;

public class CourseSessionNotFoundException extends RuntimeException {
    public CourseSessionNotFoundException(String message) {
        super(message);
    }
} 