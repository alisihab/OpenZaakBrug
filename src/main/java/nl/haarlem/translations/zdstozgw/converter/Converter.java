package nl.haarlem.translations.zdstozgw.converter;

import org.springframework.web.server.ResponseStatusException;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsStuurgegevens;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

@Data
public abstract class Converter {

    private Translation translation;
    private ZaakService zaakService;

    public Converter(Translation translation, ZaakService zaakService) {
        this.translation = translation;
        this.zaakService = zaakService;
    }
    
	public abstract ZdsDocument load(String request) throws ResponseStatusException;
	public abstract ZdsDocument execute(ZdsDocument document) throws ConverterException;
}
