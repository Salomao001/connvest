package com.conninvest.backend.controller;

import com.conninvest.backend.model.Startup;
import com.conninvest.backend.model.StartupInvitation;
import com.conninvest.backend.model.StartupMembership;
import com.conninvest.backend.model.User;
import com.conninvest.backend.repository.StartupInvitationRepository;
import com.conninvest.backend.repository.StartupMembershipRepository;
import com.conninvest.backend.repository.StartupRepository;
import com.conninvest.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/startup-invitations")
@CrossOrigin("*")
public class StartupInvitationController {
    private static final Set<String> ALLOWED_ROLES = Set.of("Admin", "Editor", "Viewer", "Advisor", "Co-founder");

    @Autowired
    private StartupInvitationRepository startupInvitationRepository;

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StartupMembershipRepository startupMembershipRepository;

    @PostMapping
    public ResponseEntity<?> createInvitation(
            @RequestParam("senderId") Long senderId,
            @RequestBody StartupInvitation invitationRequest) {
        if (!ALLOWED_ROLES.contains(invitationRequest.getRole())) {
            return ResponseEntity.status(400).body(Map.of("error", "Papel de convite invalido."));
        }

        Startup startup = startupRepository.findById(invitationRequest.getStartupId()).orElse(null);
        if (startup == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Startup nao encontrada."));
        }

        User sender = userRepository.findById(senderId).orElse(null);
        if (sender == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario remetente nao encontrado."));
        }

        User receiver = userRepository.findById(invitationRequest.getReceiverId()).orElse(null);
        if (receiver == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario convidado nao encontrado."));
        }

        if (!canInvite(startup.getId(), senderId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Usuario sem permissao para convidar membros."));
        }

        if (startupMembershipRepository.findByStartupIdAndUserId(startup.getId(), receiver.getId()).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Usuario ja faz parte da startup."));
        }

        if (startupInvitationRepository.findByStartupIdAndReceiverIdAndStatus(startup.getId(), receiver.getId(), "PENDING").isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Convite pendente ja enviado para este usuario."));
        }

        StartupInvitation invitation = new StartupInvitation();
        invitation.setStartupId(startup.getId());
        invitation.setStartupName(startup.getName());
        invitation.setSenderId(sender.getId());
        invitation.setSenderName(sender.getName());
        invitation.setReceiverId(receiver.getId());
        invitation.setReceiverName(receiver.getName());
        invitation.setRole(invitationRequest.getRole());
        invitation.setMessage(invitationRequest.getMessage());

        return ResponseEntity.ok(startupInvitationRepository.save(invitation));
    }

    @GetMapping("/receiver/{receiverId}")
    public List<StartupInvitation> getByReceiver(@PathVariable Long receiverId) {
        return startupInvitationRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId);
    }

    @GetMapping("/startup/{startupId}")
    public List<StartupInvitation> getByStartup(@PathVariable Long startupId) {
        return startupInvitationRepository.findByStartupIdOrderByCreatedAtDesc(startupId);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptInvitation(@PathVariable Long id, @RequestParam("userId") Long userId) {
        return startupInvitationRepository.findById(id)
                .map(invitation -> {
                    if (!invitation.getReceiverId().equals(userId)) {
                        return ResponseEntity.status(403).body(Map.of("error", "Somente o convidado pode responder este convite."));
                    }

                    if (!"PENDING".equals(invitation.getStatus())) {
                        return ResponseEntity.status(400).body(Map.of("error", "Convite ja respondido."));
                    }

                    startupMembershipRepository.findByStartupIdAndUserId(invitation.getStartupId(), userId)
                            .orElseGet(() -> {
                                StartupMembership membership = new StartupMembership();
                                membership.setStartupId(invitation.getStartupId());
                                membership.setUserId(userId);
                                membership.setRole(invitation.getRole());
                                return startupMembershipRepository.save(membership);
                            });

                    invitation.setStatus("ACCEPTED");
                    return ResponseEntity.ok(startupInvitationRepository.save(invitation));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectInvitation(@PathVariable Long id, @RequestParam("userId") Long userId) {
        return startupInvitationRepository.findById(id)
                .map(invitation -> {
                    if (!invitation.getReceiverId().equals(userId)) {
                        return ResponseEntity.status(403).body(Map.of("error", "Somente o convidado pode responder este convite."));
                    }

                    if (!"PENDING".equals(invitation.getStatus())) {
                        return ResponseEntity.status(400).body(Map.of("error", "Convite ja respondido."));
                    }

                    invitation.setStatus("REJECTED");
                    return ResponseEntity.ok(startupInvitationRepository.save(invitation));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private boolean canInvite(Long startupId, Long userId) {
        return startupMembershipRepository.findByStartupIdAndUserId(startupId, userId)
                .map(membership -> "Owner".equals(membership.getRole()) || "Admin".equals(membership.getRole()))
                .orElse(false);
    }
}
