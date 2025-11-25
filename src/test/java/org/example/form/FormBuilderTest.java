package org.example.form;

import org.example.ui.form.Form;
import org.example.ui.form.FormBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FormBuilderTest {

    @Test
    public void testRowsAndNewLine() {
        FormBuilder builder = new FormBuilder();
        Form form = builder.create(3);

        // add two fields in first row
        builder.addText("a","A");
        builder.addText("b","B");
        builder.newLine();

        // add one field in second row
        builder.addText("c","C");

        assertEquals(2, form.getRows().size());
        assertEquals(2, form.getRows().get(0).size());
        assertEquals(1, form.getRows().get(1).size());

        // flattened view
        assertEquals(3, form.getFields().size());
    }

    @Test
    public void testFluentAddAndNewLine() {
        FormBuilder builder = new FormBuilder();
        builder.create(2);
        builder.addText("x","X");
        builder.addText("y","Y");
        builder.newLine();
        builder.addText("z","Z");

        Form form = builder.getCurrentForm();
        assertNotNull(form);
        assertEquals(2, form.getRows().get(0).size());
        assertEquals(1, form.getRows().get(1).size());
    }
}
