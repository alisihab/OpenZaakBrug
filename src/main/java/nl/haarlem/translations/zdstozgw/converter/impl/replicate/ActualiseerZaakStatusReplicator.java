package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.impl.translate.ActualiseerZaakStatusTranslator;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01ActualiseerZaakstatus;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class ActualiseerZaakStatusReplicator extends ActualiseerZaakStatusTranslator {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public ActualiseerZaakStatusReplicator(RequestResponseCycle session, Translation translation, ZaakService zaakService) {
		super(session, translation, zaakService);
	}

    /**
     * Replicates zaak before updating zaakstatus
     *
     * @return
     * @throws ResponseStatusException
     */
	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		var zdsZakLk01ActualiseerZaakstatus = (ZdsZakLk01ActualiseerZaakstatus) this.getZdsDocument();

		var replicator = new Replicator(this);
		var legacyresponse = replicator.proxy();
		if (legacyresponse.getStatusCode() != HttpStatus.OK) {
			log.warn("Service:" + this.getTranslation().getLegacyservice() + " SoapAction: "
					+ this.getSession().getClientSoapAction());
			return legacyresponse;
		}
		replicator.replicateZaak(zdsZakLk01ActualiseerZaakstatus.objects.get(0).identificatie);
		return super.execute();
	}
}
