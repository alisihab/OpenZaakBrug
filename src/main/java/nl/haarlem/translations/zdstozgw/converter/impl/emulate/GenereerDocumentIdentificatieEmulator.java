package nl.haarlem.translations.zdstozgw.converter.impl.emulate;

import nl.haarlem.translations.zdstozgw.config.SpringContext;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.converter.impl.NotImplementedConverter;
import nl.haarlem.translations.zdstozgw.jpa.EmulateParameterRepository;
import nl.haarlem.translations.zdstozgw.requesthandler.impl.logging.RequestResponseCycleService;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGenereerZaakIdentificatieDi02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GenereerDocumentIdentificatieEmulator extends NotImplementedConverter { 
	
	public GenereerDocumentIdentificatieEmulator(Translation translation, ZaakService zaakService) {
		super(translation, zaakService);
	}	

    @Override
    public String convert(String action, String request) throws ConverterException {
    	return null;
    }	
}