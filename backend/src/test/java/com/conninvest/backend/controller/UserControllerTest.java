package com.conninvest.backend.controller;

import com.conninvest.backend.model.User;
import com.conninvest.backend.model.Startup;
import com.conninvest.backend.model.StartupMembership;
import com.conninvest.backend.repository.StartupMembershipRepository;
import com.conninvest.backend.repository.StartupRepository;
import com.conninvest.backend.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private StartupMembershipRepository startupMembershipRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        startupMembershipRepository.deleteAll();
        userRepository.deleteAll();
    }

    // --- REGISTER ---

    @Test
    void testRegisterComDadosValidos_deveRetornar200() throws Exception {
        User user = new User();
        user.setName("Maria Teste");
        user.setEmail("maria@email.com");
        user.setPassword("123456");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                // senha NÃO deve aparecer na resposta
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testRegisterComEmailDuplicado_deveRetornar409() throws Exception {
        User existente = new User();
        existente.setName("João Duplicado");
        existente.setEmail("duplicado@email.com");
        existente.setPassword("123456");
        userRepository.save(existente);

        User novo = new User();
        novo.setName("Outro João");
        novo.setEmail("duplicado@email.com");
        novo.setPassword("654321");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novo)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("E-mail já cadastrado."));
    }

    @Test
    void testRegisterComSenhaInvalida_deveRetornar400() throws Exception {
        User user = new User();
        user.setName("Sem Senha");
        user.setEmail("semsenha@email.com");
        user.setPassword("123"); // menos de 6 chars

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    // --- LOGIN ---

    @Test
    void testLoginComCredenciaisCorretas_deveRetornar200() throws Exception {
        User user = new User();
        user.setName("Login User");
        user.setEmail("login@email.com");
        user.setPassword("senha123");
        userRepository.save(user);

        User loginReq = new User();
        loginReq.setEmail("login@email.com");
        loginReq.setPassword("senha123");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("login@email.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testLoginComSenhaErrada_deveRetornar401() throws Exception {
        User user = new User();
        user.setName("Login User");
        user.setEmail("loginwrong@email.com");
        user.setPassword("correta123");
        userRepository.save(user);

        User loginReq = new User();
        loginReq.setEmail("loginwrong@email.com");
        loginReq.setPassword("errada999");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("E-mail ou senha inválidos."));
    }

    @Test
    void testUpdateProfileTypes_deveRetornar200() throws Exception {
        User user = new User();
        user.setName("Update Types");
        user.setEmail("update@email.com");
        user.setPassword("123456");
        User saved = userRepository.save(user);

        List<String> types = List.of("Founder", "Advisor");

        mockMvc.perform(post("/api/users/" + saved.getId() + "/profile-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(types)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Perfis atualizados com sucesso."));

        User updated = userRepository.findById(saved.getId()).get();
        assert(updated.getProfileTypes().containsAll(types));
    }

    @Test
    void testUpdateProfileComDadosCompletos_deveSalvarCamposDaUs003() throws Exception {
        User user = new User();
        user.setName("Perfil Inicial");
        user.setEmail("perfil@email.com");
        user.setPassword("123456");
        User saved = userRepository.save(user);

        User profile = new User();
        profile.setName("Perfil Editado");
        profile.setPhoto("https://example.com/avatar.png");
        profile.setBio("Founder focada em produto e growth.");
        profile.setLocation("Sao Paulo, SP");
        profile.setProfileTypes(List.of("Founder", "Advisor"));
        profile.setPastExperiences("PM na FinCorp; Founder na TechNova");
        profile.setMainSkills("Produto, Growth, Captacao");
        profile.setInterests("Seed, IA, Networking");
        profile.setExternalLinks("https://linkedin.com/in/perfil");

        mockMvc.perform(put("/api/users/" + saved.getId() + "/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Perfil Editado"))
                .andExpect(jsonPath("$.photo").value("https://example.com/avatar.png"))
                .andExpect(jsonPath("$.bio").value("Founder focada em produto e growth."))
                .andExpect(jsonPath("$.location").value("Sao Paulo, SP"))
                .andExpect(jsonPath("$.profileTypes[0]").value("Founder"))
                .andExpect(jsonPath("$.pastExperiences").value("PM na FinCorp; Founder na TechNova"))
                .andExpect(jsonPath("$.mainSkills").value("Produto, Growth, Captacao"))
                .andExpect(jsonPath("$.interests").value("Seed, IA, Networking"))
                .andExpect(jsonPath("$.externalLinks").value("https://linkedin.com/in/perfil"));

        User updated = userRepository.findById(saved.getId()).get();
        assert(updated.getMainSkills().equals("Produto, Growth, Captacao"));
    }

    @Test
    void testUpdateProfileComCamposOpcionaisEmBranco_deveSalvarNormalmente() throws Exception {
        User user = new User();
        user.setName("Campos Opcionais");
        user.setEmail("opcionais@email.com");
        user.setPassword("123456");
        User saved = userRepository.save(user);

        User profile = new User();
        profile.setName("Somente Nome");
        profile.setPhoto("");
        profile.setBio("");
        profile.setLocation("");
        profile.setProfileTypes(List.of());
        profile.setPastExperiences("");
        profile.setMainSkills("");
        profile.setInterests("");
        profile.setExternalLinks("");

        mockMvc.perform(put("/api/users/" + saved.getId() + "/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Somente Nome"))
                .andExpect(jsonPath("$.bio").value(""))
                .andExpect(jsonPath("$.mainSkills").value(""));
    }

    @Test
    void testUpdateProfileSemNome_deveRetornar400() throws Exception {
        User user = new User();
        user.setName("Sem Nome");
        user.setEmail("semnome@email.com");
        user.setPassword("123456");
        User saved = userRepository.save(user);

        User profile = new User();
        profile.setName("");
        profile.setMainSkills("Produto");

        mockMvc.perform(put("/api/users/" + saved.getId() + "/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Nome obrigatorio."));
    }

    @Test
    void testUpdateProfileBuscandoCoFounder_deveSalvarCamposDaUs004() throws Exception {
        User user = new User();
        user.setName("Founder Inicial");
        user.setEmail("founder@email.com");
        user.setPassword("123456");
        User saved = userRepository.save(user);

        User profile = new User();
        profile.setName("Founder Buscando Socio");
        profile.setSeekingCoFounder(true);
        profile.setDesiredCoFounderType("CTO hands-on");
        profile.setCoFounderArea("tech");
        profile.setCoFounderDedication("full-time");
        profile.setCoFounderDescription("Procuro alguem para liderar arquitetura e produto tecnico.");

        mockMvc.perform(put("/api/users/" + saved.getId() + "/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seekingCoFounder").value(true))
                .andExpect(jsonPath("$.desiredCoFounderType").value("CTO hands-on"))
                .andExpect(jsonPath("$.coFounderArea").value("tech"))
                .andExpect(jsonPath("$.coFounderDedication").value("full-time"))
                .andExpect(jsonPath("$.coFounderDescription").value("Procuro alguem para liderar arquitetura e produto tecnico."));

        User updated = userRepository.findById(saved.getId()).get();
        assert(updated.getSeekingCoFounder());
        assert(updated.getCoFounderArea().equals("tech"));
    }

    @Test
    void testGetUserStartups_deveListarStartupsVinculadasComPapel() throws Exception {
        User user = new User();
        user.setName("Founder Vinculada");
        user.setEmail("vinculada@email.com");
        user.setPassword("123456");
        User savedUser = userRepository.save(user);

        Startup startup = new Startup();
        startup.setName("FinPulse");
        startup.setSector("Fintech");
        startup.setStage("Seed");
        startup.setDescription("Tesouraria B2B com IA.");
        Startup savedStartup = startupRepository.save(startup);

        StartupMembership membership = new StartupMembership();
        membership.setUserId(savedUser.getId());
        membership.setStartupId(savedStartup.getId());
        membership.setRole("Founder");
        startupMembershipRepository.save(membership);

        mockMvc.perform(get("/api/users/" + savedUser.getId() + "/startups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].startupId").value(savedStartup.getId()))
                .andExpect(jsonPath("$[0].name").value("FinPulse"))
                .andExpect(jsonPath("$[0].role").value("Founder"))
                .andExpect(jsonPath("$[0].sector").value("Fintech"));
    }
}
