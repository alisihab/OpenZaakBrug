package nl.haarlem.translations.zdstozgw.converter.impl;

import java.lang.invoke.MethodHandles;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.Converter.ConverterException;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.jpa.model.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.translation.ZaakTranslator;
import nl.haarlem.translations.zdstozgw.translation.ZaakTranslator.ZaakTranslatorException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLa01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;

public class GeefZaakdocumentLezenConverter extends Converter {
	@Data
	private class GeefZaakdocumentLezen_Lv01 {
		final XpathDocument xpathDocument;
		Document document;

		public GeefZaakdocumentLezen_Lv01(String template) {
			this.document = nl.haarlem.translations.zdstozgw.utils.XmlUtils.getDocument(template);
			this.xpathDocument = new XpathDocument(this.document);
		}
	}	

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	public GeefZaakdocumentLezenConverter(String templatePath, String legacyService) {
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
		
			EdcLv01 edcLv01 = (EdcLv01) XmlUtils.getStUFObject(requestBody, EdcLv01.class);					
			var translator = new ZaakTranslator(zgwClient, configService);			
			EdcLa01 edcLa01 = translator.getZaakDoumentLezen(edcLv01);
			
			var bv03 = new GeefZaakdocumentLezen_Lv01(this.template);
			bv03.xpathDocument.setNodeValue(".//stuf:zender//stuf:organisatie", edcLv01.stuurgegevens.ontvanger.organisatie);
			bv03.xpathDocument.setNodeValue(".//stuf:zender//stuf:applicatie", edcLv01.stuurgegevens.ontvanger.applicatie);
			//bv03.xpathDocument.setNodeValue(".//stuf:zender//stuf:gebruiker", edcLv01.stuurgegevens.ontvanger.gebruiker);
			bv03.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:organisatie", edcLv01.stuurgegevens.zender.organisatie);
			bv03.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:applicatie", edcLv01.stuurgegevens.zender.applicatie);
			bv03.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:gebruiker", edcLv01.stuurgegevens.zender.gebruiker);
			bv03.xpathDocument.setNodeValue(".//stuf:referentienummer", edcLv01.stuurgegevens.referentienummer);
			bv03.xpathDocument.setNodeValue(".//stuf:crossRefnummer", edcLv01.stuurgegevens.referentienummer);
			DateFormat tijdstipformat = new SimpleDateFormat("yyyyMMddHHmmss");
			bv03.xpathDocument.setNodeValue(".//stuf:tijdstipBericht", tijdstipformat.format(new Date()));
			
			// beetje dubbel
			bv03.xpathDocument.setNodeValue(".//zkn:antwoord//zkn:object//zkn:auteur", edcLa01.antwoord.object.auteur);
			bv03.xpathDocument.setNodeValue(".//zkn:antwoord//zkn:object//zkn:creatiedatum", edcLa01.antwoord.object.creatiedatum.replace("-", ""));
			
			//bv03.xpathDocument.setNodeValue(".//zkn:antwoord//zkn:object//beschrijving", edcLa01.antwoord.object.dctCategorie);
			bv03.xpathDocument.setNodeValue(".//zkn:antwoord//zkn:object//zkn:beschrijving", edcLa01.antwoord.object.dctOmschrijving);
			bv03.xpathDocument.setNodeValue(".//zkn:antwoord//zkn:object//zkn:identificatie", edcLa01.antwoord.object.identificatie);
			bv03.xpathDocument.setNodeValue(".//zkn:antwoord//zkn:object//zkn:inhoud", edcLa01.antwoord.object.inhoud);
			//bv03.xpathDocument.setNodeValue(".//zkn:antwoord//zkn:object//zkn:link", edcLa01.antwoord.object.link);

			/*
			if(edcLa01.antwoord.object.ontvangstdatum == null || edcLa01.antwoord.object.ontvangstdatum == "") {
				log.info("hier ontvangstdatum:" + edcLa01.antwoord.object.ontvangstdatum);
				bv03.xpathDocument.setNodeValue(".//zkn:antwoord//zkn:object//zkn:ontvangstdatum", "00010101");				
			}
			else {
				log.info("daart ontvangstdatum:" + edcLa01.antwoord.object.ontvangstdatum);
				bv03.xpathDocument.setNodeValue(".//zkn:antwoord//zkn:object//zkn:ontvangstdatum", edcLa01.antwoord.object.ontvangstdatum.replace("-", ""));				
			}
			*/
			
			// bv03.xpathDocument.setNodeValue(".//zkn:antwoord//zkn:object//zkn:status", edcLa01.antwoord.object.status);
			bv03.xpathDocument.setNodeValue(".//zkn:antwoord//zkn:object//zkn:taal", edcLa01.antwoord.object.taal);
			bv03.xpathDocument.setNodeValue(".//zkn:antwoord//zkn:object//zkn:titel", edcLa01.antwoord.object.titel);
			bv03.xpathDocument.setNodeValue(".//zkn:antwoord//zkn:object//zkn:versie", edcLa01.antwoord.object.versie);

			return XmlUtils.xmlToString(bv03.document);
		} catch (ZGWClient.ZGWClientException hsce) {
			throw new ConverterException(this, hsce.getMessage(), hsce.getDetails(), hsce);
		} catch (ZaakTranslator.ZaakTranslatorException zte) {
			throw new ConverterException(this, zte.getMessage(), requestBody, zte);
		}		
	}
}