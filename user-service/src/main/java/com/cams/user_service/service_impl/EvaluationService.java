package com.cams.user_service.service_impl;

import com.cams.user_service.dto.ConfirmationDto;
import com.cams.user_service.dto.EvaluationAnswerDto;
import com.cams.user_service.dto.EvaluationRequestDto;
import com.cams.user_service.model.*;
import com.cams.user_service.repository.*;
import com.cams.user_service.service.LecturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    private final String sessionNotFoundMessage = "Evaluation Session not found";

    @Override
    public ConfirmationDto activateEvalSession(Long sessionId) {
        Optional<EvaluationSession> sessionOpt = evaluationSessionRepository.findById(sessionId);
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
    public ConfirmationDto submitEvaluation(Long studentId, EvaluationRequestDto request) {
        Optional<Student> studentOpt = studentService.getStudentById(studentId);
        if (studentOpt.isEmpty()) {
            return new ConfirmationDto(false, "Student not found");
        }
        Optional<Lecturer> lecturerOpt = lecturerService.getLecturerById(request.getLecturerId());
       if (lecturerOpt.isEmpty()) {
           return new ConfirmationDto(false,"Lecturer not found");
       }

       Student student = studentOpt.get();
       Lecturer lecturer = lecturerOpt.get();

        if (request.getAnswers() != null){
            Evaluation evaluation = new Evaluation();
            evaluation.setStudent(student);
            evaluation.setLecturer(lecturer);

            for (EvaluationAnswerDto answerDto : request.getAnswers()) {
                EvaluationQuestion question =  evaluationQuestionRepository.findById(answerDto.getQuestionId())
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
            ConfirmationDto response = new ConfirmationDto();
            response.setSuccess(true);
            response.setMessage("Evaluation Submitted Successfully!");
            return response;
        }
        ConfirmationDto response = new ConfirmationDto();
        response.setSuccess(false);
        response.setMessage("Evaluation Submission has failed!");

        return response;
    }


    @Override
    public List<EvaluationQuestion> getEvaluationQuestions() {
        return evaluationQuestionRepository.findAll();
    }

}
