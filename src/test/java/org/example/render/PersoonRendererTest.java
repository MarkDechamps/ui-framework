package org.example.render;

import org.example.dto.PersoonDto;
import org.example.screen.PersoonScreen;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PersoonRendererTest {

    @Test
    public void fallbackProducesRowsAndNamesWhenThymeleafFails() {
        // create a mock engine that throws when process is called to simulate NoClassDefFoundError
        TemplateEngine mockEngine = mock(TemplateEngine.class);
        when(mockEngine.process(anyString(), any())).thenThrow(new NoClassDefFoundError("ognl/PropertyAccessor"));

        ThymeleafRenderer renderer = new ThymeleafRenderer(mockEngine);

        PersoonDto dto = new PersoonDto();
        PersoonScreen screen = new PersoonScreen(dto);

        String html = renderer.render(screen);
        assertNotNull(html);

        Document doc = Jsoup.parse(html);

        // check that we have a row container
        assertFalse(doc.select(".form-rows").isEmpty(), "form-rows container should be present");

        // check that the first row has grid-template-columns inline style
        assertFalse(doc.select(".form-row").isEmpty(), "there should be at least one form-row");
        String style = doc.selectFirst(".form-row").attr("style");
        assertTrue(style != null && style.contains("grid-template-columns"), "form-row should have grid-template-columns style");

        // first row should contain the first two fields (naam and voornaam)
        int firstRowItems = doc.selectFirst(".form-row").select(".form-item").size();
        assertTrue(firstRowItems >= 2, "first row should contain at least two form items");

        // check that the input names we expect are present somewhere in the document
        assertFalse(doc.select("input[name=naam]").isEmpty(), "naam input should be present");
        assertFalse(doc.select("input[name=voornaam]").isEmpty(), "voornaam input should be present");
        assertFalse(doc.select("input[name=geboorteDatum]").isEmpty(), "geboorteDatum input should be present");
        assertFalse(doc.select("input[name=leeftijd]").isEmpty(), "leeftijd input should be present");
        assertFalse(doc.select("select[name=geslacht]").isEmpty(), "geslacht select should be present");
    }

    @Test
    public void whenThymeleafAvailableUseIt() {
        TemplateEngine mockEngine = mock(TemplateEngine.class);
        when(mockEngine.process(anyString(), any())).thenReturn("<html><body>THYMELEAF</body></html>");

        ThymeleafRenderer renderer = new ThymeleafRenderer(mockEngine);

        PersoonDto dto = new PersoonDto();
        PersoonScreen screen = new PersoonScreen(dto);

        String html = renderer.render(screen);
        assertNotNull(html);
        assertTrue(html.contains("THYMELEAF"), "Should use template engine when available");
    }
}
