package com.conninvest.backend.controller;

import com.conninvest.backend.dto.SearchResultDTO;
import com.conninvest.backend.repository.StartupRepository;
import com.conninvest.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.conninvest.backend.model.User;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin("*")
public class SearchController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StartupRepository startupRepository;

    @GetMapping
    public SearchResultDTO search(@RequestParam("q") String query) {
        return new SearchResultDTO(
                userRepository.findByNameContainingIgnoreCaseOrBioContainingIgnoreCase(query, query),
                startupRepository.findByNameContainingIgnoreCaseOrSectorContainingIgnoreCase(query, query)
        );
    }

    @GetMapping("/cofounders")
    public List<User> searchCoFounders(@RequestParam(value = "area", required = false) String area) {
        if (area == null || area.isBlank()) {
            return userRepository.findBySeekingCoFounderTrue();
        }

        return userRepository.findBySeekingCoFounderTrueAndCoFounderAreaIgnoreCase(area);
    }

    @GetMapping("/investors")
    public List<User> searchInvestors(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "sector", required = false) String sector,
            @RequestParam(value = "stage", required = false) String stage) {
        return userRepository.findAll().stream()
            .filter(u -> u.getProfileTypes() != null && (u.getProfileTypes().contains("Investidor") || u.getProfileTypes().contains("Advisor")))
            .filter(u -> query == null || query.isBlank() ||
                u.getName().toLowerCase().contains(query.toLowerCase()) ||
                (u.getBio() != null && u.getBio().toLowerCase().contains(query.toLowerCase())))
            .filter(u -> type == null || type.isBlank() ||
                (u.getInvestorType() != null && u.getInvestorType().toLowerCase().contains(type.toLowerCase())))
            .filter(u -> sector == null || sector.isBlank() ||
                (u.getSectorsOfInterest() != null && u.getSectorsOfInterest().toLowerCase().contains(sector.toLowerCase())))
            .filter(u -> stage == null || stage.isBlank() ||
                (u.getStagesOfInterest() != null && u.getStagesOfInterest().toLowerCase().contains(stage.toLowerCase())))
            .toList();
    }
}
