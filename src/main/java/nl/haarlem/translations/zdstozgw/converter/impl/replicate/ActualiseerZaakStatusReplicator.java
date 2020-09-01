package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.impl.proxy.Proxy;
import nl.haarlem.translations.zdstozgw.converter.impl.translate.ActualiseerZaakStatusTranslator;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01ActualiseerZaakstatus;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class ActualiseerZaakStatusReplicator extends ActualiseerZaakStatusTranslator {
    public ActualiseerZaakStatusReplicator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
        super(context, translation, zaakService);
    }

	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		var zdsZakLk01ActualiseerZaakstatus = (ZdsZakLk01ActualiseerZaakstatus) this.getZdsDocument();
		// replicate the zaak
        var replicator = new Replicator(this.getZaakService(), zdsZakLk01ActualiseerZaakstatus.stuurgegevens);		
		replicator.replicateZaak(zdsZakLk01ActualiseerZaakstatus.objects.get(0).identificatie);		
		// send to legacy system
		var legacyresponse = Proxy.Proxy(this.getTranslation().getLegacyservice(), this.getContext().getSoapAction(), getContext().getRequestBody());
		// do the translation
		return super.execute();
	}
}
