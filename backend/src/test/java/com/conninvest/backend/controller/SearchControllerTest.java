package com.conninvest.backend.controller;

import com.conninvest.backend.model.User;
import com.conninvest.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testSearchCoFounders_deveRetornarApenasUsuariosBuscandoCoFounder() throws Exception {
        User seeking = new User();
        seeking.setName("Founder Aberta");
        seeking.setEmail("aberta@email.com");
        seeking.setPassword("123456");
        seeking.setSeekingCoFounder(true);
        seeking.setCoFounderArea("tech");
        seeking.setCoFounderDescription("Busco CTO.");
        userRepository.save(seeking);

        User notSeeking = new User();
        notSeeking.setName("Founder Fechada");
        notSeeking.setEmail("fechada@email.com");
        notSeeking.setPassword("123456");
        notSeeking.setSeekingCoFounder(false);
        userRepository.save(notSeeking);

        mockMvc.perform(get("/api/search/cofounders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Founder Aberta"))
                .andExpect(jsonPath("$[0].coFounderDescription").value("Busco CTO."))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testSearchCoFoundersPorArea_deveFiltrarAreaDesejada() throws Exception {
        User tech = new User();
        tech.setName("Founder Tech");
        tech.setEmail("tech@email.com");
        tech.setPassword("123456");
        tech.setSeekingCoFounder(true);
        tech.setCoFounderArea("tech");
        userRepository.save(tech);

        User business = new User();
        business.setName("Founder Business");
        business.setEmail("business@email.com");
        business.setPassword("123456");
        business.setSeekingCoFounder(true);
        business.setCoFounderArea("business");
        userRepository.save(business);

        mockMvc.perform(get("/api/search/cofounders").param("area", "tech"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Founder Tech"))
                .andExpect(jsonPath("$.length()").value(1));
    }
}
