// java
package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.example.screen.PersoonScreen;
import org.example.render.ThymeleafRenderer;
import org.example.dto.PersoonDto;
import java.util.*;

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

    // Simpele demo-lookup voor postcodes. In echte app zou dit service/DB call zijn.
    @GetMapping(value = "/findPostCodeById", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, String>>> findPostCodeById(@RequestParam("code") String code) {
        List<Map<String, String>> results = new ArrayList<>();
        // dummy data
        addResult(results, "8500", "Kortrijk");
        addResult(results, "8501", "Bissegem");
        addResult(results, "1000", "Brussel");

        // filter simpel op prefix match
        String c = code == null ? "" : code.trim();
        List<Map<String, String>> filtered = new ArrayList<>();
        for (Map<String, String> r : results) {
            if (c.isEmpty() || r.get("code").startsWith(c)) {
                filtered.add(r);
            }
        }
        if (filtered.isEmpty()) {
            // als geen match, en code exact bestaat, retourneer één item met die code als naam onbepaald
            Map<String, String> single = new HashMap<>();
            single.put("id", c);
            single.put("code", c);
            single.put("name", "");
            filtered.add(single);
        }
        return ResponseEntity.ok(filtered);
    }

    private static void addResult(List<Map<String,String>> list, String code, String name) {
        Map<String, String> m = new HashMap<>();
        m.put("id", code);
        m.put("code", code);
        m.put("name", name);
        list.add(m);
    }
}