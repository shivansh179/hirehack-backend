package com.hirehack.hirehack.service;

import com.hirehack.hirehack.dto.InterviewRequestDto;
import com.hirehack.hirehack.dto.InterviewResponseDto;
import com.hirehack.hirehack.entity.ChatMessage;
import com.hirehack.hirehack.entity.Interview;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.factory.InterviewFactory;
import com.hirehack.hirehack.observer.EventPublisher;
import com.hirehack.hirehack.observer.InterviewCompletedEvent;
import com.hirehack.hirehack.repository.ChatMessageRepository;
import com.hirehack.hirehack.repository.InterviewRepository;
import com.hirehack.hirehack.repository.UserRepository;
import com.hirehack.hirehack.service.interfaces.InterviewServiceInterface;

import java.util.Map;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewService implements InterviewServiceInterface {

    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final GeminiService geminiService;
    private final InterviewFactory interviewFactory;
    private final EventPublisher eventPublisher;

    @Transactional
    public InterviewResponseDto startNewInterview(InterviewRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Interview interview = interviewFactory.createInterviewFromRequest(requestDto, user);
        interview = interviewRepository.save(interview);

        String firstQuestion = geminiService.generateInitialQuestion(
                user,
                interview.getRole(),
                interview.getSkills(),
                interview.getInterviewType()
        );

        saveChatMessage(interview, ChatMessage.SenderType.AI, firstQuestion);
        
        InterviewResponseDto responseDto = new InterviewResponseDto();
        responseDto.setInterviewId(interview.getId());
        responseDto.setInitialQuestion(firstQuestion);
        return responseDto;
    }

    @Transactional
    public String handleUserResponse(Long interviewId, String userMessage, Long userId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new EntityNotFoundException("Interview not found with ID: " + interviewId));

        // Verify that the interview belongs to the authenticated user
        if (!interview.getUser().getId().equals(userId)) {
            throw new EntityNotFoundException("Interview not found or access denied");
        }

        if ("COMPLETED".equals(interview.getStatus())) {
            return "INTERVIEW_ENDED: This interview has already been completed.";
        }

        long minutesElapsed = ChronoUnit.MINUTES.between(interview.getCreatedAt(), OffsetDateTime.now());

        saveChatMessage(interview, ChatMessage.SenderType.USER, userMessage);

        if (minutesElapsed >= interview.getInterviewDurationMinutes()) {
            interview.setStatus("COMPLETED");
            interview.setEndedAt(OffsetDateTime.now());
            interviewRepository.save(interview);
            
            // Publish interview completion event
            eventPublisher.publishEvent(new InterviewCompletedEvent(interview, Map.of(
                    "completionReason", "TIME_LIMIT_REACHED",
                    "durationMinutes", minutesElapsed,
                    "timestamp", interview.getEndedAt()
            )));
            
            String finalMessage = "Thank you for your time. The interview is now complete.";
            saveChatMessage(interview, ChatMessage.SenderType.AI, finalMessage);
            return "INTERVIEW_ENDED: " + finalMessage;
        }

        String chatHistory = getFormattedChatHistory(interviewId);
        String resumeText = interview.getUser().getResumeText() != null ? interview.getUser().getResumeText() : "No resume was provided.";

        String nextAiQuestion = geminiService.generateNextQuestion(
                interview.getRole(),
                interview.getSkills(),
                interview.getInterviewType(),
                resumeText,
                chatHistory
        );

        saveChatMessage(interview, ChatMessage.SenderType.AI, nextAiQuestion);

        return nextAiQuestion;
    }

    @Transactional
    public String generateFeedback(Long interviewId, Long userId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new EntityNotFoundException("Interview not found with ID: " + interviewId));
        
        // Verify that the interview belongs to the authenticated user
        if (!interview.getUser().getId().equals(userId)) {
            throw new EntityNotFoundException("Interview not found or access denied");
        }
        
        if (!"COMPLETED".equals(interview.getStatus())) {
             interview.setStatus("COMPLETED");
             interview.setEndedAt(OffsetDateTime.now());
             
             // Publish interview completion event
             eventPublisher.publishEvent(new InterviewCompletedEvent(interview, Map.of(
                     "completionReason", "MANUAL_COMPLETION",
                     "timestamp", interview.getEndedAt()
             )));
        }

        User user = interview.getUser();
        String chatHistory = getFormattedChatHistory(interviewId);
        String resumeText = user.getResumeText() != null ? user.getResumeText() : "No resume was provided.";

        String feedback = geminiService.generateFeedback(
                chatHistory,
                resumeText,
                interview.getRole(),
                interview.getSkills()
        );

        interview.setFeedback(feedback);
        interviewRepository.save(interview);
        return feedback;
    }

    @Transactional(readOnly = true)
    public List<Interview> getInterviewHistoryForUser(Long userId) {
        return interviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    private void saveChatMessage(Interview interview, ChatMessage.SenderType sender, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setInterview(interview);
        chatMessage.setSenderType(sender);
        chatMessage.setMessageText(message);
        chatMessageRepository.save(chatMessage);
    }

    private String getFormattedChatHistory(Long interviewId) {
        List<ChatMessage> messages = chatMessageRepository.findByInterviewIdOrderByCreatedAtAsc(interviewId);
        return messages.stream()
                .map(msg -> msg.getSenderType().name() + ": " + msg.getMessageText())
                .collect(Collectors.joining("\n"));
    }
}