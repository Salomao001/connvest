package com.conninvest.backend.controller;

import com.conninvest.backend.model.Post;
import com.conninvest.backend.repository.PostRepository;
import com.conninvest.backend.repository.StartupMembershipRepository;
import com.conninvest.backend.repository.StartupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin("*")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private StartupMembershipRepository startupMembershipRepository;

    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestBody Post post) {
        if (isStartupPost(post)) {
            Long startupId = post.getStartupId() != null ? post.getStartupId() : post.getAuthorId();

            if (startupId == null || startupRepository.findById(startupId).isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Startup nao encontrada."));
            }

            if (userId == null || !canPublishAsStartup(startupId, userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Usuario sem permissao para publicar em nome da startup."));
            }

            post.setStartupId(startupId);
        }

        return ResponseEntity.ok(postRepository.save(post));
    }

    private boolean isStartupPost(Post post) {
        return post.getStartupId() != null || "Startup".equals(post.getAuthorType());
    }

    private boolean canPublishAsStartup(Long startupId, Long userId) {
        Set<String> allowedRoles = Set.of("Owner", "Admin", "Editor");
        return startupMembershipRepository.findByStartupIdAndUserId(startupId, userId)
                .map(membership -> allowedRoles.contains(membership.getRole()))
                .orElse(false);
    }
}
