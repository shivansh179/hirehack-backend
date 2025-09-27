package com.hirehack.hirehack.observer;

import com.hirehack.hirehack.entity.User;

import java.util.Map;

/**
 * Event that is fired when a user successfully registers.
 */
public class UserRegistrationEvent extends Event {
    
    private final User user;
    
    public UserRegistrationEvent(User user, Map<String, Object> metadata) {
        super("USER_REGISTRATION", metadata);
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
}
