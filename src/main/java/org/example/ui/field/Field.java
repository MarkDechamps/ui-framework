package org.example.ui.field;

public abstract class Field {
    protected final String name;
    protected final String label;
    private String icon; // optional icon key (e.g. "calendar", "user")

    protected Field(String name, String label) {
        this.name = name;
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public abstract String getType();

    // optional icon support
    public Field icon(String icon) {
        this.icon = icon;
        return this;
    }

    public String getIcon() {
        return icon;
    }
}
