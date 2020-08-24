package nl.haarlem.translations.zdstozgw.converter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsStuurgegevens;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

@Data
public abstract class Converter {
    private Translation translation;
    private ZaakService zaakService;
    private RequestHandlerContext context;
    protected ZdsDocument zdsDocument;
    
    public Converter(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
    	this.context = context;
    	this.translation = translation;
        this.zaakService = zaakService;
    }
    
	public abstract void load() throws ResponseStatusException;
	public abstract ResponseEntity<?> execute() throws ConverterException;
}
