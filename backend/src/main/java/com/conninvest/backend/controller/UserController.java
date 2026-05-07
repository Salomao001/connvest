package com.conninvest.backend.controller;

import com.conninvest.backend.dto.AuthResponse;
import com.conninvest.backend.dto.UserStartupDTO;
import com.conninvest.backend.model.User;
import com.conninvest.backend.repository.StartupMembershipRepository;
import com.conninvest.backend.repository.StartupRepository;
import com.conninvest.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private StartupMembershipRepository startupMembershipRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam("query") String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String normalizedQuery = query.trim();
        return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(normalizedQuery, normalizedQuery);
    }

    @GetMapping("/{id}/startups")
    public ResponseEntity<List<UserStartupDTO>> getUserStartups(@PathVariable Long id) {
        if (userRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<UserStartupDTO> startups = startupMembershipRepository.findByUserId(id).stream()
                .map(membership -> startupRepository.findById(membership.getStartupId())
                        .map(startup -> new UserStartupDTO(
                                startup.getId(),
                                startup.getName(),
                                startup.getSector(),
                                startup.getStage(),
                                startup.getDescription(),
                                membership.getRole()
                        ))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return ResponseEntity.ok(startups);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User registerRequest) {
        // Valida se e-mail já está cadastrado
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.status(409)
                    .body(Map.of("error", "E-mail já cadastrado."));
        }

        // Validação básica
        if (registerRequest.getName() == null || registerRequest.getName().isBlank()) {
            return ResponseEntity.status(400).body(Map.of("error", "Nome é obrigatório."));
        }
        if (registerRequest.getEmail() == null || !registerRequest.getEmail().contains("@")) {
            return ResponseEntity.status(400).body(Map.of("error", "E-mail inválido."));
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 6) {
            return ResponseEntity.status(400).body(Map.of("error", "Senha deve ter ao menos 6 caracteres."));
        }

        User saved = userRepository.save(registerRequest);
        return ResponseEntity.ok(new AuthResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getProfileTypes()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .filter(user -> user.getPassword().equals(loginRequest.getPassword()))
                .map(user -> ResponseEntity.ok((Object) new AuthResponse(
                        user.getId(), user.getName(), user.getEmail(), user.getProfileTypes())))
                .orElse(ResponseEntity.status(401)
                        .body(Map.of("error", "E-mail ou senha inválidos.")));
    }

    @PostMapping("/{id}/profile-types")
    public ResponseEntity<?> updateProfileTypes(@PathVariable Long id, @RequestBody List<String> profileTypes) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setProfileTypes(profileTypes);
                    userRepository.save(user);
                    return ResponseEntity.ok(Map.of("message", "Perfis atualizados com sucesso."));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody User profileRequest) {
        if (profileRequest.getName() == null || profileRequest.getName().isBlank()) {
            return ResponseEntity.status(400).body(Map.of("error", "Nome obrigatorio."));
        }

        return userRepository.findById(id)
                .map(user -> {
                    user.setName(profileRequest.getName().trim());
                    user.setPhoto(profileRequest.getPhoto());
                    user.setBio(profileRequest.getBio());
                    user.setLocation(profileRequest.getLocation());
                    user.setProfileTypes(profileRequest.getProfileTypes());
                    user.setPastExperiences(profileRequest.getPastExperiences());
                    user.setMainSkills(profileRequest.getMainSkills());
                    user.setInterests(profileRequest.getInterests());
                    user.setExternalLinks(profileRequest.getExternalLinks());
                    user.setSeekingCoFounder(Boolean.TRUE.equals(profileRequest.getSeekingCoFounder()));
                    user.setDesiredCoFounderType(profileRequest.getDesiredCoFounderType());
                    user.setCoFounderArea(profileRequest.getCoFounderArea());
                    user.setCoFounderDedication(profileRequest.getCoFounderDedication());
                    user.setCoFounderDescription(profileRequest.getCoFounderDescription());
                    // Investor fields
                    user.setInvestorType(profileRequest.getInvestorType());
                    user.setSectorsOfInterest(profileRequest.getSectorsOfInterest());
                    user.setStagesOfInterest(profileRequest.getStagesOfInterest());
                    user.setAverageTicket(profileRequest.getAverageTicket());
                    user.setInvestmentHistory(profileRequest.getInvestmentHistory());
                    user.setValueAdd(profileRequest.getValueAdd());
                    user.setOperationStyle(profileRequest.getOperationStyle());
                    user.setContactPreference(profileRequest.getContactPreference());
                    User saved = userRepository.save(user);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
