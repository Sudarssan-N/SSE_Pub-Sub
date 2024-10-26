// File: src/main/java/com/pubsub/message_broker/controller/MessageController.java
package com.pubsub.message_broker.controller;

import com.pubsub.message_broker.model.Message;
import com.pubsub.message_broker.service.EmitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "https://wise-hawk-hdi2dp-dev-ed.trailblaze.lightning.force.com")
@RestController
@RequestMapping("/api")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final EmitterService emitterService;

    public MessageController(EmitterService emitterService) {
        this.emitterService = emitterService;
    }

    @CrossOrigin(origins = "https://wise-hawk-hdi2dp-dev-ed.trailblaze.lightning.force.com")
    @PostMapping("/register")
    public ResponseEntity<String> registerClient(@RequestParam String userId) {
        logger.debug("Registering client: {}", userId);
        emitterService.createEmitter(userId);
        return ResponseEntity.ok(userId);
    }

    @CrossOrigin(origins = "https://wise-hawk-hdi2dp-dev-ed.trailblaze.lightning.force.com")
    @GetMapping("/stream")
    public SseEmitter streamMessages(@RequestParam String userId) {
        logger.debug("Starting stream for client: {}", userId);
        return emitterService.createEmitter(userId);
    }

    @CrossOrigin(origins = "https://wise-hawk-hdi2dp-dev-ed.trailblaze.lightning.force.com")
    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, Object>> broadcastMessage(@RequestBody Message message) {
        logger.debug("Broadcasting message: {}", message);
        List<String> successfulDeliveries = new ArrayList<>();
        List<String> failedDeliveries = new ArrayList<>();

        message.getTargetUsers().forEach(userId -> {
            try {
                emitterService.sendToUser(userId, message);
                successfulDeliveries.add(userId);
            } catch (Exception e) {
                logger.error("Failed to send message to user: {}", userId, e);
                failedDeliveries.add(userId);
            }
        });

        Map<String, Object> response = Map.of(
                "messageId", message.getId(),
                "successfulDeliveries", successfulDeliveries.size(),
                "failedDeliveries", failedDeliveries.size()
        );

        return ResponseEntity.ok(response);
    }
}