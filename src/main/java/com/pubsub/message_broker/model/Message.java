// File: src/main/java/com/pubsub/message_broker/model/Message.java
package com.pubsub.message_broker.model;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class Message {
    private String id;
    private String type;
    private String content;
    private Set<String> targetUsers;
    private LocalDateTime timestamp;

    public Message() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<String> getTargetUsers() {
        return targetUsers;
    }

    public void setTargetUsers(Set<String> targetUsers) {
        this.targetUsers = targetUsers;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}