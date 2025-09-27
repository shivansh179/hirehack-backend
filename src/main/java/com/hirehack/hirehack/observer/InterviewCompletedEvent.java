package com.hirehack.hirehack.observer;

import com.hirehack.hirehack.entity.Interview;

import java.util.Map;

/**
 * Event that is fired when an interview is completed.
 */
public class InterviewCompletedEvent extends Event {
    
    private final Interview interview;
    
    public InterviewCompletedEvent(Interview interview, Map<String, Object> metadata) {
        super("INTERVIEW_COMPLETED", metadata);
        this.interview = interview;
    }
    
    public Interview getInterview() {
        return interview;
    }
}
