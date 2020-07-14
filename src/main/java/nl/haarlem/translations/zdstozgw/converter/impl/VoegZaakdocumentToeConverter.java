package nl.haarlem.translations.zdstozgw.converter.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Document;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.Converter.ConverterException;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.jpa.model.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.translation.ZaakTranslator;
import nl.haarlem.translations.zdstozgw.translation.zds.client.ZDSClient;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakInformatieObject;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;

public class VoegZaakdocumentToeConverter extends Converter {
	@Data
	private class VoegZaakdocumentToeConverter_Bv03 {
		final XpathDocument xpathDocument;
		Document document;

		public VoegZaakdocumentToeConverter_Bv03(String template) {
			this.document = nl.haarlem.translations.zdstozgw.utils.XmlUtils.getDocument(template);
			this.xpathDocument = new XpathDocument(this.document);
		}
	}

	public VoegZaakdocumentToeConverter(String templatePath, String legacyService) {
		super(templatePath, legacyService);
	}

	@Override
	public String proxyZds(String soapAction, RequestResponseCycle session, ApplicationParameterRepository repository, String requestBody) {
		var zdsClient= new ZDSClient();
		String zdsResponse = zdsClient.post(session, zdsUrl, soapAction, requestBody);
		return zdsResponse;
	}

	@Override
	public String proxyZdsAndReplicateToZgw(String soapAction, RequestResponseCycle session, ZGWClient zgwClient, ConfigService config, ApplicationParameterRepository repository, String requestBody) {
		try {
			EdcLk01 edcLk01 = (EdcLk01) XmlUtils.getStUFObject(requestBody, EdcLk01.class);
			var translator = new ZaakTranslator(zgwClient, config);			
			translator.replicateZds2ZgwZaak(session, config,  edcLk01.stuurgegevens,  edcLk01.objects.get(0).isRelevantVoor.gerelateerde.identificatie);

			// to the legacy zaaksystem
			var zdsClient= new ZDSClient();
			String zdsResponse = zdsClient.post(session, zdsUrl, soapAction, requestBody);

			// also to openzaak
			String zgwResonse = convertToZgw(session, zgwClient, config, repository, requestBody);

			// the original response
			return zdsResponse;
		} catch (ZGWClient.ZGWClientException hsce) {
			throw new ConverterException(this, hsce.getMessage(), hsce.getDetails(), hsce);
		} catch (ZaakTranslator.ZaakTranslatorException zte) {
			throw new ConverterException(this, zte.getMessage(), requestBody, zte);
		}
	}

	@Override
	public String convertToZgwAndReplicateToZds(String soapAction, RequestResponseCycle session, ZGWClient zgwClient, ConfigService config, ApplicationParameterRepository repository, String requestBody) {
		try {
			EdcLk01 edcLk01 = (EdcLk01) XmlUtils.getStUFObject(requestBody, EdcLk01.class);
			var translator = new ZaakTranslator(zgwClient, config);			
			translator.replicateZds2ZgwZaak(session, config,  edcLk01.stuurgegevens,  edcLk01.objects.get(0).isRelevantVoor.gerelateerde.identificatie);

			// to openzaak
			String zgwResonse = convertToZgw(session, zgwClient, config, repository, requestBody);

			// also to the legacy zaaksystem
			var zdsClient= new ZDSClient();
			String zdsResponse = zdsClient.post(session, zdsUrl, soapAction, requestBody);

			// response
			return zgwResonse;
		} catch (ZGWClient.ZGWClientException hsce) {
			throw new ConverterException(this, hsce.getMessage(), hsce.getDetails(), hsce);
		} catch (ZaakTranslator.ZaakTranslatorException zte) {
			throw new ConverterException(this, zte.getMessage(), requestBody, zte);
		}		
	}

	@Override
	public String convertToZgw(RequestResponseCycle session, ZGWClient zgwClient, ConfigService configService, ApplicationParameterRepository repository, String requestBody) {
		try {
			EdcLk01 edcLk01 = (EdcLk01) XmlUtils.getStUFObject(requestBody, EdcLk01.class);
			var translator = new ZaakTranslator(zgwClient, configService);
			var zgwZaakInformatieObject = translator.voegZaakDocumentToe(edcLk01);
//			var bv03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.Bv03();
//			bv03.setReferentienummer(zgwZaakInformatieObject.getUuid());
//			return bv03.getSoapMessageAsString();
			var bv03 = new VoegZaakdocumentToeConverter_Bv03(this.template);
			bv03.xpathDocument.setNodeValue(".//stuf:zender//stuf:organisatie",
					edcLk01.stuurgegevens.ontvanger.organisatie);
			bv03.xpathDocument.setNodeValue(".//stuf:zender//stuf:applicatie",
					edcLk01.stuurgegevens.ontvanger.applicatie);
			bv03.xpathDocument.setNodeValue(".//stuf:zender//stuf:gebruiker",
					edcLk01.stuurgegevens.ontvanger.gebruiker);
			bv03.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:organisatie",
					edcLk01.stuurgegevens.zender.organisatie);
			bv03.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:applicatie",
					edcLk01.stuurgegevens.zender.applicatie);
			bv03.xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:gebruiker",
					edcLk01.stuurgegevens.zender.gebruiker);
			bv03.xpathDocument.setNodeValue(".//stuf:referentienummer", zgwZaakInformatieObject.uuid);
			bv03.xpathDocument.setNodeValue(".//stuf:crossRefnummer", edcLk01.stuurgegevens.referentienummer);
			DateFormat tijdstipformat = new SimpleDateFormat("yyyyMMddHHmmss");
			bv03.xpathDocument.setNodeValue(".//stuf:tijdstipBericht", tijdstipformat.format(new Date()));
			return XmlUtils.xmlToString(bv03.document);
		} catch (ZGWClient.ZGWClientException hsce) {
			throw new ConverterException(this, hsce.getMessage(), hsce.getDetails(), hsce);
		} catch (ZaakTranslator.ZaakTranslatorException zte) {
			throw new ConverterException(this, zte.getMessage(), requestBody, zte);
		}
		/*
		 * try {
		 * 
		 * EdcLk01 object = (EdcLk01) XmlUtils.getStUFObject(requestbody,
		 * EdcLk01.class); var translator = new ZaakTranslator(zgwClient,
		 * configService); ZgwZaakInformatieObject zgwZaakInformatieObject =
		 * translator.voegZaakDocumentToe((EdcLk01) object); var bv03 = new
		 * nl.haarlem.translations.zdstozgw.translation.zds.model.Bv03();
		 * bv03.setReferentienummer(zgwZaakInformatieObject.getUuid()); return
		 * bv03.getSoapMessageAsString();
		 * 
		 * } catch (Exception ex) { ex.printStackTrace(); var f03 = new
		 * nl.haarlem.translations.zdstozgw.translation.zds.model.F03();
		 * f03.setFaultString("Object was not saved"); f03.setCode("StUF046");
		 * f03.setOmschrijving("Object niet opgeslagen");
		 * f03.setDetails(ex.getMessage()); return f03.getSoapMessageAsString(); }
		 */
	}
}