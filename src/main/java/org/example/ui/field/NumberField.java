package org.example.ui.field;

public class NumberField extends Field {
    private Integer min;
    private Integer max;
    private Integer value;

    public NumberField(String name, String label, Integer min, Integer max) {
        super(name, label);
        this.min = min;
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    public Integer getValue() {
        return value;
    }

    public NumberField value(Integer value) {
        this.value = value;
        return this;
    }

    @Override
    public String getType() {
        return "number";
    }

    public NumberField icon(String icon) {
        super.icon(icon);
        return this;
    }
}
