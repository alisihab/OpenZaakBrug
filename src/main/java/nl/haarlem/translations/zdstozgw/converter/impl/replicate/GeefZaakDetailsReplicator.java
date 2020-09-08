package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.impl.proxy.Proxy;
import nl.haarlem.translations.zdstozgw.converter.impl.translate.GeefZaakDetailsTranslator;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class GeefZaakDetailsReplicator extends GeefZaakDetailsTranslator {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	
	public GeefZaakDetailsReplicator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
        super(context, translation, zaakService);
    }
	
	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		String rsin = this.getZaakService().getRSIN(this.zdsDocument.stuurgegevens.zender.organisatie);
		
		var zdsZakLv01 = (ZdsZakLv01) this.getZdsDocument();
		// replicate the zaak
        var replicator = new Replicator(this.getZaakService(), zdsZakLv01.stuurgegevens);
		replicator.replicateZaak(rsin, zdsZakLv01.gelijk.identificatie);		

		// send to legacy system
		var legacyresponse = Proxy.Proxy(this.getTranslation().getLegacyservice(), this.getContext().getSoapAction(), getContext().getRequestBody());
		
		// quit ont error
		if(legacyresponse.getStatusCode() != HttpStatus.OK) {
			log.warn("Service:" + this.getTranslation().getLegacyservice() +  " SoapAction: " +   this.getContext().getSoapAction());
			return legacyresponse;
		}		
		
		// do the translation
		return super.execute();		
	}		
}
