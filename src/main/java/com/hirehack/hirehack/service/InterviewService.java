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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + requestDto.getPhoneNumber()));

        Interview interview = new Interview();
        interview.setUser(user);
        // Assuming these new fields are now in InterviewRequestDto
        interview.setRole(requestDto.getRole());
        interview.setSkills(requestDto.getSkills());
        interview.setInterviewType(requestDto.getInterviewType());
        interview.setInterviewDurationMinutes(requestDto.getInterviewDurationMinutes());
        interview.setStatus("STARTED");
        interview = interviewRepository.save(interview);

        // Generate a much more detailed first question
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

        // --- TIME CHECK LOGIC --- (This part is correct)
        long minutesElapsed = ChronoUnit.MINUTES.between(interview.getCreatedAt(), OffsetDateTime.now());

        if (minutesElapsed >= interview.getInterviewDurationMinutes()) {
            interview.setStatus("COMPLETED");
            interview.setEndedAt(OffsetDateTime.now());
            interviewRepository.save(interview);
            saveChatMessage(interview, ChatMessage.SenderType.USER, userMessage);
            String finalMessage = "Thank you for your time. The interview is now complete.";
            saveChatMessage(interview, ChatMessage.SenderType.AI, finalMessage);
            return "INTERVIEW_ENDED: " + finalMessage;
        }
        // --- END OF TIME CHECK ---

        // 1. Save the user's message
        saveChatMessage(interview, ChatMessage.SenderType.USER, userMessage);

        // 2. Get the entire chat history for context
        String chatHistory = getFormattedChatHistory(interviewId);

        // --- THIS IS THE CORRECTED PART ---
        // 3. Generate the next question from Gemini using the FULL context
        String nextAiQuestion = geminiService.generateNextQuestion(
                interview.getRole(),
                interview.getSkills(),
                interview.getInterviewType(),
                interview.getUser().getResumeText(), // Pass the resume text
                chatHistory
        );
        // --- END OF CORRECTION ---

        // 4. Save the AI's new message
        saveChatMessage(interview, ChatMessage.SenderType.AI, nextAiQuestion);

        return nextAiQuestion;
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
                .map(msg -> msg.getSenderType() + ": " + msg.getMessageText())
                .collect(Collectors.joining("\n"));
    }

    @Transactional
    public String generateFeedback(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new EntityNotFoundException("Interview not found: " + interviewId));
        User user = interview.getUser();
        String chatHistory = getFormattedChatHistory(interviewId);

        String feedback = geminiService.generateFeedback(
                chatHistory,
                user.getResumeText(),
                interview.getRole(),
                interview.getSkills()
        );

        interview.setFeedback(feedback);
        interviewRepository.save(interview);
        return feedback;
    }

    public List<Interview> getInterviewHistoryForUser(String phoneNumber) {
        return interviewRepository.findByUserPhoneNumberOrderByCreatedAtDesc(phoneNumber);
    }
}