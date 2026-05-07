package com.conninvest.backend.controller;

import com.conninvest.backend.model.SavedStartup;
import com.conninvest.backend.model.Startup;
import com.conninvest.backend.repository.SavedStartupRepository;
import com.conninvest.backend.repository.StartupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/saved-startups")
@CrossOrigin("*")
public class SavedStartupController {

    @Autowired
    private SavedStartupRepository savedStartupRepository;

    @Autowired
    private StartupRepository startupRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getSaved(@RequestParam Long userId) {
        List<SavedStartup> saved = savedStartupRepository.findByUserId(userId);
        List<Map<String, Object>> result = saved.stream().map(s -> {
            Map<String, Object> item = new HashMap<>();
            item.put("savedStartup", s);
            startupRepository.findById(s.getStartupId()).ifPresent(startup -> item.put("startup", startup));
            return item;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> saveStartup(@RequestParam Long userId, @RequestParam Long startupId) {
        Optional<SavedStartup> existing = savedStartupRepository.findByUserIdAndStartupId(userId, startupId);
        if (existing.isPresent()) {
            return ResponseEntity.ok(existing.get());
        }
        if (startupRepository.findById(startupId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Startup nao encontrada."));
        }
        SavedStartup saved = new SavedStartup();
        saved.setUserId(userId);
        saved.setStartupId(startupId);
        return ResponseEntity.ok(savedStartupRepository.save(saved));
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<?> unsaveStartup(@RequestParam Long userId, @RequestParam Long startupId) {
        savedStartupRepository.deleteByUserIdAndStartupId(userId, startupId);
        return ResponseEntity.ok(Map.of("message", "Removido dos salvos."));
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkSaved(@RequestParam Long userId, @RequestParam Long startupId) {
        boolean saved = savedStartupRepository.findByUserIdAndStartupId(userId, startupId).isPresent();
        return ResponseEntity.ok(Map.of("saved", saved));
    }

    @PutMapping("/{id}/pipeline")
    public ResponseEntity<?> updatePipeline(@PathVariable Long id, @RequestParam String stage) {
        List<String> validStages = List.of("INTERESTING", "IN_CONVERSATION", "EVALUATING", "ARCHIVED");
        if (!validStages.contains(stage)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Estagio invalido."));
        }
        return savedStartupRepository.findById(id).map(s -> {
            s.setPipelineStage(stage);
            return ResponseEntity.ok(savedStartupRepository.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }
}
