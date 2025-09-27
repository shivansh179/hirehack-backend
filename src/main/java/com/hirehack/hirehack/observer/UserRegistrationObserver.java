package com.hirehack.hirehack.observer;

import com.hirehack.hirehack.service.interfaces.NotificationServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Observer that handles user registration events.
 * Sends welcome notifications and performs post-registration tasks.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationObserver implements EventObserver {
    
    private final NotificationServiceInterface notificationService;
    
    @Override
    public void handleEvent(Event event) {
        if (event instanceof UserRegistrationEvent userRegistrationEvent) {
            handleUserRegistration(userRegistrationEvent);
        }
    }
    
    @Override
    public String[] getSupportedEventTypes() {
        return new String[]{"USER_REGISTRATION"};
    }
    
    private void handleUserRegistration(UserRegistrationEvent event) {
        try {
            log.info("Handling user registration event for user: {}", event.getUser().getPhoneNumber());
            
            // Send welcome email (if email is available in the future)
            // notificationService.sendEmail(user.getEmail(), "Welcome to HireHack", "Welcome message");
            
            // Send welcome SMS
            String welcomeMessage = String.format(
                    "Welcome to HireHack, %s! You can now start practicing interviews. Good luck!",
                    event.getUser().getFullName()
            );
            
            notificationService.sendSms(event.getUser().getPhoneNumber(), welcomeMessage);
            
            // Log registration for analytics
            log.info("User registration completed: phone={}, name={}, profession={}", 
                    event.getUser().getPhoneNumber(), 
                    event.getUser().getFullName(), 
                    event.getUser().getProfession());
            
        } catch (Exception e) {
            log.error("Error handling user registration event for user: {}", 
                    event.getUser().getPhoneNumber(), e);
        }
    }
}
