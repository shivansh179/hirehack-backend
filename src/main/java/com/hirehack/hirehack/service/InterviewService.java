package com.hirehack.hirehack.service;

import com.hirehack.hirehack.dto.InterviewRequestDto;
import com.hirehack.hirehack.dto.InterviewResponseDto;
import com.hirehack.hirehack.entity.ChatMessage;
import com.hirehack.hirehack.entity.Interview;
import com.hirehack.hirehack.entity.User;
import com.hirehack.hirehack.repository.ChatMessageRepository;
import com.hirehack.hirehack.repository.InterviewRepository;
import com.hirehack.hirehack.repository.UserRepository;
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
public class InterviewService {

    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final GeminiService geminiService;

    @Transactional
    public InterviewResponseDto startNewInterview(InterviewRequestDto requestDto) {
        User user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber())
                .orElseThrow(() -> new EntityNotFoundException("User not found with phone number: " + requestDto.getPhoneNumber()));

        Interview interview = new Interview();
        interview.setUser(user);
        interview.setRole(requestDto.getRole());
        interview.setSkills(requestDto.getSkills());
        interview.setInterviewType(requestDto.getInterviewType());
        interview.setInterviewDurationMinutes(requestDto.getInterviewDurationMinutes());
        interview.setStatus("STARTED");
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
    public String handleUserResponse(Long interviewId, String userMessage) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new EntityNotFoundException("Interview not found with ID: " + interviewId));

        if ("COMPLETED".equals(interview.getStatus())) {
            return "INTERVIEW_ENDED: This interview has already been completed.";
        }

        long minutesElapsed = ChronoUnit.MINUTES.between(interview.getCreatedAt(), OffsetDateTime.now());

        saveChatMessage(interview, ChatMessage.SenderType.USER, userMessage);

        if (minutesElapsed >= interview.getInterviewDurationMinutes()) {
            interview.setStatus("COMPLETED");
            interview.setEndedAt(OffsetDateTime.now());
            interviewRepository.save(interview);
            
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
    public String generateFeedback(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new EntityNotFoundException("Interview not found with ID: " + interviewId));
        
        if (!"COMPLETED".equals(interview.getStatus())) {
             interview.setStatus("COMPLETED");
             interview.setEndedAt(OffsetDateTime.now());
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
    public List<Interview> getInterviewHistoryForUser(String phoneNumber) {
        return interviewRepository.findByUserPhoneNumberOrderByCreatedAtDesc(phoneNumber);
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