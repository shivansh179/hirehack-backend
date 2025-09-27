package com.hirehack.hirehack.service.interfaces;

import com.hirehack.hirehack.dto.InterviewRequestDto;
import com.hirehack.hirehack.dto.InterviewResponseDto;
import com.hirehack.hirehack.entity.Interview;

import java.util.List;

/**
 * Interface for interview service operations.
 * Provides abstraction for interview management, question generation, and feedback.
 */
public interface InterviewServiceInterface {
    
    /**
     * Starts a new interview session for a user.
     *
     * @param requestDto the interview request containing role, skills, and configuration
     * @param userId the user's unique identifier
     * @return interview response with interview ID and initial question
     * @throws jakarta.persistence.EntityNotFoundException if user not found
     */
    InterviewResponseDto startNewInterview(InterviewRequestDto requestDto, Long userId);
    
    /**
     * Handles user response during an interview and generates the next question.
     *
     * @param interviewId the interview's unique identifier
     * @param userMessage the user's response message
     * @param userId the user's unique identifier
     * @return the next AI question or interview completion message
     * @throws jakarta.persistence.EntityNotFoundException if interview not found or access denied
     */
    String handleUserResponse(Long interviewId, String userMessage, Long userId);
    
    /**
     * Generates feedback for a completed interview.
     *
     * @param interviewId the interview's unique identifier
     * @param userId the user's unique identifier
     * @return the generated feedback in markdown format
     * @throws jakarta.persistence.EntityNotFoundException if interview not found or access denied
     */
    String generateFeedback(Long interviewId, Long userId);
    
    /**
     * Retrieves interview history for a specific user.
     *
     * @param userId the user's unique identifier
     * @return list of interviews ordered by creation date (most recent first)
     */
    List<Interview> getInterviewHistoryForUser(Long userId);
}
