package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.impl.proxy.Proxy;
import nl.haarlem.translations.zdstozgw.converter.impl.translate.GeefZaakdocumentLezenTranslator;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class GeefZaakdocumentLezenReplicator extends GeefZaakdocumentLezenTranslator {
    private Replicator replicator;	

    public GeefZaakdocumentLezenReplicator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
        super(context, translation, zaakService);
        replicator = new Replicator(zaakService);
    }

	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		var zdsEdcLv01 = (ZdsEdcLv01) this.getZdsDocument();
		// replicate the zaak
		replicator.replicateZaak(zdsEdcLv01.zdsScope.object.isRelevantVoor.gerelateerde.identificatie);
		
		// send to legacy system
		var legacyresponse = Proxy.Proxy(this.getTranslation().getLegacyservice(), this.getContext().getSoapAction(), getContext().getRequestBody());
		// do the translation
		return super.execute();		
	}		
}

