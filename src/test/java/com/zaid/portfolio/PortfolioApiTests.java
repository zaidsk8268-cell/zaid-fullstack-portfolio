package com.zaid.portfolio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.zaid.portfolio.contact.ContactMessageRepository;

@SpringBootTest
class PortfolioApiTests {

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private ContactMessageRepository repository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
        repository.deleteAll();
    }

    @Test
    void servesThePortfolioHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/Index.html"));
    }

    @Test
    void returnsPortfolioData() throws Exception {
        mockMvc.perform(get("/api/portfolio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zaid Khan"))
                .andExpect(jsonPath("$.skills.length()").value(3))
                .andExpect(jsonPath("$.projects.length()").value(2));
    }

    @Test
    void savesAValidContactMessage() throws Exception {
        String request = """
                {
                  "name": "Aisha Khan",
                  "email": "aisha@example.com",
                  "subject": "Project enquiry",
                  "message": "I would like to discuss a student project with you."
                }
                """;

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.message").value("Thanks! Your message has been saved."));

        assertThat(repository.count()).isEqualTo(1);
        assertThat(repository.findAll().get(0).getEmail()).isEqualTo("aisha@example.com");
    }

    @Test
    void rejectsAnInvalidContactMessage() throws Exception {
        String request = """
                {
                  "name": "",
                  "email": "not-an-email",
                  "subject": "",
                  "message": "short"
                }
                """;

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.subject").exists())
                .andExpect(jsonPath("$.errors.message").exists());

        assertThat(repository.count()).isZero();
    }
}
