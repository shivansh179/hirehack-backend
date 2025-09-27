package com.hirehack.hirehack.observer;

import com.hirehack.hirehack.service.interfaces.NotificationServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Observer that handles interview completion events.
 * Sends completion notifications and performs post-interview tasks.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InterviewCompletedObserver implements EventObserver {
    
    private final NotificationServiceInterface notificationService;
    
    @Override
    public void handleEvent(Event event) {
        if (event instanceof InterviewCompletedEvent interviewCompletedEvent) {
            handleInterviewCompletion(interviewCompletedEvent);
        }
    }
    
    @Override
    public String[] getSupportedEventTypes() {
        return new String[]{"INTERVIEW_COMPLETED"};
    }
    
    private void handleInterviewCompletion(InterviewCompletedEvent event) {
        try {
            log.info("Handling interview completion event for interview: {}", event.getInterview().getId());
            
            // Send completion notification
            String completionMessage = String.format(
                    "Great job completing your %s interview for %s! Your feedback is ready. Check your dashboard for detailed insights.",
                    event.getInterview().getInterviewType(),
                    event.getInterview().getRole()
            );
            
            notificationService.sendSms(
                    event.getInterview().getUser().getPhoneNumber(), 
                    completionMessage
            );
            
            // Log completion for analytics
            log.info("Interview completed: id={}, user={}, role={}, type={}, duration={} minutes", 
                    event.getInterview().getId(),
                    event.getInterview().getUser().getPhoneNumber(),
                    event.getInterview().getRole(),
                    event.getInterview().getInterviewType(),
                    event.getInterview().getInterviewDurationMinutes());
            
        } catch (Exception e) {
            log.error("Error handling interview completion event for interview: {}", 
                    event.getInterview().getId(), e);
        }
    }
}
