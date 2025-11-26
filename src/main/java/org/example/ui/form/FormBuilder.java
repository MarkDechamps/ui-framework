package org.example.ui.form;

import org.example.ui.field.DateField;
import org.example.ui.field.Field;
import org.example.ui.field.NumberField;
import org.example.ui.field.SelectField;
import org.example.ui.field.TextField;
import org.example.ui.field.ReferenceField;

public class FormBuilder {

    private Form currentForm;

    public Form create(int columns) {
        currentForm = new Form(columns);
        return currentForm;
    }

    public TextField text(String name, String label) {
        return new TextField(name, label);
    }

    public NumberField number(String name, String label, Integer min, Integer max) {
        return new NumberField(name, label, min, max);
    }

    public DateField date(String name, String label) {
        return new DateField(name, label);
    }

    public SelectField select(String name, String label) {
        return new SelectField(name, label);
    }

    public ReferenceField reference(String name, String label) {
        return new ReferenceField(name, label);
    }

    // convenience: add field to the current form (fluent)
    public FormBuilder add(Field field) {
        if (currentForm == null) throw new IllegalStateException("No form created. Call create(int) first.");
        currentForm.add(field);
        return this;
    }

    // convenience: end current row on the current form (fluent)
    public FormBuilder newLine() {
        if (currentForm == null) throw new IllegalStateException("No form created. Call create(int) first.");
        currentForm.newLine();
        return this;
    }

    public Form getCurrentForm() {
        return currentForm;
    }

    // convenience combined methods: create field, add to current form, return the field for further configuration
    public TextField addText(String name, String label) {
        TextField f = new TextField(name, label);
        add(f);
        return f;
    }

    public NumberField addNumber(String name, String label, Integer min, Integer max) {
        NumberField f = new NumberField(name, label, min, max);
        add(f);
        return f;
    }

    public DateField addDate(String name, String label) {
        DateField f = new DateField(name, label);
        add(f);
        return f;
    }

    public SelectField addSelect(String name, String label) {
        SelectField f = new SelectField(name, label);
        add(f);
        return f;
    }

    public ReferenceField addReference(String name, String label) {
        ReferenceField f = new ReferenceField(name, label);
        add(f);
        return f;
    }

}
