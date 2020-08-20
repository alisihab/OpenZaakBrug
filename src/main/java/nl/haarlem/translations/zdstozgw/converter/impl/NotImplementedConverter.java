package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class NotImplementedConverter extends Converter {

	public NotImplementedConverter(Translation translation, ZaakService zaakService) {
		super(translation, zaakService);
	}

	@Override
	public String convert(String soapAction, String request) throws ConverterException {
		throw new ConverterException("soapaction: '" + soapAction  + "' not implemented");
	}

}
