package org.example.render;

import org.example.ui.AbstractScreen;
import org.example.ui.form.Form;
import org.example.ui.form.FormBuilder;
import org.example.ui.field.Field;
import org.example.ui.field.TextField;
import org.example.ui.field.NumberField;
import org.example.ui.field.DateField;
import org.example.ui.field.SelectField;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThymeleafRenderer {
    private static final Logger log = LoggerFactory.getLogger(ThymeleafRenderer.class);
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

        // First try the Thymeleaf template rendering (keeps original behavior)
        try {
            Context ctx = new Context();
            ctx.setVariable("title", screen.getTitle());
            ctx.setVariable("form", form);
            String result = engine.process("screen", ctx);
            log.debug("Rendered screen '{}' using Thymeleaf template", screen.getTitle());
            return result;
        } catch (Throwable t) {
            // Catch everything (including NoClassDefFoundError / LinkageError)
            log.warn("Thymeleaf rendering failed, falling back to simple HTML renderer: {}", t.toString());
            return renderSimpleHtml(screen.getTitle(), form);
        }
    }

    private String renderSimpleHtml(String title, Form form) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html><html><head><meta charset=\"utf-8\"/>\n");
        sb.append("<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"/>\n");
        sb.append("<title>").append(escape(title)).append("</title>\n");
        // link the same stylesheet used by templates so fallback is styled
        sb.append("<link rel=\"stylesheet\" href=\"/css/styles.css\" />\n");
        sb.append("</head><body>\n");
        sb.append("<div class=\"page-container\">\n");
        sb.append("<header class=\"page-header\"><h1>").append(escape(title)).append("</h1></header>\n");
        sb.append("<main class=\"page-main\">\n");
        sb.append("<div class=\"form-card\">\n");
        sb.append("<form method=\"post\" class=\"form-body\">\n");
        sb.append("<div class=\"form-grid\" style=\"grid-template-columns: repeat("+form.getColumns()+", 1fr)\">\n");

        for (Field f : form.getFields()) {
            sb.append("<div class=\"form-item\">\n");
            sb.append("<label class=\"form-label\">").append(escape(f.getLabel())).append("</label>\n");

            // render icon-aware wrapper
            String icon = f.getIcon();
            boolean hasIcon = icon != null && !icon.isEmpty();

            if (f instanceof TextField) {
                TextField tf = (TextField) f;
                if (hasIcon) sb.append("<div class=\"input-with-icon\">\n");
                if (hasIcon) sb.append("<span class=\"input-icon\">\n").append(getSvgForIcon(icon)).append("</span>");
                sb.append("<input type=\"text\" class=\"input\" name=\"").append(escape(tf.getName())).append("\"");
                if (tf.getValue() != null) sb.append(" value=\"").append(escape(tf.getValue())).append("\"");
                sb.append(" />\n");
                if (hasIcon) sb.append("</div>\n");
            } else if (f instanceof NumberField) {
                NumberField nf = (NumberField) f;
                if (hasIcon) sb.append("<div class=\"input-with-icon\">\n");
                if (hasIcon) sb.append("<span class=\"input-icon\">\n").append(getSvgForIcon(icon)).append("</span>");
                sb.append("<input type=\"number\" class=\"input\" name=\"").append(escape(nf.getName())).append("\"");
                if (nf.getValue() != null) sb.append(" value=\"").append(nf.getValue()).append("\"");
                if (nf.getMin() != null) sb.append(" min=\"").append(nf.getMin()).append("\"");
                if (nf.getMax() != null) sb.append(" max=\"").append(nf.getMax()).append("\"");
                sb.append(" />\n");
                if (hasIcon) sb.append("</div>\n");
            } else if (f instanceof DateField) {
                DateField df = (DateField) f;
                if (hasIcon) sb.append("<div class=\"input-with-icon\">\n");
                if (hasIcon) sb.append("<span class=\"input-icon\">\n").append(getSvgForIcon(icon)).append("</span>");
                sb.append("<input type=\"date\" class=\"input\" name=\"").append(escape(df.getName())).append("\"");
                if (df.getValue() != null) sb.append(" value=\"").append(escape(df.getValue())).append("\"");
                sb.append(" />\n");
                if (hasIcon) sb.append("</div>\n");
            } else if (f instanceof SelectField) {
                SelectField sf = (SelectField) f;
                sb.append("<div>");
                sb.append("<select class=\"input\" name=\"").append(escape(sf.getName())).append("\">\n");
                for (SelectField.Option opt : sf.getOptions()) {
                    sb.append("<option value=\"").append(escape(opt.getId())).append("\"");
                    if (opt.getId().equals(sf.getSelectedId())) sb.append(" selected");
                    sb.append(">").append(escape(opt.getLabel())).append("</option>\n");
                }
                sb.append("</select>");
                sb.append("</div>\n");
            } else {
                sb.append("<input type=\"text\" class=\"input\" name=\"").append(escape(f.getName())).append("\" />\n");
            }

            sb.append("</div>\n");
        }

        sb.append("</div>\n");
        sb.append("<div class=\"form-actions\"><button type=\"submit\" class=\"btn primary\">Submit</button></div>\n");
        sb.append("</form>\n");
        sb.append("</div>\n");
        sb.append("</main>\n");
        sb.append("</div>\n");
        sb.append("</body></html>");
        return sb.toString();
    }

    private String getSvgForIcon(String icon) {
        if (icon == null) return "";
        switch (icon) {
            case "user":
                return "<svg width='16' height='16' viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'><path d='M12 12c2.7614 0 5-2.2386 5-5s-2.2386-5-5-5-5 2.2386-5 5 2.2386 5 5 5zM4 20.25C4 16.798 7.5817 14 12 14s8 2.798 8 6.25V22H4v-1.75z' stroke='#374151' stroke-width='1.2' stroke-linecap='round' stroke-linejoin='round'/></svg>";
            case "calendar":
                return "<svg width='16' height='16' viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'><path d='M7 10h10M7 14h4M3 7h18M7 3v4M17 3v4' stroke='#374151' stroke-width='1.2' stroke-linecap='round' stroke-linejoin='round'/></svg>";
            default:
                return "<svg width='16' height='16' viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'><path d='M3 12h18M12 3v18' stroke='#374151' stroke-width='1.2' stroke-linecap='round' stroke-linejoin='round'/></svg>";
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
