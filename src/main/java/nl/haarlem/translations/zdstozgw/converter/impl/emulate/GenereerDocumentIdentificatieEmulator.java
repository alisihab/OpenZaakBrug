package nl.haarlem.translations.zdstozgw.converter.impl.emulate;

import org.springframework.http.HttpStatus;
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
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGenereerDocumentIdentificatieDi02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGenereerDocumentIdentificatieDu02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGenereerZaakIdentificatieDi02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGenereerZaakIdentificatieDu02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GenereerDocumentIdentificatieEmulator extends Converter { 
	
	public GenereerDocumentIdentificatieEmulator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
		super(context, translation, zaakService);
	}	

	@Override
	public void load() throws ResponseStatusException {
        this.zdsDocument = (ZdsGenereerDocumentIdentificatieDi02) XmlUtils.getStUFObject(getContext().getRequestBody(), ZdsGenereerDocumentIdentificatieDi02.class);
	}

	@Override
	public ResponseEntity<?> execute() throws ConverterException {
    	EmulateParameterRepository repository = SpringContext.getBean(EmulateParameterRepository.class); 	
		var prefixparam = repository.getOne("DocumentIdentificatiePrefix");
		var idparam = repository.getOne("DocumentIdentificatieHuidige");
		var identificatie = Long.parseLong(idparam.getParameterValue()) + 1;
		idparam.setParameterValue(Long.toString(identificatie));
		repository.save(idparam);
		
		var di02 = (ZdsGenereerDocumentIdentificatieDi02) this.zdsDocument;
		var du02 = new ZdsGenereerDocumentIdentificatieDu02(di02.stuurgegevens);
      	du02.document = new ZdsZaakDocument();
      	du02.document.identificatie = prefixparam.getParameterValue() + identificatie;        

      	var response = XmlUtils.getSOAPMessageFromObject(du02);        
        return new ResponseEntity<>(response, HttpStatus.OK);
	}   
}