package com.cams.course_service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cams.course_service.client.UserServiceClient;
import com.cams.course_service.dto.CourseDto;
import com.cams.course_service.dto.CourseSessionDto;
import com.cams.course_service.dto.LecturerDto;
import com.cams.course_service.model.CourseSession;
import com.cams.course_service.model.Enrollment;
import com.cams.course_service.repository.CourseSessionRepository;
import com.cams.course_service.repository.EnrollmentRepository;

@Service
public class EnrollmentService implements com.cams.course_service.service.EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseSessionRepository courseSessionRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    private static final String LECTURER_SEPARATOR = " | ";

    @Override
    public List<CourseSessionDto> getCourseSessions(Long studentId, Integer year, Integer semester, Integer academicYear) {
        List<CourseSession> css = enrollmentRepository.findMatchingCourseSessions(studentId, year, semester, academicYear);

         return css.stream()
                .map(cs -> {
                    try {
                        return mapToDTO(cs);
                    } catch (Exception e) {
                        System.err.println("Error mapping course session to DTO: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseSessionDto> getCourseSessionsByStudent(Long studentId) {
        try {
            System.out.println("Fetching enrollments for student ID: " + studentId);
            List<Enrollment> sessions = enrollmentRepository.findByStudentId(studentId);
            System.out.println("Found " + (sessions != null ? sessions.size() : "null") + " enrollments");

            if (sessions == null || sessions.isEmpty()) {
                System.out.println("No enrollments found for student");
                return List.of();
            }

            List<CourseSession> css = sessions.stream()
                .map(e -> {
                    CourseSession cs = e.getCourseSession();
                    System.out.println("Mapping enrollment to course session: " + 
                        (cs != null ? "ID=" + cs.getId() : "null"));
                    return cs;
                })
                .filter(cs -> cs != null)
                .collect(Collectors.toList());

            System.out.println("Mapped to " + css.size() + " course sessions");

            return css.stream()
                .map(cs -> {
                    try {
                        return mapToDTO(cs);
                    } catch (Exception e) {
                        System.err.println("Error mapping course session to DTO: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in getCourseSessionsByStudent: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public Enrollment enrollStudent(Long studentId, Long courseSessionId) {
        // Check if course session exists and is active with enrollment open
        CourseSession courseSession = courseSessionRepository.findById(courseSessionId)
            .orElseThrow(() -> new IllegalArgumentException("Course session not found"));
        
        if (!courseSession.getIsActive()) {
            throw new IllegalStateException("Course session is not active");
        }
        
        if (!courseSession.getEnrollmentOpen()) {
            throw new IllegalStateException("Enrollment is not open for this course session");
        }
        
        // Check if student is already enrolled
        Optional<Enrollment> existingEnrollment = enrollmentRepository.findAll().stream()
            .filter(e -> e.getStudentId().equals(studentId) && 
                   e.getCourseSession().getId().equals(courseSessionId))
            .findFirst();
        
        if (existingEnrollment.isPresent()) {
            throw new IllegalStateException("Student is already enrolled in this course session");
        }
        
        // Create enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseSession(courseSession);
        enrollment.setEnrollmentDate(LocalDateTime.now().toString());
        enrollment.setIsActive(true);
        
        return enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional
    public void unenrollStudent(Long studentId, Long courseSessionId) {
        // Find enrollment
        Optional<Enrollment> existingEnrollment = enrollmentRepository.findAll().stream()
            .filter(e -> e.getStudentId().equals(studentId) && 
                   e.getCourseSession().getId().equals(courseSessionId))
            .findFirst();
        
        if (existingEnrollment.isEmpty()) {
            throw new IllegalArgumentException("Student is not enrolled in this course session");
        }
        
        // Delete enrollment
        enrollmentRepository.delete(existingEnrollment.get());
    }

    @Override
    public boolean isStudentEnrolled(Long studentId, Long courseSessionId) {
        return enrollmentRepository.findAll().stream()
            .anyMatch(e -> e.getStudentId().equals(studentId) && 
                    e.getCourseSession().getId().equals(courseSessionId) &&
                    e.getIsActive());
    }

    @Override
    public List<Long> getEnrolledStudents(Long courseSessionId) {
        return enrollmentRepository.findByCourseSessionId(courseSessionId).stream()
            .map(Enrollment::getStudentId)
            .collect(Collectors.toList());
    }

    private CourseSessionDto mapToDTO(CourseSession cs) {
        CourseDto courseDTO = CourseDto.builder()
            .code(cs.getCourse().getCode())
            .name(cs.getCourse().getName())
            .creditHour(cs.getCourse().getCreditHour())
            .build();

        System.out.println("Mapping course session " + cs.getId() + " with lecturer IDs: " + cs.getLecturerId());

        // Fetch lecturer details from user-service for each lecturerId
        String lecturerName = cs.getLecturerId().stream()
            .map(id -> {
                try {
                    System.out.println("Fetching lecturer details for ID: " + id);
                    LecturerDto lecturer = userServiceClient.getLecturerById(id);
                    System.out.println("Received lecturer response: " + (lecturer != null ? lecturer : "null"));
                    if (lecturer != null) {
                        String fullName = lecturer.getFullName();
                        System.out.println("Got lecturer name: " + fullName);
                        return fullName;
                    }
                    System.out.println("Lecturer DTO was null");
                    return "N/A";
                } catch (Exception e) {
                    System.err.println("Error fetching lecturer " + id + ": " + e.getMessage());
                    e.printStackTrace();
                    return "N/A";
                }
            })
            .filter(name -> !name.equals("N/A"))
            .collect(Collectors.joining(LECTURER_SEPARATOR));

        // If no valid lecturer names were found, set to N/A
        if (lecturerName.isEmpty()) {
            System.out.println("No valid lecturer names found for course session " + cs.getId());
            lecturerName = "N/A";
        } else {
            System.out.println("Final lecturer names for course session " + cs.getId() + ": " + lecturerName);
        }

        return CourseSessionDto.builder()
            .id(cs.getId())
            .year(cs.getYear())
            .semester(cs.getSemester())
            .academicYear(cs.getAcademicYear())
            .course(courseDTO)
            .lecturerName(lecturerName)
            .build();
    }
}