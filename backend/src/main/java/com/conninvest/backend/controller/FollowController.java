package com.conninvest.backend.controller;

import com.conninvest.backend.model.Follow;
import com.conninvest.backend.model.Notification;
import com.conninvest.backend.repository.FollowRepository;
import com.conninvest.backend.repository.UserRepository;
import com.conninvest.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follows")
@CrossOrigin("*")
public class FollowController {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleFollow(
            @RequestParam Long followerId,
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        var existing = followRepository.findByFollowerIdAndTargetTypeAndTargetId(followerId, targetType, targetId);
        if (existing.isPresent()) {
            followRepository.delete(existing.get());
            long count = followRepository.countByTargetTypeAndTargetId(targetType, targetId);
            return ResponseEntity.ok(Map.of("following", false, "count", count));
        } else {
            Follow follow = new Follow();
            follow.setFollowerId(followerId);
            follow.setTargetType(targetType);
            follow.setTargetId(targetId);
            followRepository.save(follow);
            long count = followRepository.countByTargetTypeAndTargetId(targetType, targetId);

            if ("USER".equals(targetType)) {
                String followerName = userRepository.findById(followerId)
                        .map(u -> u.getName()).orElse("Alguém");
                Notification notif = new Notification();
                notif.setRecipientId(targetId);
                notif.setType("FOLLOW");
                notif.setTitle(followerName + " começou a te seguir");
                notif.setBody("");
                notif.setRelatedId(followerId);
                notif.setRelatedType("user");
                notificationService.createAndSend(notif);
            }

            return ResponseEntity.ok(Map.of("following", true, "count", count));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getFollowStatus(
            @RequestParam Long followerId,
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        boolean following = followRepository.findByFollowerIdAndTargetTypeAndTargetId(followerId, targetType, targetId).isPresent();
        long count = followRepository.countByTargetTypeAndTargetId(targetType, targetId);
        return ResponseEntity.ok(Map.of("following", following, "count", count));
    }

    @GetMapping("/following")
    public ResponseEntity<List<Follow>> getFollowing(@RequestParam Long followerId) {
        return ResponseEntity.ok(followRepository.findByFollowerId(followerId));
    }
}
