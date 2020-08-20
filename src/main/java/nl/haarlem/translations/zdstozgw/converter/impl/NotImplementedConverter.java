package nl.haarlem.translations.zdstozgw.converter.impl;

import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.SpringContext;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.jpa.EmulateParameterRepository;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGenereerDocumentIdentificatieDi02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGenereerDocumentIdentificatieDu02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class NotImplementedConverter extends Converter {

	public NotImplementedConverter(Translation translation, ZaakService zaakService) {
		super(translation, zaakService);
	}

	@Override
	public ZdsDocument load(String request) throws ResponseStatusException {
		throw new ConverterException("not implemented!");
	}

	@Override
	public ZdsDocument execute(ZdsDocument document) throws ConverterException {
		throw new ConverterException("not implemented!");
	}   	
}
