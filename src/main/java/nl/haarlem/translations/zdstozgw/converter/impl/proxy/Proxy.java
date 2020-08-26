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
import nl.haarlem.translations.zdstozgw.converter.impl.NotImplementedConverter;
import nl.haarlem.translations.zdstozgw.jpa.EmulateParameterRepository;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.requesthandler.impl.logging.RequestResponseCycleService;
import nl.haarlem.translations.zdstozgw.translation.zds.client.ZDSClient;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZknDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGenereerDocumentIdentificatieDi02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGenereerDocumentIdentificatieDu02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGenereerZaakIdentificatieDi02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGenereerZaakIdentificatieDu02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class Proxy extends Converter { 
    
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
	public Proxy(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
		super(context, translation, zaakService);
	}	

	@Override
	public void load() throws ResponseStatusException {
		// nothing to do here, we dont set the zdsDocument
		this.zdsDocument = null;
	}

	@Override
	public ResponseEntity<?> execute() throws ConverterException {
		var zdsClient= new ZDSClient();
		var url = this.getContext().getUrl();
		var soapaction = this.getContext().getSoapAction();
		var requestbody = getContext().getRequestBody();
		log.info("relaying request to url: " + url + " with soapaction: " + soapaction);
		return zdsClient.post(url, soapaction, requestbody);		
	}   
}