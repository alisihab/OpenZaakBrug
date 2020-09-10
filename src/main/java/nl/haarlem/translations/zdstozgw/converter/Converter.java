package nl.haarlem.translations.zdstozgw.converter;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZknDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;

@Data
public abstract class Converter {
	    
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
    protected Translation translation;
    protected ZaakService zaakService;
    protected RequestHandlerContext context;
    protected ZdsZknDocument zdsDocument;
    
    public Converter(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
    	this.context = context;
    	this.translation = translation;
        this.zaakService = zaakService;
    }
        
	public abstract void load() throws ResponseStatusException;
	public abstract ResponseEntity<?> execute() throws ConverterException;
}
