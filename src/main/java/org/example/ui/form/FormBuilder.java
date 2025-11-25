package org.example.ui.form;

import org.example.ui.field.DateField;
import org.example.ui.field.Field;
import org.example.ui.field.NumberField;
import org.example.ui.field.SelectField;
import org.example.ui.field.TextField;

public class FormBuilder {

    public Form create(int columns) {
        return new Form(columns);
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

}

