package org.example.ui.field;

/**
 * ReferenceField: samengesteld veld met een code-input, een leesbare naam en een zoek-knop.
 * Voorbeeld: postcode (code) + gemeentenaam (label) met een lookup endpoint.
 */
public class ReferenceField extends Field {
    private String idValue;       // interne id van de reference (optioneel)
    private String code;          // invoerbare code (bv. postcode)
    private String displayName;   // getoonde naam (bv. Kortrijk)
    private String lookupUrl;     // endpoint om via code te zoeken

    public ReferenceField(String name, String label) {
        super(name, label);
    }

    // extra convenience constructor wanneer je meteen een code wil meegeven
    public ReferenceField(String name, String label, String code) {
        super(name, label);
        this.code = code;
    }

    public ReferenceField idValue(String id) {
        this.idValue = id;
        return this;
    }

    public ReferenceField code(String code) {
        this.code = code;
        return this;
    }

    public ReferenceField displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ReferenceField lookupUrl(String lookupUrl) {
        this.lookupUrl = lookupUrl;
        return this;
    }

    public String getIdValue() {
        return idValue;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLookupUrl() {
        return lookupUrl;
    }

    @Override
    public String getType() {
        return "reference";
    }
}
