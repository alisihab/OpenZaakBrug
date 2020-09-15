package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.impl.translate.CreeerZaakTranslator;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class CreeerZaakReplicator extends CreeerZaakTranslator {
	
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	
    public CreeerZaakReplicator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
        super(context, translation, zaakService);
    }

	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		var zdsZakLk01 = (ZdsZakLk01) this.getZdsDocument();
		// is new, no need to replicate

		// send to legacy system
		var url = this.getTranslation().getLegacyservice();
		var soapaction = this.getTranslation().getSoapAction();
		var request = context.getRequestBody();
		log.info("relaying request to url: " + url + " with soapaction: " + soapaction + " request-size:" + request.length());
		var legacyresponse = this.zaakService.zdsClient.post(url, soapaction, request);
		
		// quit ont error
		if(legacyresponse.getStatusCode() != HttpStatus.OK) {
			log.warn("Service:" + this.getTranslation().getLegacyservice() +  " SoapAction: " +   this.getContext().getSoapAction());
			return legacyresponse;
		}
		
		// do the translation
		return super.execute();
	}
}