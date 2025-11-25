package org.example.screen;

import org.example.dto.PersoonDto;
import org.example.ui.AbstractScreen;
import org.example.ui.form.Form;
import org.example.ui.form.FormBuilder;

public class PersoonScreen extends AbstractScreen<PersoonDto> {

    public PersoonScreen(PersoonDto dto) {
        super("Persoon scherm", dto);
    }

    @Override
    public Form createForm(FormBuilder formBuilder, PersoonDto persoon) {
        Form form = formBuilder.create(3);
        form.add(persoon.naam);
        form.add(persoon.voornaam);
        form.add(persoon.geboorteDatum);
        form.add(persoon.leeftijd);
        form.add(persoon.geslacht);
        return form;
    }
}

