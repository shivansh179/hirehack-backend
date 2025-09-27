package com.hirehack.hirehack.service.interfaces;

import com.hirehack.hirehack.entity.User;

/**
 * Interface for AI interview service operations.
 * Provides abstraction for AI-powered interview question generation and feedback.
 */
public interface AiInterviewServiceInterface {
    
    /**
     * Generates the initial interview question based on user profile and interview requirements.
     *
     * @param user the user entity containing profile information
     * @param role the target role for the interview
     * @param skills the required skills for the role
     * @param interviewType the type of interview (e.g., technical, behavioral)
     * @return the generated initial question
     */
    String generateInitialQuestion(User user, String role, String skills, String interviewType);
    
    /**
     * Generates the next interview question based on conversation history.
     *
     * @param role the target role for the interview
     * @param skills the required skills for the role
     * @param interviewType the type of interview
     * @param resumeText the candidate's resume text
     * @param chatHistory the formatted conversation history
     * @return the generated next question
     */
    String generateNextQuestion(String role, String skills, String interviewType, String resumeText, String chatHistory);
    
    /**
     * Generates comprehensive feedback for a completed interview.
     *
     * @param chatHistory the complete interview transcript
     * @param resumeText the candidate's resume text
     * @param role the target role for the interview
     * @param skills the required skills for the role
     * @return the generated feedback in markdown format
     */
    String generateFeedback(String chatHistory, String resumeText, String role, String skills);
}
