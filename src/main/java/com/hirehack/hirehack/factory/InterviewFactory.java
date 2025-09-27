package com.hirehack.hirehack.factory;

import com.hirehack.hirehack.dto.InterviewRequestDto;
import com.hirehack.hirehack.entity.Interview;
import com.hirehack.hirehack.entity.User;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating Interview entities.
 * Implements the Factory pattern to encapsulate interview creation logic.
 */
@Component
public class InterviewFactory {

    /**
     * Creates a new Interview entity from interview request data.
     *
     * @param requestDto the interview request containing interview configuration
     * @param user the user who will be interviewed
     * @return a new Interview entity with initial status
     */
    public Interview createInterviewFromRequest(InterviewRequestDto requestDto, User user) {
        Interview interview = new Interview();
        interview.setUser(user);
        interview.setRole(requestDto.getRole());
        interview.setSkills(requestDto.getSkills());
        interview.setInterviewType(requestDto.getInterviewType());
        interview.setInterviewDurationMinutes(requestDto.getInterviewDurationMinutes());
        interview.setStatus("STARTED");
        
        return interview;
    }

    /**
     * Creates a new Interview entity with default settings.
     *
     * @param user the user who will be interviewed
     * @param role the target role for the interview
     * @param skills the required skills
     * @return a new Interview entity with default configuration
     */
    public Interview createDefaultInterview(User user, String role, String skills) {
        Interview interview = new Interview();
        interview.setUser(user);
        interview.setRole(role);
        interview.setSkills(skills);
        interview.setInterviewType("TECHNICAL"); // Default interview type
        interview.setInterviewDurationMinutes(30); // Default duration
        interview.setStatus("STARTED");
        
        return interview;
    }
}
