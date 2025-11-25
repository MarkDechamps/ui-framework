package org.example.render;

import org.example.screen.PersoonScreen;
import org.example.ui.AbstractScreen;
import org.example.ui.form.Form;
import org.example.ui.form.FormBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class ThymeleafRenderer {
    private final TemplateEngine engine;

    public ThymeleafRenderer() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);

        engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
    }

    public String render(AbstractScreen<?> screen) {
        FormBuilder builder = new FormBuilder();
        Form form = screen.createForm(builder, screen.dto);

        Context ctx = new Context();
        ctx.setVariable("title", screen.getTitle());
        ctx.setVariable("form", form);

        return engine.process("screen", ctx);
    }
}

