package org.example.ui.form;

import org.example.ui.field.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Form {
    private final int columns;
    // rows of fields; each row is a list of Field
    private final List<List<Field>> rows = new ArrayList<>();

    public Form(int columns) {
        this.columns = columns;
        // start with an initial row
        rows.add(new ArrayList<>());
    }

    public int getColumns() {
        return columns;
    }

    // add field to the current (last) row
    public void add(Field field) {
        if (rows.isEmpty()) {
            rows.add(new ArrayList<>());
        }
        rows.get(rows.size() - 1).add(field);
    }

    // end the current row and start a new empty row
    public void newLine() {
        // only add a new row if the last row is not empty
        if (rows.isEmpty() || !rows.get(rows.size() - 1).isEmpty()) {
            rows.add(new ArrayList<>());
        }
    }

    // flattened view maintained for backward compatibility
    public List<Field> getFields() {
        List<Field> all = new ArrayList<>();
        for (List<Field> row : rows) {
            all.addAll(row);
        }
        return Collections.unmodifiableList(all);
    }

    // new API: access rows directly
    public List<List<Field>> getRows() {
        // return unmodifiable deep view
        List<List<Field>> unmodifiable = new ArrayList<>();
        for (List<Field> row : rows) {
            unmodifiable.add(Collections.unmodifiableList(row));
        }
        return Collections.unmodifiableList(unmodifiable);
    }
}
