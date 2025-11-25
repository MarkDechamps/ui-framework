package org.example.ui.form;

import org.example.ui.field.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Form {
    private final int columns;
    private final List<Field> fields = new ArrayList<>();

    public Form(int columns) {
        this.columns = columns;
    }

    public int getColumns() {
        return columns;
    }

    public void add(Field field) {
        fields.add(field);
    }

    public List<Field> getFields() {
        return Collections.unmodifiableList(fields);
    }
}

