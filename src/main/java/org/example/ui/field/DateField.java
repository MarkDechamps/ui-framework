package org.example.ui.field;

public class DateField extends Field {
    private String value;

    public DateField(String name, String label) {
        super(name, label);
    }

    public String getValue() {
        return value;
    }

    public DateField value(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String getType() {
        return "date";
    }
}

