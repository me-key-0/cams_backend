package com.cams.user_service.service_impl;

import com.cams.user_service.client.CourseServiceClient;
import com.cams.user_service.dto.*;
import com.cams.user_service.model.*;
import com.cams.user_service.repository.*;
import com.cams.user_service.service.LecturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EvaluationService implements com.cams.user_service.service.EvaluationService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private LecturerService lecturerService;

    @Autowired
    private EvaluationQuestionRepository evaluationQuestionRepository;

    @Autowired
    private EvaluationOptionRepository evaluationOptionRepository;

    @Autowired
    private EvaluationSessionRepository evaluationSessionRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Autowired
    private EvaluationCategoryRepository evaluationCategoryRepository;
    
    @Autowired
    private CourseServiceClient courseServiceClient;

    private final String sessionNotFoundMessage = "Evaluation Session not found";

    @Override
    @Transactional
    public ConfirmationDto activateEvalSession(Long sessionId) {
        Optional<EvaluationSession> sessionOpt = evaluationSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return new ConfirmationDto(false, sessionNotFoundMessage);
        }
        
        EvaluationSession session = sessionOpt.get();
        ConfirmationDto dto = isEvalSessionActivated(sessionId);

        ConfirmationDto response = new ConfirmationDto();
        if (dto.getSuccess()) {
            session.setActive(false);
            response.setSuccess(true);
            response.setMessage("Session Deactivated Successfully");
        } else if (Objects.equals(dto.getMessage(), sessionNotFoundMessage)) {
            response.setSuccess(false);
            response.setMessage("Session Does not Exist");
        } else {
            session.setActive(true);
            response.setSuccess(true);
            response.setMessage("Session Activated Successfully");
        }
        evaluationSessionRepository.save(session);
        return response;
    }

    @Override
    public ConfirmationDto isEvalSessionActivated(Long sessionId) {
        Optional<EvaluationSession> sessionOpt = evaluationSessionRepository.findById(sessionId);
        ConfirmationDto response = new ConfirmationDto();

        if (sessionOpt.isPresent()) {
            EvaluationSession session = sessionOpt.get();
            boolean active = session.isActive();

            response.setSuccess(active);
            response.setMessage(active ? "Evaluation Session is Active" : "Evaluation Session is Inactive");
        } else {
            response.setSuccess(false);
            response.setMessage(sessionNotFoundMessage);
        }
        return response;
    }
    
    @Override
    @Transactional
    public EvaluationSessionDto createEvaluationSession(CreateEvaluationSessionRequest request, Long adminId) {
        // Validate course session exists
        boolean courseSessionExists = courseServiceClient.checkCourseSessionExists(request.getCourseSessionId());
        if (!courseSessionExists) {
            throw new IllegalArgumentException("Course session does not exist");
        }
        
        // Check if evaluation session already exists for this course session
        Optional<EvaluationSession> existingSession = evaluationSessionRepository.findByCourseSessionId(request.getCourseSessionId());
        if (existingSession.isPresent()) {
            throw new IllegalArgumentException("Evaluation session already exists for this course session");
        }
        
        EvaluationSession session = EvaluationSession.builder()
            .courseSessionId(request.getCourseSessionId())
            .isActive(false)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .departmentId(request.getDepartmentId())
            .activatedBy(adminId)
            .build();
        
        EvaluationSession savedSession = evaluationSessionRepository.save(session);
        
        return EvaluationSessionDto.builder()
            .id(savedSession.getId())
            .courseSessionId(savedSession.getCourseSessionId())
            .isActive(savedSession.isActive())
            .startDate(savedSession.getStartDate())
            .endDate(savedSession.getEndDate())
            .departmentId(savedSession.getDepartmentId())
            .activatedBy(savedSession.getActivatedBy())
            .build();
    }
    
    @Override
    public List<EvaluationSessionDto> getEvaluationSessionsByDepartment(Long departmentId) {
        List<EvaluationSession> sessions = evaluationSessionRepository.findByDepartmentId(departmentId);
        
        return sessions.stream()
            .map(session -> EvaluationSessionDto.builder()
                .id(session.getId())
                .courseSessionId(session.getCourseSessionId())
                .isActive(session.isActive())
                .startDate(session.getStartDate())
                .endDate(session.getEndDate())
                .departmentId(session.getDepartmentId())
                .activatedBy(session.getActivatedBy())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConfirmationDto submitEvaluation(Long studentId, EvaluationRequestDto request) {
        Optional<Student> studentOpt = studentService.getStudentById(studentId);
        if (studentOpt.isEmpty()) {
            return new ConfirmationDto(false, "Student not found");
        }
        
        Optional<Lecturer> lecturerOpt = lecturerService.getLecturerById(request.getLecturerId());
        if (lecturerOpt.isEmpty()) {
            return new ConfirmationDto(false, "Lecturer not found");
        }

        // Validate course session and check if student is enrolled
        Optional<EvaluationSession> sessionOpt = evaluationSessionRepository.findByCourseSessionId(request.getCourseSessionId());
        if (sessionOpt.isEmpty()) {
            return new ConfirmationDto(false, "Evaluation session not found for this course");
        }
        
        EvaluationSession session = sessionOpt.get();
        if (!session.isActive()) {
            return new ConfirmationDto(false, "Evaluation session is not active");
        }
        
        // Check if student is enrolled in the course
        boolean isEnrolled = courseServiceClient.isStudentEnrolled(studentId, session.getCourseSessionId());
        if (!isEnrolled) {
            return new ConfirmationDto(false, "Student is not enrolled in this course");
        }
        
        // Check if lecturer is assigned to the course
        boolean isLecturerAssigned = courseServiceClient.validateLecturerForCourseSession(
            request.getLecturerId(), session.getCourseSessionId());
        if (!isLecturerAssigned) {
            return new ConfirmationDto(false, "Lecturer is not assigned to this course");
        }

        Student student = studentOpt.get();
        Lecturer lecturer = lecturerOpt.get();

        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            return new ConfirmationDto(false, "No evaluation answers provided");
        }
        
        // Check if student has already submitted an evaluation for this lecturer in this session
        List<Evaluation> existingEvaluations = evaluationRepository.findByStudentId(studentId);
        boolean alreadySubmitted = existingEvaluations.stream()
            .anyMatch(e -> e.getLecturer().getId().equals(request.getLecturerId()) && 
                     e.getSession().getId().equals(session.getId()));
        
        if (alreadySubmitted) {
            return new ConfirmationDto(false, "You have already submitted an evaluation for this lecturer");
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setStudent(student);
        evaluation.setLecturer(lecturer);
        evaluation.setSession(session);

        for (EvaluationAnswerDto answerDto : request.getAnswers()) {
            EvaluationQuestion question = evaluationQuestionRepository.findById(answerDto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            EvaluationOption answer = evaluationOptionRepository.findById(answerDto.getAnswerId())
                    .orElseThrow(() -> new RuntimeException("Answer option not found"));

            EvaluationAnswer evaluationAnswer = new EvaluationAnswer();
            evaluationAnswer.setAnswer(answer);
            evaluationAnswer.setQuestion(question);
            evaluationAnswer.setEvaluation(evaluation);
            evaluation.getAnswers().add(evaluationAnswer);
        }
        
        evaluationRepository.save(evaluation);
        
        return new ConfirmationDto(true, "Evaluation Submitted Successfully!");
    }

    @Override
    public List<EvaluationQuestion> getEvaluationQuestions() {
        return evaluationQuestionRepository.findAll();
    }
    
    @Override
    public List<EvaluationQuestionDto> getEvaluationQuestionsByCategory(Long categoryId) {
        List<EvaluationQuestion> questions = evaluationQuestionRepository.findByCategoryId(categoryId);
        
        return questions.stream()
            .map(q -> EvaluationQuestionDto.builder()
                .id(q.getId())
                .question(q.getQuestion())
                .categoryId(q.getCategory().getId())
                .categoryName(q.getCategory().getName())
                .build())
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EvaluationCategoryDto> getEvaluationCategories() {
        List<EvaluationCategory> categories = evaluationCategoryRepository.findAll();
        
        return categories.stream()
            .map(c -> EvaluationCategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .build())
            .collect(Collectors.toList());
    }
    
    @Override
    public EvaluationAnalyticsResponse getEvaluationAnalytics(Long courseSessionId, Long lecturerId) {
        // Get the evaluation session for this course
        Optional<EvaluationSession> sessionOpt = evaluationSessionRepository.findByCourseSessionId(courseSessionId);
        if (sessionOpt.isEmpty()) {
            throw new IllegalArgumentException("No evaluation session found for this course");
        }
        
        EvaluationSession session = sessionOpt.get();
        
        // Get lecturer details
        Optional<Lecturer> lecturerOpt = lecturerService.getLecturerById(lecturerId);
        if (lecturerOpt.isEmpty()) {
            throw new IllegalArgumentException("Lecturer not found");
        }
        
        Lecturer lecturer = lecturerOpt.get();
        
        // Get all evaluations for this lecturer in this session
        List<Evaluation> evaluations = evaluationRepository.findByLecturerId(lecturerId).stream()
            .filter(e -> e.getSession().getId().equals(session.getId()))
            .collect(Collectors.toList());
        
        if (evaluations.isEmpty()) {
            throw new IllegalArgumentException("No evaluations found for this lecturer in this course");
        }
        
        // Calculate overall rating and prepare analytics
        Map<Long, List<Integer>> questionRatings = new HashMap<>();
        Map<Long, String> questionTexts = new HashMap<>();
        Map<Long, Long> questionCategories = new HashMap<>();
        Map<Long, String> categoryNames = new HashMap<>();
        
        // Process all evaluations and answers
        for (Evaluation evaluation : evaluations) {
            for (EvaluationAnswer answer : evaluation.getAnswers()) {
                Long questionId = answer.getQuestion().getId();
                
                // Store question text and category
                questionTexts.putIfAbsent(questionId, answer.getQuestion().getQuestion());
                
                EvaluationCategory category = answer.getQuestion().getCategory();
                if (category != null) {
                    questionCategories.putIfAbsent(questionId, category.getId());
                    categoryNames.putIfAbsent(category.getId(), category.getName());
                }
                
                // Parse rating value from option
                int rating = Integer.parseInt(answer.getAnswer().getValue());
                
                // Add to ratings list
                questionRatings.computeIfAbsent(questionId, k -> new ArrayList<>()).add(rating);
            }
        }
        
        // Calculate question analytics
        List<EvaluationAnalyticsResponse.QuestionAnalytics> questionAnalyticsList = new ArrayList<>();
        Map<Long, List<Double>> categoryAverages = new HashMap<>();
        
        for (Map.Entry<Long, List<Integer>> entry : questionRatings.entrySet()) {
            Long questionId = entry.getKey();
            List<Integer> ratings = entry.getValue();
            
            // Calculate average rating
            double averageRating = ratings.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
            
            // Calculate rating distribution
            Map<String, Integer> distribution = new HashMap<>();
            for (int i = 1; i <= 5; i++) {
                final int rating = i;
                distribution.put(String.valueOf(i), 
                    (int) ratings.stream().filter(r -> r == rating).count());
            }
            
            // Get category
            Long categoryId = questionCategories.get(questionId);
            String category = categoryId != null ? categoryNames.get(categoryId) : "Uncategorized";
            
            // Add to category averages
            if (categoryId != null) {
                categoryAverages.computeIfAbsent(categoryId, k -> new ArrayList<>()).add(averageRating);
            }
            
            // Create question analytics
            EvaluationAnalyticsResponse.QuestionAnalytics questionAnalytics = 
                EvaluationAnalyticsResponse.QuestionAnalytics.builder()
                    .questionId(questionId)
                    .question(questionTexts.get(questionId))
                    .category(category)
                    .averageRating(averageRating)
                    .ratingDistribution(distribution)
                    .build();
            
            questionAnalyticsList.add(questionAnalytics);
        }
        
        // Calculate category ratings
        Map<String, Double> categoryRatings = new HashMap<>();
        for (Map.Entry<Long, List<Double>> entry : categoryAverages.entrySet()) {
            Long categoryId = entry.getKey();
            List<Double> averages = entry.getValue();
            
            double categoryAverage = averages.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
            
            categoryRatings.put(categoryNames.get(categoryId), categoryAverage);
        }
        
        // Calculate overall rating
        double overallRating = questionAnalyticsList.stream()
            .mapToDouble(EvaluationAnalyticsResponse.QuestionAnalytics::getAverageRating)
            .average()
            .orElse(0.0);
        
        // Build response
        return EvaluationAnalyticsResponse.builder()
            .lecturerId(lecturerId)
            .lecturerName(lecturer.getUser().getFirstname() + " " + lecturer.getUser().getLastname())
            .courseSessionId(courseSessionId)
            .courseCode("CS101") // This should be fetched from course service
            .courseName("Programming Fundamentals") // This should be fetched from course service
            .totalSubmissions(evaluations.size())
            .overallRating(overallRating)
            .categoryRatings(categoryRatings)
            .questionAnalytics(questionAnalyticsList)
            .build();
    }
    
    @Override
    public List<EvaluationAnalyticsResponse> getLecturerEvaluationAnalytics(Long lecturerId) {
        // Get all evaluations for this lecturer
        List<Evaluation> evaluations = evaluationRepository.findByLecturerId(lecturerId);
        
        if (evaluations.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Group evaluations by course session
        Map<Long, List<Evaluation>> evaluationsByCourseSession = evaluations.stream()
            .collect(Collectors.groupingBy(e -> e.getSession().getCourseSessionId()));
        
        // Generate analytics for each course session
        List<EvaluationAnalyticsResponse> results = new ArrayList<>();
        for (Map.Entry<Long, List<Evaluation>> entry : evaluationsByCourseSession.entrySet()) {
            Long courseSessionId = entry.getKey();
            
            try {
                EvaluationAnalyticsResponse analytics = getEvaluationAnalytics(courseSessionId, lecturerId);
                results.add(analytics);
            } catch (Exception e) {
                // Skip if analytics generation fails for a course
                continue;
            }
        }
        
        return results;
    }
    
    @Override
    public List<EvaluationAnalyticsResponse> getDepartmentEvaluationAnalytics(Long departmentId) {
        // Get all evaluation sessions for this department
        List<EvaluationSession> sessions = evaluationSessionRepository.findByDepartmentId(departmentId);
        
        if (sessions.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Generate analytics for each session and lecturer combination
        List<EvaluationAnalyticsResponse> results = new ArrayList<>();
        
        for (EvaluationSession session : sessions) {
            // Get all evaluations for this session
            List<Evaluation> sessionEvaluations = session.getEvaluations();
            
            // Group by lecturer
            Map<Long, List<Evaluation>> evaluationsByLecturer = sessionEvaluations.stream()
                .collect(Collectors.groupingBy(e -> e.getLecturer().getId()));
            
            // Generate analytics for each lecturer
            for (Long lecturerId : evaluationsByLecturer.keySet()) {
                try {
                    EvaluationAnalyticsResponse analytics = 
                        getEvaluationAnalytics(session.getCourseSessionId(), lecturerId);
                    results.add(analytics);
                } catch (Exception e) {
                    // Skip if analytics generation fails
                    continue;
                }
            }
        }
        
        return results;
    }
}