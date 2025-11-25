// java
package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.example.screen.PersoonScreen;
import org.example.render.ThymeleafRenderer;
import org.example.dto.PersoonDto;

@Controller
@RequestMapping("/persoon")
public class PersoonController {

    private final ThymeleafRenderer renderer = new ThymeleafRenderer();

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getPersoon() {
        PersoonDto dto = new PersoonDto();
        PersoonScreen screen = new PersoonScreen(dto);
        String html = renderer.render(screen);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }
}