package com.conninvest.backend.controller;

import com.conninvest.backend.dto.StartupMemberDTO;
import com.conninvest.backend.model.*;
import com.conninvest.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/startups")
@CrossOrigin("*")
public class StartupController {

    @Autowired private StartupRepository startupRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private StartupMembershipRepository startupMembershipRepository;
    @Autowired private CapTableEntryRepository capTableEntryRepository;
    @Autowired private StartupRiskRepository startupRiskRepository;
    @Autowired private StartupNicheDataRepository startupNicheDataRepository;

    private static final Set<String> MEMBER_ROLES = Set.of("Admin", "Editor", "Viewer", "Advisor", "Co-founder");

    // --- Listagem e criação ---

    @GetMapping
    public List<Startup> getAllStartups(@RequestParam(value = "stage", required = false) String stage) {
        if (stage != null && !stage.isBlank()) return startupRepository.findByStageIgnoreCase(stage);
        return startupRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createStartup(@RequestParam("creatorId") Long creatorId, @RequestBody Startup startup) {
        if (startup.getName() == null || startup.getName().isBlank())
            return ResponseEntity.status(400).body(Map.of("error", "Nome da startup obrigatorio."));

        if (userRepository.findById(creatorId).isEmpty())
            return ResponseEntity.status(404).body(Map.of("error", "Usuario criador nao encontrado."));

        startup.setName(startup.getName().trim());
        Startup saved = startupRepository.save(startup);

        StartupMembership membership = new StartupMembership();
        membership.setUserId(creatorId);
        membership.setStartupId(saved.getId());
        membership.setRole("Owner");
        startupMembershipRepository.save(membership);

        return ResponseEntity.ok(saved);
    }

    // --- Leitura ---

    @GetMapping("/{id}")
    public ResponseEntity<Startup> getStartupById(@PathVariable Long id,
            @RequestParam(value = "viewerId", required = false) Long viewerId) {
        return startupRepository.findById(id)
                .map(s -> ResponseEntity.ok(visibleStartupFor(s, viewerId)))
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Atualização principal ---

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStartup(@PathVariable Long id,
            @RequestParam("userId") Long userId,
            @RequestBody Startup req) {
        return startupRepository.findById(id).map(s -> {
            if (!canEdit(id, userId))
                return ResponseEntity.status(403).body(Map.of("error", "Usuario sem permissao para editar startup."));

            if (req.getName() != null) {
                if (req.getName().isBlank())
                    return ResponseEntity.status(400).body(Map.of("error", "Nome da startup obrigatorio."));
                s.setName(req.getName().trim());
            }

            // Identidade
            s.setLogo(req.getLogo());
            s.setShortDescription(req.getShortDescription());
            s.setPitch(req.getPitch());
            s.setDescription(req.getDescription());
            s.setFullDescription(req.getFullDescription());
            s.setProblemDescription(req.getProblemDescription());
            s.setSolutionDescription(req.getSolutionDescription());
            s.setCompetitiveDifferential(req.getCompetitiveDifferential());
            s.setSector(req.getSector());
            s.setStage(req.getStage());
            s.setLocation(req.getLocation());
            s.setWebsiteUrl(req.getWebsiteUrl());
            s.setPitchDeckUrl(req.getPitchDeckUrl());
            s.setFoundingYear(req.getFoundingYear());

            // Mercado
            s.setTam(req.getTam());
            s.setSam(req.getSam());
            s.setSom(req.getSom());
            s.setMarketSource(req.getMarketSource());
            s.setMarketTiming(req.getMarketTiming());

            // Financeiro
            s.setMainMetrics(req.getMainMetrics());
            s.setMonthlyRevenue(req.getMonthlyRevenue());
            s.setAnnualRevenue(req.getAnnualRevenue());
            s.setMrr(req.getMrr());
            s.setArr(req.getArr());
            s.setCac(req.getCac());
            s.setLtv(req.getLtv());
            s.setGrossMargin(req.getGrossMargin());
            s.setBurnRate(req.getBurnRate());
            s.setRunway(req.getRunway());
            s.setUsersCount(req.getUsersCount());
            s.setGrowthPercent(req.getGrowthPercent());
            s.setClientsCount(req.getClientsCount());
            s.setChurn(req.getChurn());
            s.setMetricsPublic(req.getMetricsPublic() == null ? true : req.getMetricsPublic());

            // Rodada
            s.setRoundStatus(req.getRoundStatus());
            s.setRoundAmountRaised(req.getRoundAmountRaised());
            s.setRoundValuation(req.getRoundValuation());
            s.setRoundStructure(req.getRoundStructure());
            s.setRoundPercentCommitted(req.getRoundPercentCommitted());
            s.setRoundCapitalUse(req.getRoundCapitalUse());

            // Objetivo
            s.setCurrentObjective(req.getCurrentObjective());

            // Nichos selecionados
            if (req.getSelectedNiches() != null) s.setSelectedNiches(req.getSelectedNiches());

            // Visibilidade por campo
            if (req.getFieldVisibilityJson() != null) s.setFieldVisibilityJson(req.getFieldVisibilityJson());

            return ResponseEntity.ok(startupRepository.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- Cap Table ---

    @GetMapping("/{id}/cap-table")
    public ResponseEntity<List<CapTableEntry>> getCapTable(@PathVariable Long id) {
        if (startupRepository.findById(id).isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(capTableEntryRepository.findByStartupIdOrderBySortOrderAsc(id));
    }

    @PutMapping("/{id}/cap-table")
    @Transactional
    public ResponseEntity<?> replaceCapTable(@PathVariable Long id,
            @RequestParam("userId") Long userId,
            @RequestBody List<CapTableEntry> entries) {
        if (!canEdit(id, userId))
            return ResponseEntity.status(403).body(Map.of("error", "Sem permissao."));

        capTableEntryRepository.deleteByStartupId(id);
        for (int i = 0; i < entries.size(); i++) {
            CapTableEntry e = entries.get(i);
            e.setId(null);
            e.setStartupId(id);
            e.setSortOrder(i);
        }
        return ResponseEntity.ok(capTableEntryRepository.saveAll(entries));
    }

    // --- Riscos ---

    @GetMapping("/{id}/risks")
    public ResponseEntity<List<StartupRisk>> getRisks(@PathVariable Long id) {
        if (startupRepository.findById(id).isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(startupRiskRepository.findByStartupIdOrderBySortOrderAsc(id));
    }

    @PutMapping("/{id}/risks")
    @Transactional
    public ResponseEntity<?> replaceRisks(@PathVariable Long id,
            @RequestParam("userId") Long userId,
            @RequestBody List<StartupRisk> risks) {
        if (!canEdit(id, userId))
            return ResponseEntity.status(403).body(Map.of("error", "Sem permissao."));

        startupRiskRepository.deleteByStartupId(id);
        for (int i = 0; i < risks.size(); i++) {
            StartupRisk r = risks.get(i);
            r.setId(null);
            r.setStartupId(id);
            r.setSortOrder(i);
        }
        return ResponseEntity.ok(startupRiskRepository.saveAll(risks));
    }

    // --- Dados de nicho ---

    @GetMapping("/{id}/niche-data")
    public ResponseEntity<List<StartupNicheData>> getNicheData(@PathVariable Long id) {
        if (startupRepository.findById(id).isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(startupNicheDataRepository.findByStartupId(id));
    }

    @PutMapping("/{id}/niche-data/{nicheKey}")
    public ResponseEntity<?> upsertNicheData(@PathVariable Long id,
            @PathVariable String nicheKey,
            @RequestParam("userId") Long userId,
            @RequestBody Map<String, String> fieldValues) {
        if (!canEdit(id, userId))
            return ResponseEntity.status(403).body(Map.of("error", "Sem permissao."));

        StartupNicheData data = startupNicheDataRepository
                .findByStartupIdAndNicheKey(id, nicheKey)
                .orElseGet(() -> { StartupNicheData d = new StartupNicheData(); d.setStartupId(id); d.setNicheKey(nicheKey); return d; });

        data.setFieldValuesJson(mapToJson(fieldValues));
        return ResponseEntity.ok(startupNicheDataRepository.save(data));
    }

    @DeleteMapping("/{id}/niche-data/{nicheKey}")
    @Transactional
    public ResponseEntity<?> deleteNicheData(@PathVariable Long id,
            @PathVariable String nicheKey,
            @RequestParam("userId") Long userId) {
        if (!canEdit(id, userId))
            return ResponseEntity.status(403).body(Map.of("error", "Sem permissao."));
        startupNicheDataRepository.deleteByStartupIdAndNicheKey(id, nicheKey);
        return ResponseEntity.ok(Map.of("message", "Removido."));
    }

    // --- Membros ---

    @GetMapping("/{id}/members")
    public ResponseEntity<List<StartupMemberDTO>> getStartupMembers(@PathVariable Long id) {
        if (startupRepository.findById(id).isEmpty()) return ResponseEntity.notFound().build();
        List<StartupMemberDTO> members = startupMembershipRepository.findByStartupId(id).stream()
                .map(m -> userRepository.findById(m.getUserId())
                        .map(u -> new StartupMemberDTO(u.getId(), u.getName(), u.getEmail(), u.getPhoto(), m.getRole()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
        return ResponseEntity.ok(members);
    }

    @PutMapping("/{id}/members/{userId}/role")
    public ResponseEntity<?> updateMemberRole(@PathVariable Long id,
            @PathVariable Long userId,
            @RequestParam("requesterId") Long requesterId,
            @RequestParam("role") String role) {
        if (!MEMBER_ROLES.contains(role))
            return ResponseEntity.status(400).body(Map.of("error", "Papel invalido."));
        if (!canManageMembers(id, requesterId))
            return ResponseEntity.status(403).body(Map.of("error", "Sem permissao para gerenciar membros."));
        return startupMembershipRepository.findByStartupIdAndUserId(id, userId)
                .map(m -> {
                    if ("Owner".equals(m.getRole()))
                        return ResponseEntity.status(400).body(Map.of("error", "Owner nao pode ter papel alterado."));
                    m.setRole(role);
                    return ResponseEntity.ok(startupMembershipRepository.save(m));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable Long id,
            @PathVariable Long userId,
            @RequestParam("requesterId") Long requesterId) {
        if (!canManageMembers(id, requesterId))
            return ResponseEntity.status(403).body(Map.of("error", "Sem permissao para gerenciar membros."));
        return startupMembershipRepository.findByStartupIdAndUserId(id, userId)
                .map(m -> {
                    if ("Owner".equals(m.getRole()))
                        return ResponseEntity.status(400).body(Map.of("error", "Owner nao pode ser removido."));
                    startupMembershipRepository.delete(m);
                    return ResponseEntity.ok(Map.of("message", "Membro removido."));
                }).orElse(ResponseEntity.notFound().build());
    }

    // --- Helpers ---

    private Startup visibleStartupFor(Startup s, Long viewerId) {
        if (Boolean.FALSE.equals(s.getMetricsPublic()) && !canEdit(s.getId(), viewerId)) {
            Startup copy = shallowCopy(s);
            copy.setMainMetrics(null); copy.setMonthlyRevenue(null); copy.setAnnualRevenue(null);
            copy.setMrr(null); copy.setArr(null); copy.setCac(null); copy.setLtv(null);
            copy.setGrossMargin(null); copy.setBurnRate(null); copy.setRunway(null);
            copy.setUsersCount(null); copy.setGrowthPercent(null); copy.setClientsCount(null);
            copy.setChurn(null); copy.setRoundAmountRaised(null); copy.setRoundValuation(null);
            return copy;
        }
        return s;
    }

    private Startup shallowCopy(Startup s) {
        Startup c = new Startup();
        c.setId(s.getId()); c.setName(s.getName()); c.setLogo(s.getLogo());
        c.setShortDescription(s.getShortDescription()); c.setPitch(s.getPitch());
        c.setDescription(s.getDescription()); c.setFullDescription(s.getFullDescription());
        c.setProblemDescription(s.getProblemDescription()); c.setSolutionDescription(s.getSolutionDescription());
        c.setCompetitiveDifferential(s.getCompetitiveDifferential());
        c.setSector(s.getSector()); c.setStage(s.getStage()); c.setLocation(s.getLocation());
        c.setWebsiteUrl(s.getWebsiteUrl()); c.setPitchDeckUrl(s.getPitchDeckUrl());
        c.setFoundingYear(s.getFoundingYear());
        c.setTam(s.getTam()); c.setSam(s.getSam()); c.setSom(s.getSom());
        c.setMarketSource(s.getMarketSource()); c.setMarketTiming(s.getMarketTiming());
        c.setMainMetrics(s.getMainMetrics()); c.setMonthlyRevenue(s.getMonthlyRevenue());
        c.setAnnualRevenue(s.getAnnualRevenue()); c.setMrr(s.getMrr()); c.setArr(s.getArr());
        c.setCac(s.getCac()); c.setLtv(s.getLtv()); c.setGrossMargin(s.getGrossMargin());
        c.setBurnRate(s.getBurnRate()); c.setRunway(s.getRunway());
        c.setUsersCount(s.getUsersCount()); c.setGrowthPercent(s.getGrowthPercent());
        c.setClientsCount(s.getClientsCount()); c.setChurn(s.getChurn());
        c.setMetricsPublic(s.getMetricsPublic());
        c.setRoundStatus(s.getRoundStatus()); c.setRoundAmountRaised(s.getRoundAmountRaised());
        c.setRoundValuation(s.getRoundValuation()); c.setRoundStructure(s.getRoundStructure());
        c.setRoundPercentCommitted(s.getRoundPercentCommitted()); c.setRoundCapitalUse(s.getRoundCapitalUse());
        c.setCurrentObjective(s.getCurrentObjective()); c.setRanking(s.getRanking()); c.setBadges(s.getBadges());
        c.setSelectedNiches(s.getSelectedNiches()); c.setFieldVisibilityJson(s.getFieldVisibilityJson());
        c.setUpdatedAt(s.getUpdatedAt());
        return c;
    }

    private boolean canEdit(Long startupId, Long userId) {
        if (userId == null) return false;
        return startupMembershipRepository.findByStartupIdAndUserId(startupId, userId)
                .map(m -> "Owner".equals(m.getRole()) || "Admin".equals(m.getRole()))
                .orElse(false);
    }

    private boolean canManageMembers(Long startupId, Long userId) {
        if (userId == null) return false;
        return startupMembershipRepository.findByStartupIdAndUserId(startupId, userId)
                .map(m -> "Owner".equals(m.getRole()))
                .orElse(false);
    }

    private String mapToJson(Map<String, String> map) {
        if (map == null || map.isEmpty()) return "{}";
        StringBuilder sb = new StringBuilder("{");
        map.forEach((k, v) -> sb.append("\"").append(k.replace("\"", "\\\"")).append("\":\"")
                .append(v == null ? "" : v.replace("\"", "\\\"")).append("\","));
        if (sb.charAt(sb.length() - 1) == ',') sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }
}
