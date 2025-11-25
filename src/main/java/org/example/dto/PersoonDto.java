package org.example.dto;

import org.example.ui.field.SelectField;
import org.example.ui.field.TextField;
import org.example.ui.field.NumberField;
import org.example.ui.field.DateField;

import java.util.List;
import java.util.ArrayList;

public class PersoonDto extends BaseDto {
    public final TextField naam = new TextField("naam", "Naam").maxLength(50);
    public final TextField voornaam = new TextField("voornaam", "Voornaam").maxLength(50);
    public final DateField geboorteDatum = new DateField("geboorteDatum", "Geboorte datum");
    public final NumberField leeftijd = new NumberField("leeftijd", "Leeftijd", 0, 150);
    public final SelectField geslacht = new SelectField("geslacht", "Geslacht");

    public PersoonDto() {
        List<SelectField.Option> opts = new ArrayList<>();
        opts.add(new SelectField.Option("M", "Man"));
        opts.add(new SelectField.Option("V", "Vrouw"));
        this.geslacht.options(opts);

        // set icons after construction to preserve concrete types for chaining
        this.naam.icon("user");
        this.voornaam.icon("user");
        this.geboorteDatum.icon("calendar");
        this.leeftijd.icon("hash");
    }
}
