package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import java.lang.invoke.MethodHandles;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.client.ZDSClient;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;

@Service
public class Replicator {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private ZaakService zaakService;
    
    @Autowired
    private Replicator() {
    }
    
	public Replicator(ZaakService zaakService) {
		this.zaakService = zaakService;
	}

	public void replicateZaak(String zaakidentificatie) {
		log.info("replicateZaak for zaakidentificatie:" + zaakidentificatie);
		
		var zgwZaak = this.zaakService.zgwClient.getZaakByIdentificatie(zaakidentificatie);
		if(zgwZaak != null)  {
			log.info("replication: no need to copy, zaak with id #" + zaakidentificatie + " already in zgw");
			// nothing to do here
			return;
		}
		throw new ConverterException("not yet implemented!");
//		var zdsClient= new ZDSClient();		
//		String soapAction = "http://www.egem.nl/StUF/sector/zkn/0310/geefZaakdetails_Lv01";
//		String zdsUrl = null;
//		for(Translation translation : config.getConfiguratie().getTranslations())
//		{
//			if(soapAction.equals(translation.soapaction)) {
//				zdsUrl = translation.legacyservice;
//				break;
//			}
//		}
//		if(zdsUrl == null) {
//			throw new ZaakTranslatorException("geen legacy url gevonden voor soapaction:" + soapAction);			
//		}
//
//		// get the zaak details
//		var document = nl.haarlem.translations.zdstozgw.utils.XmlUtils.getDocument("src/main/java/nl/haarlem/translations/zdstozgw/converter/impl/GeefZaakdetails_Lv01.xml");
//		var xpathDocument = new XpathDocument(document);
//		xpathDocument.setNodeValue(".//stuf:zender//stuf:organisatie", stuurgegevens.ontvanger.organisatie);
//		xpathDocument.setNodeValue(".//stuf:zender//stuf:applicatie", stuurgegevens.ontvanger.applicatie);
//		xpathDocument.setNodeValue(".//stuf:zender//stuf:gebruiker", stuurgegevens.ontvanger.gebruiker);
//		xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:organisatie", stuurgegevens.zender.organisatie);
//		xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:applicatie", stuurgegevens.zender.applicatie);
//		xpathDocument.setNodeValue(".//stuf:ontvanger//stuf:gebruiker", stuurgegevens.zender.gebruiker);
//		xpathDocument.setNodeValue(".//stuf:referentienummer", stuurgegevens.crossRefnummer);
//		xpathDocument.setNodeValue(".//stuf:crossRefnummer", stuurgegevens.referentienummer);
//		DateFormat tijdstipformat = new SimpleDateFormat("yyyyMMddHHmmss");
//		xpathDocument.setNodeValue(".//stuf:tijdstipBericht", tijdstipformat.format(new Date()));
//		
//		xpathDocument.setNodeValue(".//zkn:gelijk//zkn:identificatie", zaakidentificatie);
//		
//		String zdsRequest = XmlUtils.xmlToString(document);
//		log.info("post action: " + soapAction + " with body:" + zdsRequest);
//		String zdsResponse = zdsClient.post(session, zdsUrl, soapAction, zdsRequest);
//		log.info("response:" + zdsResponse);
//		ZakZakLa01 zakLa01 = (ZakZakLa01) XmlUtils.getStUFObject(zdsResponse, ZakZakLa01.class);
//		ZdsZaak zdsZaak = zakLa01.antwoord.object;
//	
//		// use the info to create the zaak with this info
//		creeerZaak(stuurgegevens, zdsZaak);
//		
//		// TODO: ook de documenten copieren!
//		log.error("Also copy the files for zaakid:" + zaakidentificatie);
//		
	}

	private void replicateDocuments(String zaakidentificatie, String documentidentificatie) {		
	}
}
