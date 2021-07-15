package nl.haarlem.translations.zdstozgw.converter.impl.proxy;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.SpringContext;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.translation.zds.client.ZDSClient;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class Proxy extends Converter {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public Proxy(RequestResponseCycle session, Translation translation, ZaakService zaakService) {
		super(session, translation, zaakService);
	}

	@Override
	public void load() throws ResponseStatusException {
		// nothing to do here, we dont set the zdsDocument
		this.zdsDocument = null;
	}

	@Override
	public ResponseEntity<?> execute() throws ConverterException {
		var url = this.getTranslation().getLegacyservice();
		var soapaction = this.getTranslation().getSoapAction();
		var request = this.getSession().getClientRequestBody();
		
		this.getSession().setFunctie("Proxy");
		this.getSession().setKenmerk(url);
		
		log.info("relaying request to url: " + url + " with soapaction: " + soapaction + " request-size:"
				+ request.length());

		ZDSClient zdsClient = SpringContext.getBean(ZDSClient.class);		
		return zdsClient.post(this.getSession().getReferentienummer(), url, soapaction, request);
	}
}
