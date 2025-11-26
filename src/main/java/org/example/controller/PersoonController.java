// java
package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.example.screen.PersoonScreen;
import org.example.render.ThymeleafRenderer;
import org.example.dto.PersoonDto;
import org.example.dto.PostcodeDto;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import java.util.*;

@Controller
@RequestMapping("/persoon")
public class PersoonController {

    private final ThymeleafRenderer renderer;

    // Primary constructor for DI
    public PersoonController(ThymeleafRenderer renderer) {
        this.renderer = renderer;
    }

    // Backwards-compatible no-arg constructor for tests that instantiate directly
    @Deprecated
    public PersoonController() {
        this(new ThymeleafRenderer());
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getPersoon() {
        PersoonDto dto = new PersoonDto();
        PersoonScreen screen = new PersoonScreen(dto);
        String html = renderer.render(screen);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    // Simpele demo-lookup voor postcodes. In echte app zou dit service/DB call zijn.
    @GetMapping(value = "/findPostCodeById", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PostcodeDto>> findPostCodeById(@RequestParam("code") String code) {
        var results = new ArrayList<PostcodeDto>();
        // dummy data
        addResult(results, "8500", "Kortrijk");
        addResult(results, "8501", "Bissegem");
        addResult(results, "1000", "Brussel");

        // filter simpel op prefix match
        var c = (code == null) ? "" : code.trim();
        var filtered = results.stream()
                .filter(r -> !StringUtils.hasText(c) || r.getCode().startsWith(c))
                .toList();
        if (filtered.isEmpty()) {
            // als geen match, en code exact bestaat, retourneer één item met die code als naam onbepaald
            filtered = List.of(new PostcodeDto(c, c, ""));
        }
        return ResponseEntity.ok(filtered);
    }

    // htmx: return a fragment with <li class="lookup-item"> entries
    @GetMapping(value = "/lookup/postcodes", produces = MediaType.TEXT_HTML_VALUE)
    public String lookupPostcodes(@RequestParam(name = "code", required = false) String code,
                                  @RequestParam(name = "ref", required = false) String ref,
                                  Model model) {
        // Reuse the same filtering logic as JSON endpoint
        List<PostcodeDto> results = new ArrayList<>();
        addResult(results, "8500", "Kortrijk");
        addResult(results, "8501", "Bissegem");
        addResult(results, "1000", "Brussel");

        var c = (code == null) ? "" : code.trim();
        var filtered = results.stream()
                .filter(r -> !StringUtils.hasText(c) || r.getCode().startsWith(c))
                .toList();
        if (filtered.isEmpty()) {
            filtered = List.of(new PostcodeDto(c, c, ""));
        }
        model.addAttribute("list", filtered);
        model.addAttribute("ref", ref == null ? "postcode" : ref);
        return "fragments/postcodes :: items";
    }

    // Optional selection endpoint (reserved for future OOB swap usage)
    @PostMapping(value = "/lookup/select", produces = MediaType.TEXT_HTML_VALUE)
    public String selectPostcode(@RequestParam("id") String id,
                                 @RequestParam("code") String code,
                                 @RequestParam("name") String name,
                                 @RequestParam(name = "ref", required = false, defaultValue = "postcode") String ref,
                                 Model model) {
        model.addAttribute("id", id);
        model.addAttribute("code", code);
        model.addAttribute("name", name);
        model.addAttribute("ref", ref);
        return "fragments/postcodes :: selectionOob";
    }

    private static void addResult(List<PostcodeDto> list, String code, String name) {
        list.add(new PostcodeDto(code, code, name));
    }
}