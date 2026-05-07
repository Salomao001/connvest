package com.conninvest.backend.controller;

import com.conninvest.backend.model.ConnectionRequest;
import com.conninvest.backend.repository.ConnectionRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
@CrossOrigin("*")
public class ConnectionController {

    @Autowired
    private ConnectionRequestRepository repository;

    @PostMapping
    public ConnectionRequest createRequest(@RequestBody ConnectionRequest request) {
        return repository.save(request);
    }

    @GetMapping("/receiver/{receiverId}")
    public List<ConnectionRequest> getByReceiver(@PathVariable Long receiverId) {
        return repository.findByReceiverIdOrderByCreatedAtDesc(receiverId);
    }

    @PutMapping("/{id}/status")
    public ConnectionRequest updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        ConnectionRequest req = repository.findById(id).orElseThrow();
        req.setStatus(status);
        return repository.save(req);
    }
}
