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

import java.util.List;
import org.example.ui.field.ReferenceField;

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

    // Additional constructor to support unit testing with a mock TemplateEngine
    ThymeleafRenderer(TemplateEngine engine) {
        this.engine = engine;
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
        sb.append("<link rel=\"stylesheet\" href=\"/css/styles.css?v=20251126\" />\n");
        sb.append("</head><body>\n");
        sb.append("<div class=\"page-container\">\n");
        sb.append("<header class=\"page-header\"><h1>").append(escape(title)).append("</h1></header>\n");
        sb.append("<main class=\"page-main\">\n");
        sb.append("<div class=\"form-card\">\n");
        sb.append("<form method=\"post\" class=\"form-body\">\n");
        sb.append("<div class=\"form-rows\">\n");

        List<List<Field>> rows = form.getRows();
        for (List<Field> row : rows) {
            sb.append("<div class=\"form-row\" style=\"grid-template-columns: repeat("+form.getColumns()+", 1fr)\">\n");
            for (Field f : row) {
                sb.append("<div class=\"form-item\">\n");
                sb.append("<label class=\"form-label\">").append(escape(f.getLabel())).append("</label>\n");

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
                } else if (f instanceof ReferenceField) {
                    ReferenceField rf = (ReferenceField) f;
                    sb.append("<div class=\"reference-field\" style=\"display:flex;gap:8px;align-items:center;\">\n");
                    sb.append("<input type=\"text\" class=\"input\" name=\"")
                      .append(escape(rf.getName())).append("_code\"")
                      .append(" value=\"").append(escape(nullToEmpty(rf.getCode()))).append("\"")
                      .append(" placeholder=\"code\" style=\"width:120px;\"")
                      .append(" data-lookup-url=\"").append(escape(nullToEmpty(rf.getLookupUrl()))).append("\"")
                      .append(" />\n");
                    sb.append("<span class=\"reference-name\">").append(escape(nullToEmpty(rf.getDisplayName()))).append("</span>\n");
                    sb.append("<button type=\"button\" class=\"btn icon\" title=\"Zoeken\" aria-label=\"Zoek\" data-lookup-url=\"")
                      .append(escape(nullToEmpty(rf.getLookupUrl()))).append("\">");
                    sb.append("<svg width=\"16\" height=\"16\" viewBox=\"0 0 24 24\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\"><path d=\"M21 21l-4.35-4.35M10 18a8 8 0 100-16 8 8 0 000 16z\" stroke=\"#374151\" stroke-width=\"1.5\" stroke-linecap=\"round\" stroke-linejoin=\"round\"/></svg>");
                    sb.append("</button>\n");
                    sb.append("<input type=\"hidden\" name=\"").append(escape(rf.getName())).append("\" value=\"")
                      .append(escape(nullToEmpty(rf.getIdValue()))).append("\" />\n");
                    sb.append("</div>\n");
                } else {
                    sb.append("<input type=\"text\" class=\"input\" name=\"").append(escape(f.getName())).append("\" />\n");
                }

                sb.append("</div>\n");
            }
            sb.append("</div>\n");
        }

        sb.append("</div>\n");
        sb.append("<div class=\"form-actions\"><button type=\"submit\" class=\"btn primary\">Submit</button></div>\n");
        sb.append("</form>\n");
        sb.append("</div>\n");
        sb.append("</main>\n");
        sb.append("</div>\n");

        // Modal markup for fallback mode
        sb.append("<div id=\"lookup-overlay\" class=\"lookup-overlay\" aria-hidden=\"true\" hidden></div>\n");
        sb.append("<div id=\"lookup-modal\" class=\"lookup-modal\" role=\"dialog\" aria-modal=\"true\" aria-labelledby=\"lookup-title\" hidden>\n");
        sb.append("  <div class=\"lookup-header\">\n");
        sb.append("    <h3 id=\"lookup-title\" class=\"lookup-title\">Zoek waarde</h3>\n");
        sb.append("    <button type=\"button\" class=\"btn secondary lookup-close\" aria-label=\"Sluiten\">×</button>\n");
        sb.append("  </div>\n");
        sb.append("  <div class=\"lookup-body\">\n");
        sb.append("    <input id=\"lookup-input\" class=\"input\" type=\"text\" placeholder=\"Typ om te filteren...\" />\n");
        sb.append("    <ul id=\"lookup-list\" class=\"lookup-list\" role=\"listbox\" aria-label=\"Zoekresultaten\"></ul>\n");
        sb.append("  </div>\n");
        sb.append("  <div class=\"lookup-footer\">\n");
        sb.append("    <button type=\"button\" class=\"btn secondary lookup-cancel\">Annuleer</button>\n");
        sb.append("  </div>\n");
        sb.append("</div>\n");

        // Inline JS for modal behavior in fallback
        sb.append("<script>(function(){\n"
                + "var overlay=document.getElementById('lookup-overlay');\n"
                + "var modal=document.getElementById('lookup-modal');\n"
                + "var inputEl=document.getElementById('lookup-input');\n"
                + "var listEl=document.getElementById('lookup-list');\n"
                + "function show(el){el.hidden=false;el.setAttribute('aria-hidden','false');}\n"
                + "function hide(el){el.hidden=true;el.setAttribute('aria-hidden','true');}\n"
                + "var ctx=null;\n"
                + "function openModal(c){ctx=c;inputEl.value=(c.codeInput&&c.codeInput.value)||'';render([]);show(overlay);show(modal);setTimeout(function(){inputEl.focus();inputEl.select();},0);fetchAndRender(inputEl.value);}\n"
                + "function closeModal(){hide(modal);hide(overlay);if(ctx&&ctx.codeInput){ctx.codeInput.focus();}ctx=null;}\n"
                + "function render(items){listEl.innerHTML='';items.forEach(function(it){var li=document.createElement('li');li.className='lookup-item';li.setAttribute('role','option');li.tabIndex=0;li.dataset.id=it.id||'';li.dataset.code=it.code||'';li.dataset.name=it.name||'';li.textContent=(it.code?it.code+' — ':'')+(it.name||'');li.addEventListener('dblclick',function(){select(li);});li.addEventListener('keydown',function(e){if(e.key==='Enter'){select(li);}});listEl.appendChild(li);});}\n"
                + "function fetchAndRender(q){if(!ctx||!ctx.url)return;var u=ctx.url+'?code='+encodeURIComponent(q||'');fetch(u).then(function(r){return r.ok?r.json():[];}).then(function(list){render(Array.isArray(list)?list:[]);}).catch(function(){render([]);});}\n"
                + "var t=null;inputEl.addEventListener('input',function(){clearTimeout(t);var q=this.value;t=setTimeout(function(){fetchAndRender(q);},180);});\n"
                + "function select(li){if(!ctx)return;var code=li.dataset.code||'';var name=li.dataset.name||'';var id=li.dataset.id||code||'';if(ctx.codeInput)ctx.codeInput.value=code;if(ctx.nameSpan)ctx.nameSpan.textContent=name;if(ctx.hidden)ctx.hidden.value=id;closeModal();}\n"
                + "document.addEventListener('click',function(e){var btn=e.target.closest('button.btn.icon[data-lookup-url]');if(btn){var w=btn.closest('.reference-field');if(!w)return;var code=w.querySelector('input.input[data-lookup-url]')||w.querySelector('input.input');var name=w.querySelector('.reference-name');var hid=w.querySelector('input[type=\"hidden\"]');var url=btn.getAttribute('data-lookup-url')||(code&&code.getAttribute('data-lookup-url'));if(url){openModal({url:url,wrapper:w,codeInput:code,nameSpan:name,hidden:hid});}}});\n"
                + "Array.prototype.slice.call(document.querySelectorAll('.lookup-close,.lookup-cancel')).forEach(function(b){b.addEventListener('click',closeModal);});\n"
                + "overlay.addEventListener('click',closeModal);\n"
                + "document.addEventListener('keydown',function(e){if(e.key==='Escape'&&!modal.hidden){closeModal();}});\n"
                + "// Submit handler: bouw DTO en log naar console\n"
                + "var form=document.querySelector('form.form-body');\n"
                + "if(form){form.addEventListener('submit',function(ev){ev.preventDefault();var dto={};\n"
                + "var fields=Array.prototype.slice.call(form.querySelectorAll('input, select, textarea'));\n"
                + "var handled={};\n"
                + "fields.forEach(function(el){if(el.type==='hidden'){var name=el.name;var w=el.closest('.reference-field');if(w){var code=w.querySelector('input.input[name$=\"_code\"]');var nm=w.querySelector('.reference-name');dto[name]={id:el.value||'',code:code?code.value:'',name:nm?nm.textContent:''};handled[name]=true;handled[name+'_code']=true;}}});\n"
                + "fields.forEach(function(el){if(!el.name||handled[el.name])return;if(el.type==='hidden')return;dto[el.name]=el.value;});\n"
                + "try{console.log('DTO submitted:',dto);console.log('DTO (json):',JSON.stringify(dto,null,2));}catch(e){console.log('DTO submitted (raw):',dto);} });}\n"
                + "})();</script>");

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

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
