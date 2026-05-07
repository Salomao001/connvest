package com.conninvest.backend.controller;

import com.conninvest.backend.model.Startup;
import com.conninvest.backend.model.StartupMembership;
import com.conninvest.backend.model.User;
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

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StartupControllerTest {

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

    @BeforeEach
    void setUp() {
        startupMembershipRepository.deleteAll();
        startupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateStartupComDadosValidos_deveCriarStartupEVincularOwner() throws Exception {
        User creator = new User();
        creator.setName("Founder Criadora");
        creator.setEmail("creator@email.com");
        creator.setPassword("123456");
        User savedCreator = userRepository.save(creator);

        Startup startup = new Startup();
        startup.setName("Nova Startup");
        startup.setLogo("https://example.com/logo.png");
        startup.setShortDescription("Resumo curto.");
        startup.setDescription("Descricao curta usada no prototipo.");
        startup.setFullDescription("Descricao completa da startup.");
        startup.setSector("SaaS");
        startup.setStage("MVP");
        startup.setLocation("Sao Paulo, SP");
        startup.setWebsiteUrl("https://nova.example.com");
        startup.setCurrentObjective("Validar ICP");
        startup.setMainMetrics("10 clientes piloto");

        mockMvc.perform(post("/api/startups")
                        .param("creatorId", savedCreator.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startup)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Nova Startup"))
                .andExpect(jsonPath("$.logo").value("https://example.com/logo.png"))
                .andExpect(jsonPath("$.shortDescription").value("Resumo curto."))
                .andExpect(jsonPath("$.fullDescription").value("Descricao completa da startup."))
                .andExpect(jsonPath("$.sector").value("SaaS"))
                .andExpect(jsonPath("$.stage").value("MVP"))
                .andExpect(jsonPath("$.location").value("Sao Paulo, SP"))
                .andExpect(jsonPath("$.websiteUrl").value("https://nova.example.com"))
                .andExpect(jsonPath("$.currentObjective").value("Validar ICP"))
                .andExpect(jsonPath("$.mainMetrics").value("10 clientes piloto"));

        List<StartupMembership> memberships = startupMembershipRepository.findByUserId(savedCreator.getId());
        assert(memberships.size() == 1);
        assert(memberships.get(0).getRole().equals("Owner"));
    }

    @Test
    void testCreateStartupComCamposOpcionaisEmBranco_deveCriarComNome() throws Exception {
        User creator = new User();
        creator.setName("Founder Minima");
        creator.setEmail("minima@email.com");
        creator.setPassword("123456");
        User savedCreator = userRepository.save(creator);

        Startup startup = new Startup();
        startup.setName("Startup Minima");

        mockMvc.perform(post("/api/startups")
                        .param("creatorId", savedCreator.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startup)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Startup Minima"));

        List<StartupMembership> memberships = startupMembershipRepository.findByUserId(savedCreator.getId());
        assert(memberships.get(0).getRole().equals("Owner"));
    }

    @Test
    void testCreateStartupSemNome_deveRetornar400() throws Exception {
        User creator = new User();
        creator.setName("Founder Sem Nome");
        creator.setEmail("semnome-startup@email.com");
        creator.setPassword("123456");
        User savedCreator = userRepository.save(creator);

        Startup startup = new Startup();
        startup.setName("");

        mockMvc.perform(post("/api/startups")
                        .param("creatorId", savedCreator.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startup)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Nome da startup obrigatorio."));
    }

    @Test
    void testGetAllStartupsComFiltroDeEstagio_deveRetornarStartupsDoEstagio() throws Exception {
        Startup mvp = new Startup();
        mvp.setName("Startup MVP");
        mvp.setStage("MVP");
        mvp.setSector("SaaS");
        startupRepository.save(mvp);

        Startup ideia = new Startup();
        ideia.setName("Startup Ideia");
        ideia.setStage("Ideia");
        ideia.setSector("Fintech");
        startupRepository.save(ideia);

        mockMvc.perform(get("/api/startups").param("stage", "MVP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Startup MVP"))
                .andExpect(jsonPath("$[0].stage").value("MVP"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testUpdateStartupComoOwner_deveSalvarAlteracoesEAtualizarData() throws Exception {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@email.com");
        owner.setPassword("123456");
        User savedOwner = userRepository.save(owner);

        Startup startup = new Startup();
        startup.setName("Startup Original");
        startup.setDescription("Descricao original");
        Startup savedStartup = startupRepository.save(startup);

        StartupMembership membership = new StartupMembership();
        membership.setUserId(savedOwner.getId());
        membership.setStartupId(savedStartup.getId());
        membership.setRole("Owner");
        startupMembershipRepository.save(membership);

        Startup update = new Startup();
        update.setName("Startup Editada");
        update.setLogo("https://example.com/new-logo.png");
        update.setShortDescription("Resumo editado");
        update.setDescription("Descricao editada");
        update.setFullDescription("Descricao completa editada");
        update.setSector("Healthtech");
        update.setStage("Tracao");
        update.setLocation("Rio de Janeiro, RJ");
        update.setWebsiteUrl("https://editada.example.com");
        update.setCurrentObjective("Escalar vendas");
        update.setMainMetrics("R$ 80k MRR");

        mockMvc.perform(put("/api/startups/" + savedStartup.getId())
                        .param("userId", savedOwner.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Startup Editada"))
                .andExpect(jsonPath("$.logo").value("https://example.com/new-logo.png"))
                .andExpect(jsonPath("$.description").value("Descricao editada"))
                .andExpect(jsonPath("$.sector").value("Healthtech"))
                .andExpect(jsonPath("$.stage").value("Tracao"))
                .andExpect(jsonPath("$.mainMetrics").value("R$ 80k MRR"))
                .andExpect(jsonPath("$.currentObjective").value("Escalar vendas"))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testUpdateStartupComoAdmin_devePermitirEdicao() throws Exception {
        User admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin-startup@email.com");
        admin.setPassword("123456");
        User savedAdmin = userRepository.save(admin);

        Startup startup = new Startup();
        startup.setName("Startup Admin");
        Startup savedStartup = startupRepository.save(startup);

        StartupMembership membership = new StartupMembership();
        membership.setUserId(savedAdmin.getId());
        membership.setStartupId(savedStartup.getId());
        membership.setRole("Admin");
        startupMembershipRepository.save(membership);

        Startup update = new Startup();
        update.setName("Startup Admin Editada");
        update.setSector("SaaS");

        mockMvc.perform(put("/api/startups/" + savedStartup.getId())
                        .param("userId", savedAdmin.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Startup Admin Editada"))
                .andExpect(jsonPath("$.sector").value("SaaS"));
    }

    @Test
    void testUpdateStartupComoViewer_deveRetornar403() throws Exception {
        User viewer = new User();
        viewer.setName("Viewer");
        viewer.setEmail("viewer@email.com");
        viewer.setPassword("123456");
        User savedViewer = userRepository.save(viewer);

        Startup startup = new Startup();
        startup.setName("Startup Bloqueada");
        Startup savedStartup = startupRepository.save(startup);

        StartupMembership membership = new StartupMembership();
        membership.setUserId(savedViewer.getId());
        membership.setStartupId(savedStartup.getId());
        membership.setRole("Viewer");
        startupMembershipRepository.save(membership);

        Startup update = new Startup();
        update.setName("Nao Pode Editar");

        mockMvc.perform(put("/api/startups/" + savedStartup.getId())
                        .param("userId", savedViewer.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Usuario sem permissao para editar startup."));
    }

    @Test
    void testGetStartupComMetricasPublicas_deveExibirReceitaMensal() throws Exception {
        Startup startup = new Startup();
        startup.setName("Startup Publica");
        startup.setMainMetrics("MRR em crescimento");
        startup.setMonthlyRevenue("R$ 180k");
        startup.setUsersCount(1200);
        startup.setGrowthPercent("+32%");
        startup.setClientsCount(42);
        startup.setMrr("R$ 180k");
        startup.setChurn("2.1%");
        startup.setMetricsPublic(true);
        Startup savedStartup = startupRepository.save(startup);

        mockMvc.perform(get("/api/startups/" + savedStartup.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyRevenue").value("R$ 180k"))
                .andExpect(jsonPath("$.usersCount").value(1200))
                .andExpect(jsonPath("$.growthPercent").value("+32%"))
                .andExpect(jsonPath("$.clientsCount").value(42))
                .andExpect(jsonPath("$.mrr").value("R$ 180k"))
                .andExpect(jsonPath("$.churn").value("2.1%"))
                .andExpect(jsonPath("$.metricsPublic").value(true));
    }

    @Test
    void testGetStartupComMetricasPrivadasParaVisitante_deveOcultarMetricas() throws Exception {
        User visitor = new User();
        visitor.setName("Visitante");
        visitor.setEmail("visitante@email.com");
        visitor.setPassword("123456");
        User savedVisitor = userRepository.save(visitor);

        Startup startup = new Startup();
        startup.setName("Startup Privada");
        startup.setMainMetrics("MRR confidencial");
        startup.setMonthlyRevenue("R$ 300k");
        startup.setUsersCount(2200);
        startup.setGrowthPercent("+18%");
        startup.setClientsCount(88);
        startup.setMrr("R$ 300k");
        startup.setChurn("1.7%");
        startup.setMetricsPublic(false);
        Startup savedStartup = startupRepository.save(startup);

        mockMvc.perform(get("/api/startups/" + savedStartup.getId())
                        .param("viewerId", savedVisitor.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metricsPublic").value(false))
                .andExpect(jsonPath("$.mainMetrics").value(nullValue()))
                .andExpect(jsonPath("$.monthlyRevenue").value(nullValue()))
                .andExpect(jsonPath("$.usersCount").value(nullValue()))
                .andExpect(jsonPath("$.growthPercent").value(nullValue()))
                .andExpect(jsonPath("$.clientsCount").value(nullValue()))
                .andExpect(jsonPath("$.mrr").value(nullValue()))
                .andExpect(jsonPath("$.churn").value(nullValue()));
    }

    @Test
    void testGetStartupComMetricasPrivadasParaOwner_deveExibirMetricas() throws Exception {
        User owner = new User();
        owner.setName("Owner Metricas");
        owner.setEmail("owner-metricas@email.com");
        owner.setPassword("123456");
        User savedOwner = userRepository.save(owner);

        Startup startup = new Startup();
        startup.setName("Startup Owner");
        startup.setMainMetrics("MRR privado");
        startup.setMonthlyRevenue("R$ 420k");
        startup.setMrr("R$ 420k");
        startup.setMetricsPublic(false);
        Startup savedStartup = startupRepository.save(startup);

        StartupMembership membership = new StartupMembership();
        membership.setUserId(savedOwner.getId());
        membership.setStartupId(savedStartup.getId());
        membership.setRole("Owner");
        startupMembershipRepository.save(membership);

        mockMvc.perform(get("/api/startups/" + savedStartup.getId())
                        .param("viewerId", savedOwner.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metricsPublic").value(false))
                .andExpect(jsonPath("$.mainMetrics").value("MRR privado"))
                .andExpect(jsonPath("$.monthlyRevenue").value("R$ 420k"))
                .andExpect(jsonPath("$.mrr").value("R$ 420k"));
    }

    @Test
    void testUpdateMemberRoleComoOwner_deveAlterarPermissao() throws Exception {
        User owner = createUser("Owner Permissoes", "owner-permissoes@email.com");
        User editor = createUser("Editor Permissoes", "editor-permissoes@email.com");
        Startup startup = createStartup("Startup Permissoes");
        createMembership(owner.getId(), startup.getId(), "Owner");
        createMembership(editor.getId(), startup.getId(), "Editor");

        mockMvc.perform(put("/api/startups/" + startup.getId() + "/members/" + editor.getId() + "/role")
                        .param("requesterId", owner.getId().toString())
                        .param("role", "Viewer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("Viewer"));
    }

    @Test
    void testUpdateMemberRoleComoEditor_deveRetornar403() throws Exception {
        User editor = createUser("Editor Sem Permissao", "editor-sem-permissao@email.com");
        User viewer = createUser("Viewer Alvo", "viewer-alvo@email.com");
        Startup startup = createStartup("Startup Bloqueio Permissao");
        createMembership(editor.getId(), startup.getId(), "Editor");
        createMembership(viewer.getId(), startup.getId(), "Viewer");

        mockMvc.perform(put("/api/startups/" + startup.getId() + "/members/" + viewer.getId() + "/role")
                        .param("requesterId", editor.getId().toString())
                        .param("role", "Admin"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Usuario sem permissao para gerenciar membros."));
    }

    @Test
    void testRemoveMemberComoOwner_deveRemoverVinculo() throws Exception {
        User owner = createUser("Owner Remove", "owner-remove@email.com");
        User advisor = createUser("Advisor Remove", "advisor-remove@email.com");
        Startup startup = createStartup("Startup Remove");
        createMembership(owner.getId(), startup.getId(), "Owner");
        createMembership(advisor.getId(), startup.getId(), "Advisor");

        mockMvc.perform(delete("/api/startups/" + startup.getId() + "/members/" + advisor.getId())
                        .param("requesterId", owner.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Membro removido com sucesso."));

        assert(startupMembershipRepository.findByStartupIdAndUserId(startup.getId(), advisor.getId()).isEmpty());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("123456");
        return userRepository.save(user);
    }

    private Startup createStartup(String name) {
        Startup startup = new Startup();
        startup.setName(name);
        return startupRepository.save(startup);
    }

    private StartupMembership createMembership(Long userId, Long startupId, String role) {
        StartupMembership membership = new StartupMembership();
        membership.setUserId(userId);
        membership.setStartupId(startupId);
        membership.setRole(role);
        return startupMembershipRepository.save(membership);
    }
}
