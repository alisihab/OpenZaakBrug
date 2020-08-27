package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import lombok.experimental.var;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.converter.impl.proxy.Proxy;
import nl.haarlem.translations.zdstozgw.converter.impl.translate.VoegZaakdocumentToeTranslator;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZknDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class VoegZaakdocumentToeReplicator extends VoegZaakdocumentToeTranslator {
    private Replicator replicator;
	
    public VoegZaakdocumentToeReplicator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
        super(context, translation, zaakService);
    }

	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {		
		var zdsEdcLk01 = (ZdsEdcLk01) this.getZdsDocument();		
		// replicate the zaak
		replicator.replicateZaak(zdsEdcLk01.objects.get(0).identificatie);
		// send to legacy system
		var legacyresponse = Proxy.Proxy(this.getTranslation().getLegacyservice(), this.getContext().getSoapAction(), getContext().getRequestBody());
		// do the translation
		return super.execute();
	}	
}
