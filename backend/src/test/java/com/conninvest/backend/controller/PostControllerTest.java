package com.conninvest.backend.controller;

import com.conninvest.backend.model.Post;
import com.conninvest.backend.model.Startup;
import com.conninvest.backend.model.StartupMembership;
import com.conninvest.backend.model.User;
import com.conninvest.backend.repository.PostRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

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
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        startupMembershipRepository.deleteAll();
        startupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreatePostComoAdmin_devePublicarEmNomeDaStartup() throws Exception {
        Fixture fixture = createFixture("Admin");
        Post post = startupPost(fixture.startup());

        mockMvc.perform(post("/api/posts")
                        .param("userId", fixture.user().getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startupId").value(fixture.startup().getId()))
                .andExpect(jsonPath("$.authorType").value("Startup"))
                .andExpect(jsonPath("$.content").value("Update publicado pela startup."));
    }

    @Test
    void testCreatePostComoEditor_devePublicarEmNomeDaStartup() throws Exception {
        Fixture fixture = createFixture("Editor");
        Post post = startupPost(fixture.startup());

        mockMvc.perform(post("/api/posts")
                        .param("userId", fixture.user().getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startupId").value(fixture.startup().getId()));
    }

    @Test
    void testCreatePostComoViewer_deveRetornar403() throws Exception {
        Fixture fixture = createFixture("Viewer");
        Post post = startupPost(fixture.startup());

        mockMvc.perform(post("/api/posts")
                        .param("userId", fixture.user().getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Usuario sem permissao para publicar em nome da startup."));
    }

    @Test
    void testCreatePostPessoal_deveContinuarPermitido() throws Exception {
        Post post = new Post();
        post.setAuthorName("Mariana Silva");
        post.setAuthorType("Founder");
        post.setContent("Post pessoal.");
        post.setType("FREE_TEXT");

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorType").value("Founder"))
                .andExpect(jsonPath("$.content").value("Post pessoal."));
    }

    private Fixture createFixture(String role) {
        User user = new User();
        user.setName("Membro");
        user.setEmail(role.toLowerCase() + "@email.com");
        user.setPassword("123456");
        User savedUser = userRepository.save(user);

        Startup startup = new Startup();
        startup.setName("FinPulse");
        Startup savedStartup = startupRepository.save(startup);

        StartupMembership membership = new StartupMembership();
        membership.setUserId(savedUser.getId());
        membership.setStartupId(savedStartup.getId());
        membership.setRole(role);
        startupMembershipRepository.save(membership);

        return new Fixture(savedUser, savedStartup);
    }

    private Post startupPost(Startup startup) {
        Post post = new Post();
        post.setStartupId(startup.getId());
        post.setAuthorId(startup.getId());
        post.setAuthorName(startup.getName());
        post.setAuthorType("Startup");
        post.setContent("Update publicado pela startup.");
        post.setType("FREE_TEXT");
        return post;
    }

    private record Fixture(User user, Startup startup) {
    }
}
