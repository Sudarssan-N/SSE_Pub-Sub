// File: src/main/java/com/pubsub/message_broker/service/EmitterService.java
package com.pubsub.message_broker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmitterService {
    private static final Logger logger = LoggerFactory.getLogger(EmitterService.class);
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitter.onCompletion(() -> {
            logger.debug("Emitter completed for user: {}", userId);
            removeEmitter(userId);
        });

        emitter.onTimeout(() -> {
            logger.debug("Emitter timed out for user: {}", userId);
            removeEmitter(userId);
        });

        emitter.onError(ex -> {
            logger.error("Emitter error for user: {}", userId, ex);
            removeEmitter(userId);
        });

        emitters.put(userId, emitter);
        return emitter;
    }

    public void removeEmitter(String userId) {
        emitters.remove(userId);
    }

    public Map<String, SseEmitter> getEmitters() {
        return emitters;
    }

    public void sendToUser(String userId, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .data(data)
                        .id(UUID.randomUUID().toString())
                        .name("message"));
            } catch (Exception e) {
                logger.error("Error sending message to user: {}", userId, e);
                removeEmitter(userId);
            }
        }
    }
}