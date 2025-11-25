package org.example.render;

import org.example.ui.AbstractScreen;
import org.example.ui.form.Form;
import org.example.ui.form.FormBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

public class ThymeleafRenderer {
    private final TemplateEngine engine;

    public ThymeleafRenderer() {
        ITemplateResolver resolver = new ClassLoaderTemplateResolver();
        ((ClassLoaderTemplateResolver) resolver).setPrefix("/templates/");
        ((ClassLoaderTemplateResolver) resolver).setSuffix(".html");
        ((ClassLoaderTemplateResolver) resolver).setTemplateMode("HTML");
        ((ClassLoaderTemplateResolver) resolver).setCharacterEncoding("UTF-8");
        ((ClassLoaderTemplateResolver) resolver).setCacheable(false);

        engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
    }

    // Public entry keeps wildcard for callers, delegate to a typed helper to capture the wildcard
    public String render(AbstractScreen<?> screen) {
        return renderTyped(screen);
    }

    // Helper that captures the screen's generic type so createForm and getDto use the same T
    @SuppressWarnings("unchecked")
    private <T> String renderTyped(AbstractScreen<T> screen) {
        FormBuilder builder = new FormBuilder();
        // explicit unchecked cast to satisfy compilers that complain about wildcard capture
        T dto = (T) screen.getDto();
        Form form = screen.createForm(builder, dto);

        Context ctx = new Context();
        ctx.setVariable("title", screen.getTitle());
        ctx.setVariable("form", form);

        return engine.process("screen", ctx);
    }
}
