package com.conninvest.backend.controller;

import com.conninvest.backend.dto.StartupMemberDTO;
import com.conninvest.backend.model.Startup;
import com.conninvest.backend.model.StartupMembership;
import com.conninvest.backend.repository.StartupMembershipRepository;
import com.conninvest.backend.repository.StartupRepository;
import com.conninvest.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/startups")
@CrossOrigin("*")
public class StartupController {

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StartupMembershipRepository startupMembershipRepository;

    private static final Set<String> MEMBER_ROLES = Set.of("Admin", "Editor", "Viewer", "Advisor", "Co-founder");

    @GetMapping
    public List<Startup> getAllStartups(@RequestParam(value = "stage", required = false) String stage) {
        if (stage != null && !stage.isBlank()) {
            return startupRepository.findByStageIgnoreCase(stage);
        }

        return startupRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createStartup(@RequestParam("creatorId") Long creatorId, @RequestBody Startup startup) {
        if (startup.getName() == null || startup.getName().isBlank()) {
            return ResponseEntity.status(400).body(Map.of("error", "Nome da startup obrigatorio."));
        }

        if (userRepository.findById(creatorId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario criador nao encontrado."));
        }

        startup.setName(startup.getName().trim());
        Startup saved = startupRepository.save(startup);

        StartupMembership membership = new StartupMembership();
        membership.setUserId(creatorId);
        membership.setStartupId(saved.getId());
        membership.setRole("Owner");
        startupMembershipRepository.save(membership);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Startup> getStartupById(
            @PathVariable Long id,
            @RequestParam(value = "viewerId", required = false) Long viewerId) {
        return startupRepository.findById(id)
                .map(startup -> ResponseEntity.ok(visibleStartupFor(startup, viewerId)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<StartupMemberDTO>> getStartupMembers(@PathVariable Long id) {
        if (startupRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<StartupMemberDTO> members = startupMembershipRepository.findByStartupId(id).stream()
                .map(membership -> userRepository.findById(membership.getUserId())
                        .map(user -> new StartupMemberDTO(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getPhoto(),
                                membership.getRole()
                        ))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return ResponseEntity.ok(members);
    }

    @PutMapping("/{id}/members/{userId}/role")
    public ResponseEntity<?> updateMemberRole(
            @PathVariable Long id,
            @PathVariable Long userId,
            @RequestParam("requesterId") Long requesterId,
            @RequestParam("role") String role) {
        if (!MEMBER_ROLES.contains(role)) {
            return ResponseEntity.status(400).body(Map.of("error", "Papel invalido."));
        }

        if (!canManageMembers(id, requesterId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Usuario sem permissao para gerenciar membros."));
        }

        return startupMembershipRepository.findByStartupIdAndUserId(id, userId)
                .map(membership -> {
                    if ("Owner".equals(membership.getRole())) {
                        return ResponseEntity.status(400).body(Map.of("error", "Owner nao pode ter papel alterado por este fluxo."));
                    }

                    membership.setRole(role);
                    return ResponseEntity.ok(startupMembershipRepository.save(membership));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<?> removeMember(
            @PathVariable Long id,
            @PathVariable Long userId,
            @RequestParam("requesterId") Long requesterId) {
        if (!canManageMembers(id, requesterId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Usuario sem permissao para gerenciar membros."));
        }

        return startupMembershipRepository.findByStartupIdAndUserId(id, userId)
                .map(membership -> {
                    if ("Owner".equals(membership.getRole())) {
                        return ResponseEntity.status(400).body(Map.of("error", "Owner nao pode ser removido por este fluxo."));
                    }

                    startupMembershipRepository.delete(membership);
                    return ResponseEntity.ok(Map.of("message", "Membro removido com sucesso."));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStartup(@PathVariable Long id, @RequestParam("userId") Long userId, @RequestBody Startup startupRequest) {
        return startupRepository.findById(id)
                .map(startup -> {
                    boolean canEdit = startupMembershipRepository.findByStartupIdAndUserId(id, userId)
                            .map(membership -> "Owner".equals(membership.getRole()) || "Admin".equals(membership.getRole()))
                            .orElse(false);

                    if (!canEdit) {
                        return ResponseEntity.status(403).body(Map.of("error", "Usuario sem permissao para editar startup."));
                    }

                    if (startupRequest.getName() != null) {
                        if (startupRequest.getName().isBlank()) {
                            return ResponseEntity.status(400).body(Map.of("error", "Nome da startup obrigatorio."));
                        }
                        startup.setName(startupRequest.getName().trim());
                    }

                    startup.setLogo(startupRequest.getLogo());
                    startup.setShortDescription(startupRequest.getShortDescription());
                    startup.setDescription(startupRequest.getDescription());
                    startup.setFullDescription(startupRequest.getFullDescription());
                    startup.setSector(startupRequest.getSector());
                    startup.setStage(startupRequest.getStage());
                    startup.setLocation(startupRequest.getLocation());
                    startup.setWebsiteUrl(startupRequest.getWebsiteUrl());
                    startup.setCurrentObjective(startupRequest.getCurrentObjective());
                    startup.setMainMetrics(startupRequest.getMainMetrics());
                    startup.setMonthlyRevenue(startupRequest.getMonthlyRevenue());
                    startup.setUsersCount(startupRequest.getUsersCount());
                    startup.setGrowthPercent(startupRequest.getGrowthPercent());
                    startup.setClientsCount(startupRequest.getClientsCount());
                    startup.setMrr(startupRequest.getMrr());
                    startup.setChurn(startupRequest.getChurn());
                    startup.setMetricsPublic(startupRequest.getMetricsPublic() == null ? true : startupRequest.getMetricsPublic());

                    Startup saved = startupRepository.save(startup);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private Startup visibleStartupFor(Startup startup, Long viewerId) {
        if (Boolean.FALSE.equals(startup.getMetricsPublic()) && !canManageStartup(startup.getId(), viewerId)) {
            Startup copy = copyStartup(startup);
            hideMetrics(copy);
            return copy;
        }

        return startup;
    }

    private boolean canManageStartup(Long startupId, Long userId) {
        if (userId == null) {
            return false;
        }

        return startupMembershipRepository.findByStartupIdAndUserId(startupId, userId)
                .map(membership -> "Owner".equals(membership.getRole()) || "Admin".equals(membership.getRole()))
                .orElse(false);
    }

    private boolean canManageMembers(Long startupId, Long userId) {
        if (userId == null) {
            return false;
        }

        return startupMembershipRepository.findByStartupIdAndUserId(startupId, userId)
                .map(membership -> "Owner".equals(membership.getRole()))
                .orElse(false);
    }

    private Startup copyStartup(Startup startup) {
        Startup copy = new Startup();
        copy.setId(startup.getId());
        copy.setName(startup.getName());
        copy.setLogo(startup.getLogo());
        copy.setShortDescription(startup.getShortDescription());
        copy.setDescription(startup.getDescription());
        copy.setFullDescription(startup.getFullDescription());
        copy.setSector(startup.getSector());
        copy.setStage(startup.getStage());
        copy.setLocation(startup.getLocation());
        copy.setWebsiteUrl(startup.getWebsiteUrl());
        copy.setMainMetrics(startup.getMainMetrics());
        copy.setMonthlyRevenue(startup.getMonthlyRevenue());
        copy.setUsersCount(startup.getUsersCount());
        copy.setGrowthPercent(startup.getGrowthPercent());
        copy.setClientsCount(startup.getClientsCount());
        copy.setMrr(startup.getMrr());
        copy.setChurn(startup.getChurn());
        copy.setMetricsPublic(startup.getMetricsPublic());
        copy.setCurrentObjective(startup.getCurrentObjective());
        copy.setRanking(startup.getRanking());
        copy.setBadges(startup.getBadges());
        copy.setUpdatedAt(startup.getUpdatedAt());
        return copy;
    }

    private void hideMetrics(Startup startup) {
        startup.setMainMetrics(null);
        startup.setMonthlyRevenue(null);
        startup.setUsersCount(null);
        startup.setGrowthPercent(null);
        startup.setClientsCount(null);
        startup.setMrr(null);
        startup.setChurn(null);
    }
}
