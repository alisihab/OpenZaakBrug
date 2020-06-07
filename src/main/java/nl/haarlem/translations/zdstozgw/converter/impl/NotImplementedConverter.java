package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.Converter.ConverterException;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.jpa.model.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;

public class NotImplementedConverter extends Converter {

	public NotImplementedConverter(String template, String legacyService) {
		super(template, legacyService);
	}

	@Override
	public String proxyZds(String soapAction, RequestResponseCycle session, ApplicationParameterRepository repository, String requestBody)  {
		throw new ConverterException(this, "proxyZds not implemented in version", requestBody, null);
	}		
	
	@Override
	public String proxyZdsAndReplicateToZgw(String soapAction, RequestResponseCycle session, ZGWClient zgwClient, ConfigService config, ApplicationParameterRepository repository, String requestBody) {
		throw new ConverterException(this, "proxyZdsAndReplicateToZgw not implemented in version", requestBody, null);
	}

	@Override
	public String convertToZgwAndReplicateToZds(String soapAction, RequestResponseCycle session, ZGWClient zgwClient, ConfigService config, ApplicationParameterRepository repository, String requestBody) {
		throw new ConverterException(this, "convertToZgwAndReplicateToZds not implemented in version", requestBody, null);
	}

	@Override
	public String convertToZgw(RequestResponseCycle session, ZGWClient zgwClient, ConfigService configService, ApplicationParameterRepository repository, String requestBody) {
		throw new ConverterException(this, "convertToZgw not implemented in version", requestBody, null);
	}
}