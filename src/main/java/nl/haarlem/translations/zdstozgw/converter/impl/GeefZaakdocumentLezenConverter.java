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
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;

public class GeefZaakdocumentLezenConverter extends Converter {
	@Data
	private class UpdateZaak_Bv03 {
		final XpathDocument xpathDocument;
		Document document;

		public UpdateZaak_Bv03(String template) {
			this.document = nl.haarlem.translations.zdstozgw.utils.XmlUtils.getDocument(template);
			this.xpathDocument = new XpathDocument(this.document);
		}
	}	

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	public GeefZaakdocumentLezenConverter(String templatePath, String legacyService) {
		super(templatePath, legacyService);
	}

	@Override
	public String passThroughAndConvert(String soapAction, RequestResponseCycle session, ZGWClient zgwClient, ConfigService config, ApplicationParameterRepository repository, String body) {
		throw new ConverterException(this, "passThroughAndConvert not implemented in version", body, null);
	}

	@Override
	public String convertAndPassThrough(String soapAction, RequestResponseCycle session, ZGWClient zgwClient, ConfigService config, ApplicationParameterRepository repository, String body) {
		throw new ConverterException(this, "passThroughAndConvert not implemented in version", body, null);
	}

	@Override
	public String convert(RequestResponseCycle session, ZGWClient zgwClient, ConfigService configService, ApplicationParameterRepository repository, String requestBody) {
		try {
			
			EdcLv01 edcLv01 = (EdcLv01) XmlUtils.getStUFObject(requestBody, EdcLv01.class);					
			var translator = new ZaakTranslator(zgwClient, configService);			
			EdcLa01 edcLa01 = translator.getZaakDoumentLezen(edcLv01);
			
			var bv03 = new UpdateZaak_Bv03(this.template);
			bv03.xpathDocument.setNodeValue(".//stuf:zender//stuf:organisatie", edcLa01.stuurgegevens.ontvanger.organisatie);
			bv03.xpathDocument.setNodeValue(".//stuf:zender//stuf:applicatie", edcLa01.stuurgegevens.ontvanger.applicatie);
			bv03.xpathDocument.setNodeValue(".//stuf:zender//stuf:gebruiker", edcLa01.stuurgegevens.ontvanger.gebruiker);
			bv03.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:organisatie", edcLa01.stuurgegevens.zender.organisatie);
			bv03.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:applicatie", edcLa01.stuurgegevens.zender.applicatie);
			bv03.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:gebruiker", edcLa01.stuurgegevens.zender.gebruiker);
			bv03.xpathDocument.setNodeValue(".//stuf:referentienummer", edcLa01.stuurgegevens.referentienummer);
			bv03.xpathDocument.setNodeValue(".//stuf:crossRefnummer", edcLa01.stuurgegevens.referentienummer);
			DateFormat tijdstipformat = new SimpleDateFormat("yyyyMMddHHmmss");
			bv03.xpathDocument.setNodeValue(".//stuf:tijdstipBericht", tijdstipformat.format(new Date()));
			return XmlUtils.xmlToString(bv03.document);
		} catch (ZGWClient.ZGWClientException hsce) {
			throw new ConverterException(this, hsce.getMessage(), hsce.getDetails(), hsce);
		}
	}
}