package org.example.ui.field;

public abstract class Field {
    protected final String name;
    protected final String label;

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
}

