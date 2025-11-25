package org.example.ui;

import org.example.ui.form.Form;
import org.example.ui.form.FormBuilder;

public abstract class AbstractScreen<T> implements Screen {

    private final String title;
    protected final T dto;

    protected AbstractScreen(String title, T dto) {
        this.title = title;
        this.dto = dto;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public T getDto() {
        return dto;
    }

    public abstract Form createForm(FormBuilder formBuilder, T dto);
}
