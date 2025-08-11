package com.slawekle.GithubListing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GithubListControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnRepositoriesForValidUser() throws Exception {
        mockMvc.perform(get("/githublist/user/Slawek-Le"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].['Repository name']").isNotEmpty())
                .andExpect(jsonPath("$[0].['Owner login']").isNotEmpty())
                .andExpect(jsonPath("$[0].Branches").isNotEmpty())
                .andExpect(jsonPath("$[0].Branches").isArray())
                .andExpect(jsonPath("$[0].Branches[0].['Branch name']").isNotEmpty())
                .andExpect(jsonPath("$[0].Branches[0].['Last commit sha']").isNotEmpty())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void shouldReturnNotFoundForInvalidUser() throws Exception {
        mockMvc.perform(get("/githublist/user/nonexistentuser123456789"))
                .andExpect(status().is4xxClientError());
    }
}