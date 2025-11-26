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
        formBuilder.add(persoon.naam);
        formBuilder.add(persoon.voornaam);
        formBuilder.newLine();
        formBuilder.add(persoon.geboorteDatum);
        formBuilder.add(persoon.leeftijd);
        formBuilder.add(persoon.geslacht);
        formBuilder.newLine();
        formBuilder.add(persoon.postcode);
        return form;
    }
}
