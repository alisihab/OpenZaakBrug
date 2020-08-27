package nl.haarlem.translations.zdstozgw.converter;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.model.Organisatie;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZknDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.client.ZDSClient;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsStuurgegevens;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

@Data
public abstract class Converter {
	    
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
    private Translation translation;
    private ZaakService zaakService;
    private RequestHandlerContext context;
    protected ZdsZknDocument zdsDocument;
    
    public Converter(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
    	this.context = context;
    	this.translation = translation;
        this.zaakService = zaakService;
    }
        
	public abstract void load() throws ResponseStatusException;
	public abstract ResponseEntity<?> execute() throws ConverterException;
}
