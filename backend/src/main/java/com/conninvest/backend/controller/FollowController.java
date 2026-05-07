package com.conninvest.backend.controller;

import com.conninvest.backend.model.Follow;
import com.conninvest.backend.repository.FollowRepository;
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
