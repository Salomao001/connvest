package com.conninvest.backend.controller;

import com.conninvest.backend.model.Notification;
import com.conninvest.backend.model.PostLike;
import com.conninvest.backend.repository.PostLikeRepository;
import com.conninvest.backend.repository.PostRepository;
import com.conninvest.backend.repository.UserRepository;
import com.conninvest.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@CrossOrigin("*")
public class LikeController {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<?> toggleLike(@PathVariable Long postId, @RequestParam Long userId) {
        return postRepository.findById(postId).map(post -> {
            var existing = postLikeRepository.findByPostIdAndUserId(postId, userId);
            if (existing.isPresent()) {
                postLikeRepository.delete(existing.get());
                post.setLikesCount(Math.max(0, (post.getLikesCount() == null ? 0 : post.getLikesCount()) - 1));
                postRepository.save(post);
                return ResponseEntity.ok(Map.of("liked", false, "count", post.getLikesCount()));
            } else {
                PostLike like = new PostLike();
                like.setPostId(postId);
                like.setUserId(userId);
                postLikeRepository.save(like);
                post.setLikesCount((post.getLikesCount() == null ? 0 : post.getLikesCount()) + 1);
                postRepository.save(post);

                if (!userId.equals(post.getAuthorId())) {
                    String likerName = userRepository.findById(userId)
                            .map(u -> u.getName()).orElse("Alguém");
                    Notification notif = new Notification();
                    notif.setRecipientId(post.getAuthorId());
                    notif.setType("LIKE");
                    notif.setTitle(likerName + " curtiu seu post");
                    notif.setBody(post.getContent() != null && post.getContent().length() > 60
                            ? post.getContent().substring(0, 60) + "..." : post.getContent());
                    notif.setRelatedId(postId);
                    notif.setRelatedType("post");
                    notificationService.createAndSend(notif);
                }

                return ResponseEntity.ok(Map.of("liked", true, "count", post.getLikesCount()));
            }
        }).orElse(ResponseEntity.status(404).body(Map.of("error", "Post nao encontrado.")));
    }

    @GetMapping("/status")
    public ResponseEntity<?> getLikeStatus(@PathVariable Long postId, @RequestParam Long userId) {
        boolean liked = postLikeRepository.findByPostIdAndUserId(postId, userId).isPresent();
        return ResponseEntity.ok(Map.of("liked", liked));
    }
}
