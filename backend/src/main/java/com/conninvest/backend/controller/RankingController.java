package com.conninvest.backend.controller;

import com.conninvest.backend.model.Startup;
import com.conninvest.backend.repository.FollowRepository;
import com.conninvest.backend.repository.PostRepository;
import com.conninvest.backend.repository.StartupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rankings")
@CrossOrigin("*")
public class RankingController {

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private FollowRepository followRepository;

    @GetMapping("/performance")
    public ResponseEntity<List<Map<String, Object>>> getPerformanceRanking() {
        List<Startup> startups = startupRepository.findAll();
        List<Map<String, Object>> ranked = startups.stream().map(s -> {
            int score = computePerformanceScore(s);
            return buildRankEntry(s, score);
        })
        .sorted((a, b) -> (Integer) b.get("score") - (Integer) a.get("score"))
        .collect(Collectors.toList());

        for (int i = 0; i < ranked.size(); i++) {
            ranked.get(i).put("rank", i + 1);
            assignPerformanceBadge(ranked.get(i), i + 1);
        }
        return ResponseEntity.ok(ranked);
    }

    @GetMapping("/popularity")
    public ResponseEntity<List<Map<String, Object>>> getPopularityRanking() {
        List<Startup> startups = startupRepository.findAll();
        List<Map<String, Object>> ranked = startups.stream().map(s -> {
            long followers = followRepository.countByTargetTypeAndTargetId("STARTUP", s.getId());
            long postLikes = postRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(p -> s.getId().equals(p.getStartupId()))
                .mapToInt(p -> p.getLikesCount() == null ? 0 : p.getLikesCount())
                .sum();
            int score = (int) (followers * 10 + postLikes * 2);
            Map<String, Object> entry = buildRankEntry(s, score);
            entry.put("followers", followers);
            entry.put("totalLikes", postLikes);
            return entry;
        })
        .sorted((a, b) -> (Integer) b.get("score") - (Integer) a.get("score"))
        .collect(Collectors.toList());

        for (int i = 0; i < ranked.size(); i++) {
            ranked.get(i).put("rank", i + 1);
            if (i == 0) ranked.get(i).put("badge", "Popular");
            else ranked.get(i).put("badge", "");
        }
        return ResponseEntity.ok(ranked);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<Map<String, Object>>> getTrendingRanking() {
        List<Startup> startups = startupRepository.findAll();
        List<Map<String, Object>> ranked = startups.stream().map(s -> {
            long recentPosts = postRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(p -> s.getId().equals(p.getStartupId()))
                .count();
            long followers = followRepository.countByTargetTypeAndTargetId("STARTUP", s.getId());
            int growthBonus = parseGrowthPercent(s.getGrowthPercent());
            int score = (int) (recentPosts * 15 + followers * 5 + growthBonus);
            Map<String, Object> entry = buildRankEntry(s, score);
            entry.put("recentPosts", recentPosts);
            return entry;
        })
        .sorted((a, b) -> (Integer) b.get("score") - (Integer) a.get("score"))
        .collect(Collectors.toList());

        for (int i = 0; i < ranked.size(); i++) {
            ranked.get(i).put("rank", i + 1);
            if (i == 0) ranked.get(i).put("badge", "Trending");
            else ranked.get(i).put("badge", "");
        }
        return ResponseEntity.ok(ranked);
    }

    private Map<String, Object> buildRankEntry(Startup s, int score) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("id", s.getId());
        entry.put("name", s.getName());
        entry.put("sector", s.getSector());
        entry.put("stage", s.getStage());
        entry.put("score", Math.min(100, score));
        entry.put("badges", s.getBadges());
        entry.put("logo", s.getLogo());
        entry.put("shortDescription", s.getShortDescription());
        return entry;
    }

    private int computePerformanceScore(Startup s) {
        int score = 40;
        if (s.getMonthlyRevenue() != null && !s.getMonthlyRevenue().isBlank()) score += 15;
        if (s.getUsersCount() != null && s.getUsersCount() > 0) score += 10;
        if (s.getMrr() != null && !s.getMrr().isBlank()) score += 10;
        if (s.getClientsCount() != null && s.getClientsCount() > 0) score += 10;
        score += parseGrowthPercent(s.getGrowthPercent());
        if (s.getCurrentObjective() != null && !s.getCurrentObjective().isBlank()) score += 5;
        return score;
    }

    private int parseGrowthPercent(String growth) {
        if (growth == null) return 0;
        try {
            String cleaned = growth.replaceAll("[^\\d.]", "");
            return Math.min(20, (int) Double.parseDouble(cleaned) / 5);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void assignPerformanceBadge(Map<String, Object> entry, int rank) {
        int score = (Integer) entry.get("score");
        if (rank == 1 && score >= 80) entry.put("badge", "Top Growth");
        else if (rank <= 2 && score >= 70) entry.put("badge", "Most Consistent");
        else if (rank <= 3) entry.put("badge", "Rising");
        else entry.put("badge", "");
    }
}
