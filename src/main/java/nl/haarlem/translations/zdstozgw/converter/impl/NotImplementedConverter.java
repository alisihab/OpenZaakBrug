package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;

public class NotImplementedConverter extends Converter {

	public NotImplementedConverter(String template, String legacyService) {
		super(template, legacyService);
	}

	@Override
	public String Convert(ZGWClient zgwClient, ConfigService configService, ApplicationParameterRepository repository, String requestbody) {
		throw new RuntimeException("Not implemented");
	}
}