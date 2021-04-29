package nl.haarlem.translations.zdstozgw.converter.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class NotImplementedConverter extends Converter {

	public NotImplementedConverter(RequestResponseCycle context, Translation translation, ZaakService zaakService) {
		super(context, translation, zaakService);
	}

	@Override
	public void load() throws ResponseStatusException {
		throw new ConverterException("not implemented!");
	}

	@Override
	public ResponseEntity<?> execute() throws ConverterException {
		throw new ConverterException("not implemented!");
	}
}
