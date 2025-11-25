package org.example;

import org.example.dto.PersoonDto;
import org.example.render.ThymeleafRenderer;
import org.example.screen.PersoonScreen;

public class Main {
    public static void main(String[] args) {
        PersoonDto dto = new PersoonDto();
        PersoonScreen screen = new PersoonScreen(dto);

        ThymeleafRenderer renderer = new ThymeleafRenderer();
        String html = renderer.render(screen);

        System.out.println(html);
    }
}