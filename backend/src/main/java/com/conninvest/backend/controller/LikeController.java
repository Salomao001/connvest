package com.conninvest.backend.controller;

import com.conninvest.backend.model.PostLike;
import com.conninvest.backend.repository.PostLikeRepository;
import com.conninvest.backend.repository.PostRepository;
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
