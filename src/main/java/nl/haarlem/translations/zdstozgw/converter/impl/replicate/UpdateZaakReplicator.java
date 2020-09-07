package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.impl.proxy.Proxy;
import nl.haarlem.translations.zdstozgw.converter.impl.translate.UpdateZaakTranslator;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class UpdateZaakReplicator extends UpdateZaakTranslator {

	public UpdateZaakReplicator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
        super(context, translation, zaakService);
    }

	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		String rsin = this.getZaakService().getRSIN(this.zdsDocument.stuurgegevens.zender.organisatie);
		
		var zdsZakLk01 = (ZdsZakLk01) this.getZdsDocument();		
		// replicate the zaak
        var replicator = new Replicator(this.getZaakService(),zdsZakLk01.stuurgegevens);
		replicator.replicateZaak(rsin, zdsZakLk01.objects.get(0).identificatie);
		// send to legacy system
		var legacyresponse = Proxy.Proxy(this.getTranslation().getLegacyservice(), this.getContext().getSoapAction(), getContext().getRequestBody());
		// do the translation
		return super.execute();		
	}	
}
