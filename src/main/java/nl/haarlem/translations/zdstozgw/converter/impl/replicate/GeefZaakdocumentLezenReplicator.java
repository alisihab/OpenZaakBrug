package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.impl.proxy.Proxy;
import nl.haarlem.translations.zdstozgw.converter.impl.translate.GeefZaakdocumentLezenTranslator;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsStuurgegevens;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class GeefZaakdocumentLezenReplicator extends GeefZaakdocumentLezenTranslator {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	
	public GeefZaakdocumentLezenReplicator(RequestHandlerContext context, Translation translation, ZaakService zaakService, ZdsStuurgegevens stuurgegevens) {
        super(context, translation, zaakService);
    }

	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		String rsin = this.getZaakService().getRSIN(this.zdsDocument.stuurgegevens.zender.organisatie);
				
		var zdsEdcLv01 = (ZdsEdcLv01) this.getZdsDocument();
		// replicate the zaak
        var replicator = new Replicator(this.getZaakService(), zdsEdcLv01.stuurgegevens);		
		replicator.replicateZaak(rsin, zdsEdcLv01.zdsScope.object.isRelevantVoor.gerelateerde.identificatie);		

		// send to legacy system
		var url = this.getTranslation().getLegacyservice();
		var soapaction = this.getTranslation().getLegacyservice();
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

