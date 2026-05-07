package com.conninvest.backend.controller;

import com.conninvest.backend.model.Message;
import com.conninvest.backend.model.Notification;
import com.conninvest.backend.repository.MessageRepository;
import com.conninvest.backend.repository.UserRepository;
import com.conninvest.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin("*")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/conversations")
    public ResponseEntity<List<Map<String, Object>>> getConversations(@RequestParam Long userId) {
        List<Message> allMessages = messageRepository.findConversationMessages(userId);

        Map<Long, Message> latestByPartner = new LinkedHashMap<>();
        for (Message m : allMessages) {
            Long partnerId = m.getSenderId().equals(userId) ? m.getReceiverId() : m.getSenderId();
            latestByPartner.merge(partnerId, m, (existing, newMsg) ->
                newMsg.getCreatedAt().isAfter(existing.getCreatedAt()) ? newMsg : existing);
        }

        List<Map<String, Object>> conversations = latestByPartner.entrySet().stream()
            .sorted((a, b) -> b.getValue().getCreatedAt().compareTo(a.getValue().getCreatedAt()))
            .map(entry -> {
                Long partnerId = entry.getKey();
                Message latest = entry.getValue();
                Map<String, Object> conv = new HashMap<>();
                conv.put("partnerId", partnerId);
                conv.put("partnerName", latest.getSenderId().equals(userId) ? latest.getReceiverName() : latest.getSenderName());
                conv.put("partnerPhoto", latest.getSenderId().equals(userId) ? latest.getReceiverPhoto() : latest.getSenderPhoto());
                conv.put("lastMessage", latest.getContent());
                conv.put("lastMessageAt", latest.getCreatedAt());
                long unread = allMessages.stream()
                    .filter(m -> m.getSenderId().equals(partnerId) && m.getReceiverId().equals(userId) && Boolean.FALSE.equals(m.getRead()))
                    .count();
                conv.put("unreadCount", unread);
                return conv;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/thread")
    public ResponseEntity<List<Message>> getThread(@RequestParam Long userId, @RequestParam Long partnerId) {
        List<Message> messages = messageRepository.findBetweenUsers(userId, partnerId);
        messages.stream()
            .filter(m -> m.getReceiverId().equals(userId) && Boolean.FALSE.equals(m.getRead()))
            .forEach(m -> {
                m.setRead(true);
                messageRepository.save(m);
            });
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody Message message) {
        if (message.getContent() == null || message.getContent().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mensagem nao pode ser vazia."));
        }

        userRepository.findById(message.getSenderId()).ifPresent(sender -> {
            message.setSenderName(sender.getName());
            message.setSenderPhoto(sender.getPhoto());
        });
        userRepository.findById(message.getReceiverId()).ifPresent(receiver -> {
            message.setReceiverName(receiver.getName());
            message.setReceiverPhoto(receiver.getPhoto());
        });

        Message saved = messageRepository.save(message);

        // Push real-time to receiver via WebSocket (manual JSON to avoid serializer issues)
        try {
            String payload = String.format(
                "{\"id\":%d,\"senderId\":%d,\"senderName\":\"%s\",\"senderPhoto\":\"%s\"," +
                "\"receiverId\":%d,\"content\":\"%s\",\"createdAt\":\"%s\"}",
                saved.getId(),
                saved.getSenderId(),
                escape(saved.getSenderName()),
                escape(saved.getSenderPhoto() != null ? saved.getSenderPhoto() : ""),
                saved.getReceiverId(),
                escape(saved.getContent()),
                saved.getCreatedAt().toString()
            );
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(saved.getReceiverId()), "/queue/messages", payload);
        } catch (Exception e) {
            // WebSocket push failure must not break the REST response
        }

        Notification notif = new Notification();
        notif.setRecipientId(saved.getReceiverId());
        notif.setType("MESSAGE");
        notif.setTitle("Nova mensagem de " + saved.getSenderName());
        String preview = saved.getContent().length() > 100
                ? saved.getContent().substring(0, 100) + "..."
                : saved.getContent();
        notif.setBody(preview);
        notif.setRelatedId(saved.getSenderId());
        notif.setRelatedType("user");
        notificationService.createAndSend(notif);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestParam Long userId) {
        long count = messageRepository.countByReceiverIdAndReadFalse(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}
