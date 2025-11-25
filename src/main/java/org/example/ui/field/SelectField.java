package org.example.ui.field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectField extends Field {
    private final List<Option> options = new ArrayList<>();
    private String selectedId;

    public SelectField(String name, String label) {
        super(name, label);
    }

    public SelectField options(List<Option> opts) {
        this.options.clear();
        if (opts != null) this.options.addAll(opts);
        return this;
    }

    public List<Option> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public SelectField selected(String id) {
        this.selectedId = id;
        return this;
    }

    public String getSelectedId() {
        return selectedId;
    }

    @Override
    public String getType() {
        return "select";
    }

    public SelectField icon(String icon) {
        super.icon(icon);
        return this;
    }

    public static class Option {
        private final String id;
        private final String label;

        public Option(String id, String label) {
            this.id = id;
            this.label = label;
        }

        public String getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }
    }
}
