package com.hirehack.hirehack.observer;

/**
 * Interface for event observers in the observer pattern.
 * Defines the contract for handling events.
 */
public interface EventObserver {
    
    /**
     * Handles an event.
     *
     * @param event the event to handle
     */
    void handleEvent(Event event);
    
    /**
     * Gets the event types this observer is interested in.
     *
     * @return array of event types this observer handles
     */
    String[] getSupportedEventTypes();
}
