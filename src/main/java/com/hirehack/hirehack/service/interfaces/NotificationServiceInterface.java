package com.hirehack.hirehack.service.interfaces;

/**
 * Interface for notification service operations.
 * Provides abstraction for sending various types of notifications.
 */
public interface NotificationServiceInterface {
    
    /**
     * Sends an SMS notification to the specified phone number.
     *
     * @param phoneNumber the recipient's phone number
     * @param message the message content to send
     * @throws Exception if sending fails
     */
    void sendSms(String phoneNumber, String message) throws Exception;
    
    /**
     * Sends an email notification to the specified email address.
     *
     * @param emailAddress the recipient's email address
     * @param subject the email subject
     * @param message the email content
     * @throws Exception if sending fails
     */
    void sendEmail(String emailAddress, String subject, String message) throws Exception;
    
    /**
     * Sends a push notification to a user's device.
     *
     * @param userId the user's unique identifier
     * @param title the notification title
     * @param message the notification message
     * @throws Exception if sending fails
     */
    void sendPushNotification(Long userId, String title, String message) throws Exception;
}
