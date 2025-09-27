package com.hirehack.hirehack.observer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Event publisher for the observer pattern.
 * Manages event observers and publishes events to interested observers.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {
    
    private final List<EventObserver> eventObservers;
    
    /**
     * Publishes an event to all interested observers.
     *
     * @param event the event to publish
     */
    public void publishEvent(Event event) {
        log.debug("Publishing event: {}", event);
        
        eventObservers.stream()
                .filter(observer -> isObserverInterested(observer, event))
                .forEach(observer -> {
                    try {
                        // Handle events asynchronously to avoid blocking
                        CompletableFuture.runAsync(() -> {
                            try {
                                observer.handleEvent(event);
                            } catch (Exception e) {
                                log.error("Error handling event {} by observer {}", 
                                        event.getEventType(), observer.getClass().getSimpleName(), e);
                            }
                        });
                    } catch (Exception e) {
                        log.error("Error publishing event {} to observer {}", 
                                event.getEventType(), observer.getClass().getSimpleName(), e);
                    }
                });
    }
    
    /**
     * Checks if an observer is interested in a specific event.
     *
     * @param observer the observer to check
     * @param event the event to check
     * @return true if observer is interested, false otherwise
     */
    private boolean isObserverInterested(EventObserver observer, Event event) {
        String[] supportedTypes = observer.getSupportedEventTypes();
        if (supportedTypes == null || supportedTypes.length == 0) {
            return false;
        }
        
        for (String supportedType : supportedTypes) {
            if (supportedType.equals(event.getEventType())) {
                return true;
            }
        }
        
        return false;
    }
}
