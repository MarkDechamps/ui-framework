package org.example.ui;

import org.example.controller.PersoonController;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifieert dat de pagina de lookup-modal correct rendert en standaard verborgen is.
 * Deze test voert geen JavaScript uit, maar checkt de HTML-markup en attributen
 * die noodzakelijk zijn om de modal via JS te tonen.
 */
public class LookupModalRenderTest {

    @Test
    @DisplayName("Lookup modal staat in de HTML en is standaard verborgen (hidden)")
    void modalMarkupExistsAndHiddenByDefault() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new PersoonController()).build();

        MvcResult res = mockMvc.perform(get("/persoon").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andReturn();

        String html = res.getResponse().getContentAsString();
        assertNotNull(html);

        Document doc = Jsoup.parse(html);

        // overlay en modal aanwezig
        Element overlay = doc.getElementById("lookup-overlay");
        Element modal = doc.getElementById("lookup-modal");
        assertNotNull(overlay, "lookup-overlay moet aanwezig zijn");
        assertNotNull(modal, "lookup-modal moet aanwezig zijn");

        // standaard verborgen via 'hidden' attribuut (JS zet dit uit bij openen)
        assertTrue(overlay.hasAttr("hidden"), "overlay moet hidden attribuut hebben");
        assertTrue(modal.hasAttr("hidden"), "modal moet hidden attribuut hebben");

        // controleer dat de lookup-knop en code-input de data-lookup-url bezitten
        Element lookupBtn = doc.selectFirst(".reference-field button.btn.icon[data-lookup-url]");
        Element codeInput = doc.selectFirst(".reference-field input.input[data-lookup-url]");
        assertNotNull(lookupBtn, "lookup-zoekknop met data-lookup-url ontbreekt");
        assertNotNull(codeInput, "code-input met data-lookup-url ontbreekt");
        String urlBtn = lookupBtn.attr("data-lookup-url");
        String urlInput = codeInput.attr("data-lookup-url");
        assertFalse(urlBtn == null || urlBtn.isEmpty(), "data-lookup-url op knop mag niet leeg zijn");
        assertFalse(urlInput == null || urlInput.isEmpty(), "data-lookup-url op input mag niet leeg zijn");

        // input- en lijst-elementen binnen de modal
        assertNotNull(doc.getElementById("lookup-input"), "zoek-input in modal moet bestaan");
        assertNotNull(doc.getElementById("lookup-list"), "resultaten-lijst in modal moet bestaan");
    }
}
