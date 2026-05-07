package com.conninvest.backend.controller;

import com.conninvest.backend.model.Notification;
import com.conninvest.backend.repository.NotificationRepository;
import com.conninvest.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin("*")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam Long userId) {
        return notificationService.subscribe(userId);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            @RequestParam Long userId,
            @RequestParam(required = false) String excludeType) {
        List<Notification> list = excludeType != null
                ? notificationRepository.findByRecipientIdAndTypeNotOrderByCreatedAtDesc(userId, excludeType)
                : notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @RequestParam Long userId,
            @RequestParam(required = false) String excludeType) {
        long count = excludeType != null
                ? notificationRepository.countByRecipientIdAndReadFalseAndTypeNot(userId, excludeType)
                : notificationRepository.countByRecipientIdAndReadFalse(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        return notificationRepository.findById(id).map(n -> {
            n.setRead(true);
            return ResponseEntity.ok(notificationRepository.save(n));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@RequestParam Long userId) {
        notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId).forEach(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
        return ResponseEntity.ok(Map.of("message", "Todas as notificacoes foram lidas."));
    }
}
