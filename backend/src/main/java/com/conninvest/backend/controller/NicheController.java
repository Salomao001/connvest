package com.conninvest.backend.controller;

import com.conninvest.backend.model.NicheConfig;
import com.conninvest.backend.repository.NicheConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/niches")
@CrossOrigin("*")
public class NicheController {

    @Autowired
    private NicheConfigRepository nicheConfigRepository;

    @GetMapping
    public List<NicheConfig> getAllNiches() {
        return nicheConfigRepository.findAllByOrderBySortOrderAsc();
    }
}
