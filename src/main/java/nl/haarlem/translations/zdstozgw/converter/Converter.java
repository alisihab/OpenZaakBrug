package nl.haarlem.translations.zdstozgw.converter;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

@Data
public abstract class Converter {

    private Translation translation;
    private ZaakService zaakService;

    public Converter(Translation translation, ZaakService zaakService) {
        this.translation = translation;
        this.zaakService = zaakService;
    }

    public abstract String convert(String request);

}
