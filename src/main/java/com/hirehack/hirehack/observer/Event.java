package com.hirehack.hirehack.observer;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Base event class for the observer pattern.
 * Represents an event that can be observed and handled by observers.
 */
public abstract class Event {
    
    private final String eventType;
    private final OffsetDateTime timestamp;
    private final Map<String, Object> metadata;
    
    protected Event(String eventType, Map<String, Object> metadata) {
        this.eventType = eventType;
        this.timestamp = OffsetDateTime.now();
        this.metadata = metadata;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    @Override
    public String toString() {
        return String.format("Event{type='%s', timestamp=%s, metadata=%s}", 
                eventType, timestamp, metadata);
    }
}
