package com.cams.course_service.service;

import java.util.List;
import com.cams.course_service.model.CourseSession;

public interface AssignmentService {
    List<CourseSession> getCourseSessions(Long lectureId);
}
