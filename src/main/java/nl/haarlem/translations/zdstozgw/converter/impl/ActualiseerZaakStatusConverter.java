package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.Converter.ConverterException;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.jpa.model.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.translation.ZaakTranslator;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class ActualiseerZaakStatusConverter extends Converter {
	public ActualiseerZaakStatusConverter(String templatePath, String legacyService) {
		super(templatePath, legacyService);
	}
	
	@Override
	public String proxyZds(String soapAction, RequestResponseCycle session, ApplicationParameterRepository repository, String requestBody)  {
		return postZdsRequest(session, soapAction, requestBody);
	}
		
	@Override
	public String proxyZdsAndReplicateToZgw(String soapAction, RequestResponseCycle session, ZGWClient zgwClient, ConfigService config, ApplicationParameterRepository repository, String requestBody) {
		// to the legacy zaaksystem
		String zdsResponse = postZdsRequest(session, soapAction, requestBody);
		
		// also to openzaak
		String zgwResonse = convertToZgw(session, zgwClient, config, repository, requestBody);
				
		// the original response
		return zdsResponse;		
	}

	@Override
	public String convertToZgwAndReplicateToZds(String soapAction, RequestResponseCycle session, ZGWClient zgwClient, ConfigService config, ApplicationParameterRepository repository, String requestBody) {
		// to openzaak
		String zgwResonse = convertToZgw(session, zgwClient, config, repository, requestBody);
						
		// also to the legacy zaaksystem
		String zdsResponse = postZdsRequest(session, soapAction, requestBody);
		
		// response
		return zgwResonse;		
	}		
	
	@Override
	public String convertToZgw(RequestResponseCycle session, ZGWClient zgwClient, ConfigService configService, ApplicationParameterRepository repository, String requestBody) {
		try {
			ZakLk01 zakLk01 = (ZakLk01) XmlUtils.getStUFObject(requestBody, ZakLk01.class);
			var translator = new ZaakTranslator(zgwClient, configService);			
			var zaak = translator.actualiseerZaakstatus(zakLk01);
			var bv03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.Bv03();
			bv03.setReferentienummer(zaak.getUuid());
			return bv03.getSoapMessageAsString();
		} catch (ZGWClient.ZGWClientException hsce) {
			throw new ConverterException(this, hsce.getMessage(), hsce.getDetails(), hsce);
		} catch (ZaakTranslator.ZaakTranslatorException zte) {
			throw new ConverterException(this, zte.getMessage(), requestBody, zte);
		}			
/*
		} catch (Exception ex) {
			ex.printStackTrace();
			var f03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.F03();
			f03.setFaultString("Object was not saved");
			f03.setCode("StUF046");
			f03.setOmschrijving("Object niet opgeslagen");
			f03.setDetails(ex.getMessage());
			return f03.getSoapMessageAsString();
		}
*/		
	}
}
