package com.conninvest.backend.controller;

import com.conninvest.backend.model.Comment;
import com.conninvest.backend.model.Notification;
import com.conninvest.backend.model.Post;
import com.conninvest.backend.repository.CommentRepository;
import com.conninvest.backend.repository.PostRepository;
import com.conninvest.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@CrossOrigin("*")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentRepository.findByPostIdOrderByCreatedAtAsc(postId));
    }

    @PostMapping
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody Comment comment) {
        return postRepository.findById(postId).<ResponseEntity<?>>map(post -> {
            comment.setPostId(postId);
            Comment saved = commentRepository.save(comment);
            post.setCommentsCount((post.getCommentsCount() == null ? 0 : post.getCommentsCount()) + 1);
            postRepository.save(post);

            if (saved.getAuthorId() != null && !saved.getAuthorId().equals(post.getAuthorId())) {
                Notification notif = new Notification();
                notif.setRecipientId(post.getAuthorId());
                notif.setType("COMMENT");
                notif.setTitle(saved.getAuthorName() + " comentou no seu post");
                notif.setBody(saved.getContent() != null && saved.getContent().length() > 80
                        ? saved.getContent().substring(0, 80) + "..." : saved.getContent());
                notif.setRelatedId(postId);
                notif.setRelatedType("post");
                notificationService.createAndSend(notif);
            }

            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.status(404).body(Map.of("error", "Post nao encontrado.")));
    }
}
