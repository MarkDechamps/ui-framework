package org.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PersoonControllerTest {

    @Test
    public void shouldReturnPersonFormWithFields() throws Exception {
        PersoonController controller = new PersoonController();
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/persoon"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("name=\"naam\"")))
                .andExpect(content().string(containsString("name=\"voornaam\"")))
                .andExpect(content().string(containsString("name=\"geboorteDatum\"")))
                .andExpect(content().string(containsString("name=\"leeftijd\"")))
                .andExpect(content().string(containsString("name=\"geslacht\"")));
    }
}

