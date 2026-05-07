package com.conninvest.backend.service;

import com.conninvest.backend.model.Notification;
import com.conninvest.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Autowired
    private NotificationRepository notificationRepository;

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId, emitter));
        emitter.onTimeout(() -> emitters.remove(userId, emitter));
        emitter.onError(e -> emitters.remove(userId, emitter));
        try {
            emitter.send(SseEmitter.event().comment("connected"));
        } catch (Exception e) {
            emitters.remove(userId, emitter);
        }
        return emitter;
    }

    public Notification createAndSend(Notification notification) {
        Notification saved = notificationRepository.save(notification);
        SseEmitter emitter = emitters.get(saved.getRecipientId());
        if (emitter != null) {
            try {
                String payload = String.format(
                    "{\"id\":%d,\"type\":\"%s\",\"title\":\"%s\",\"body\":\"%s\",\"relatedId\":%d}",
                    saved.getId(),
                    saved.getType(),
                    escape(saved.getTitle()),
                    escape(saved.getBody() != null ? saved.getBody() : ""),
                    saved.getRelatedId() != null ? saved.getRelatedId() : 0
                );
                emitter.send(SseEmitter.event().name("notification").data(payload));
            } catch (Exception e) {
                emitters.remove(saved.getRecipientId(), emitter);
            }
        }
        return saved;
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
