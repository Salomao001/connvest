package com.conninvest.backend.controller;

import com.conninvest.backend.model.Notification;
import com.conninvest.backend.model.Proposal;
import com.conninvest.backend.repository.NotificationRepository;
import com.conninvest.backend.repository.ProposalRepository;
import com.conninvest.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proposals")
@CrossOrigin("*")
public class ProposalController {

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @PostMapping
    public ResponseEntity<?> sendProposal(@RequestBody Proposal proposal) {
        if (proposal.getSenderId() == null || proposal.getReceiverId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Remetente e destinatario sao obrigatorios."));
        }
        if (proposal.getType() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tipo de proposta e obrigatorio."));
        }

        userRepository.findById(proposal.getSenderId()).ifPresent(sender -> {
            proposal.setSenderName(sender.getName());
            proposal.setSenderPhoto(sender.getPhoto());
        });
        userRepository.findById(proposal.getReceiverId()).ifPresent(receiver -> {
            proposal.setReceiverName(receiver.getName());
        });

        Proposal saved = proposalRepository.save(proposal);

        String typeLabel = switch (proposal.getType()) {
            case "INVESTMENT" -> "Proposta de investimento";
            case "CO_FOUNDER" -> "Proposta de co-founder";
            case "ADVISOR" -> "Proposta de advisor";
            default -> "Nova proposta";
        };

        Notification notification = new Notification();
        notification.setRecipientId(proposal.getReceiverId());
        notification.setType("PROPOSAL");
        notification.setTitle(typeLabel);
        notification.setBody(proposal.getSenderName() + " enviou uma proposta para voce.");
        notification.setRelatedId(saved.getId());
        notification.setRelatedType("proposal");
        notificationRepository.save(notification);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/received")
    public ResponseEntity<List<Proposal>> getReceived(@RequestParam Long userId) {
        return ResponseEntity.ok(proposalRepository.findByReceiverIdOrderByCreatedAtDesc(userId));
    }

    @GetMapping("/sent")
    public ResponseEntity<List<Proposal>> getSent(@RequestParam Long userId) {
        return ResponseEntity.ok(proposalRepository.findBySenderIdOrderByCreatedAtDesc(userId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return proposalRepository.findById(id).map(proposal -> {
            proposal.setStatus(status);
            Proposal updated = proposalRepository.save(proposal);

            if ("ACCEPTED".equals(status)) {
                Notification notification = new Notification();
                notification.setRecipientId(proposal.getSenderId());
                notification.setType("PROPOSAL");
                notification.setTitle("Proposta aceita!");
                notification.setBody(proposal.getReceiverName() + " aceitou sua proposta.");
                notification.setRelatedId(proposal.getId());
                notification.setRelatedType("proposal");
                notificationRepository.save(notification);
            }

            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }
}
