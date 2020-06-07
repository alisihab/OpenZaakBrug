package nl.haarlem.translations.zdstozgw.converter.impl;

import org.w3c.dom.Document;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.Converter.ConverterException;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.jpa.model.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.translation.ZaakTranslator;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLa01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Ontvanger;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Stuurgegevens;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Zender;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;

public class GeefZaakDetailsConverter extends Converter {
	@Data
	private class GeefZaakDetails_Bv03 {
		final XpathDocument xpathDocument;
		Document document;

		public GeefZaakDetails_Bv03(String template) {
			this.document = nl.haarlem.translations.zdstozgw.utils.XmlUtils.getDocument(template);
			this.xpathDocument = new XpathDocument(this.document);
		}
	}	
	
	public GeefZaakDetailsConverter(String templatePath, String legacyService) {
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
		String result = "";
		try {
			
			ZakLv01 zakLv01 = (ZakLv01) XmlUtils.getStUFObject(requestBody, ZakLv01.class);
			var translator = new ZaakTranslator(zgwClient, configService);
			var zakLa01 = translator.getZaakDetails(zakLv01);

			zakLa01.stuurgegevens = new Stuurgegevens();
			zakLa01.stuurgegevens.zender = new Zender();
			zakLa01.stuurgegevens.zender.applicatie = zakLv01.stuurgegevens.ontvanger.applicatie;
			zakLa01.stuurgegevens.zender.organisatie = zakLv01.stuurgegevens.ontvanger.organisatie;
			zakLa01.stuurgegevens.zender.gebruiker = zakLv01.stuurgegevens.ontvanger.organisatie;
			zakLa01.stuurgegevens.berichtcode = "La01";

			return XmlUtils.getSOAPMessageFromObject(zakLa01);

		} catch (Exception ex) {
			ex.printStackTrace();
			var f03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.F03();
			f03.setFaultString("Object was not saved");
			f03.setCode("StUF046");
			f03.setOmschrijving("Object niet opgeslagen");
			f03.setDetails(ex.getMessage());
			return f03.getSoapMessageAsString();
		}
	}
}

