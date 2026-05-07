package com.conninvest.backend.controller;

import com.conninvest.backend.model.Startup;
import com.conninvest.backend.model.StartupInvitation;
import com.conninvest.backend.model.StartupMembership;
import com.conninvest.backend.model.User;
import com.conninvest.backend.repository.StartupInvitationRepository;
import com.conninvest.backend.repository.StartupMembershipRepository;
import com.conninvest.backend.repository.StartupRepository;
import com.conninvest.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StartupInvitationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private StartupMembershipRepository startupMembershipRepository;

    @Autowired
    private StartupInvitationRepository startupInvitationRepository;

    @BeforeEach
    void setUp() {
        startupInvitationRepository.deleteAll();
        startupMembershipRepository.deleteAll();
        startupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateInvitationComoOwner_deveEnviarConvitePendenteEListarParaConvidado() throws Exception {
        Fixture fixture = createFixture("Owner");

        StartupInvitation invitation = new StartupInvitation();
        invitation.setStartupId(fixture.startup().getId());
        invitation.setReceiverId(fixture.receiver().getId());
        invitation.setRole("Admin");
        invitation.setMessage("Venha ajudar na operacao.");

        mockMvc.perform(post("/api/startup-invitations")
                        .param("senderId", fixture.sender().getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invitation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startupName").value("FinPulse"))
                .andExpect(jsonPath("$.senderName").value("Owner Teste"))
                .andExpect(jsonPath("$.receiverName").value("Convidada Teste"))
                .andExpect(jsonPath("$.role").value("Admin"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        mockMvc.perform(get("/api/startup-invitations/receiver/" + fixture.receiver().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].startupName").value("FinPulse"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void testCreateInvitationComoViewer_deveRetornar403() throws Exception {
        Fixture fixture = createFixture("Viewer");

        StartupInvitation invitation = new StartupInvitation();
        invitation.setStartupId(fixture.startup().getId());
        invitation.setReceiverId(fixture.receiver().getId());
        invitation.setRole("Advisor");

        mockMvc.perform(post("/api/startup-invitations")
                        .param("senderId", fixture.sender().getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invitation)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Usuario sem permissao para convidar membros."));
    }

    @Test
    void testAcceptInvitation_deveCriarMembershipComPapelSelecionado() throws Exception {
        Fixture fixture = createFixture("Admin");
        StartupInvitation invitation = saveInvitation(fixture, "Co-founder");

        mockMvc.perform(put("/api/startup-invitations/" + invitation.getId() + "/accept")
                        .param("userId", fixture.receiver().getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        StartupMembership membership = startupMembershipRepository
                .findByStartupIdAndUserId(fixture.startup().getId(), fixture.receiver().getId())
                .orElseThrow();

        assert(membership.getRole().equals("Co-founder"));
    }

    @Test
    void testRejectInvitation_naoDeveCriarMembership() throws Exception {
        Fixture fixture = createFixture("Owner");
        StartupInvitation invitation = saveInvitation(fixture, "Editor");

        mockMvc.perform(put("/api/startup-invitations/" + invitation.getId() + "/reject")
                        .param("userId", fixture.receiver().getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        assert(startupMembershipRepository
                .findByStartupIdAndUserId(fixture.startup().getId(), fixture.receiver().getId())
                .isEmpty());
    }

    @Test
    void testCreateInvitationComPapelInvalido_deveRetornar400() throws Exception {
        Fixture fixture = createFixture("Owner");

        StartupInvitation invitation = new StartupInvitation();
        invitation.setStartupId(fixture.startup().getId());
        invitation.setReceiverId(fixture.receiver().getId());
        invitation.setRole("Founder Supremo");

        mockMvc.perform(post("/api/startup-invitations")
                        .param("senderId", fixture.sender().getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invitation)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Papel de convite invalido."));
    }

    private Fixture createFixture(String senderRole) {
        User sender = new User();
        sender.setName("Owner Teste");
        sender.setEmail("owner-convite@email.com");
        sender.setPassword("123456");
        User savedSender = userRepository.save(sender);

        User receiver = new User();
        receiver.setName("Convidada Teste");
        receiver.setEmail("convidada@email.com");
        receiver.setPassword("123456");
        User savedReceiver = userRepository.save(receiver);

        Startup startup = new Startup();
        startup.setName("FinPulse");
        Startup savedStartup = startupRepository.save(startup);

        StartupMembership membership = new StartupMembership();
        membership.setUserId(savedSender.getId());
        membership.setStartupId(savedStartup.getId());
        membership.setRole(senderRole);
        startupMembershipRepository.save(membership);

        return new Fixture(savedSender, savedReceiver, savedStartup);
    }

    private StartupInvitation saveInvitation(Fixture fixture, String role) {
        StartupInvitation invitation = new StartupInvitation();
        invitation.setStartupId(fixture.startup().getId());
        invitation.setStartupName(fixture.startup().getName());
        invitation.setSenderId(fixture.sender().getId());
        invitation.setSenderName(fixture.sender().getName());
        invitation.setReceiverId(fixture.receiver().getId());
        invitation.setReceiverName(fixture.receiver().getName());
        invitation.setRole(role);
        invitation.setStatus("PENDING");
        return startupInvitationRepository.save(invitation);
    }

    private record Fixture(User sender, User receiver, Startup startup) {
    }
}
