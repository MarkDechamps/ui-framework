package org.example.ui.field;

public class TextField extends Field {
    private Integer maxLength;
    private String value;

    public TextField(String name, String label) {
        super(name, label);
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public TextField maxLength(Integer maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public String getValue() {
        return value;
    }

    public TextField value(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String getType() {
        return "text";
    }

    // Concrete fluent override for icon to preserve TextField type in chaining
    public TextField icon(String icon) {
        super.icon(icon);
        return this;
    }
}
