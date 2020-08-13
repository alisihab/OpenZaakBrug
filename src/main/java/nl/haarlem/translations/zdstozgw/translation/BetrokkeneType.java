package nl.haarlem.translations.zdstozgw.translation;

public enum BetrokkeneType {
    NATUURLIJK_PERSOON("natuurlijk_persoon"),
    NIET_NATUURLIJK_PERSOON("niet_natuurlijk_persoon"),
    VESTIGING("vestiging"),
    ORGANISATORISCHE_EENHEID("organisatorische_eenheid"),
    MEDEWERKER("medewerker");

    String description;

    BetrokkeneType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
