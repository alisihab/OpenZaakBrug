package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.converter.impl.replicate.model.ZdsReplicateGeefLijstZaakdocumentenLv01;
import nl.haarlem.translations.zdstozgw.converter.impl.replicate.model.ZdsReplicateGeefZaakdetailsLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsHeeftRelevant;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsParameters;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsScope;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsStuurgegevens;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakDocumentInhoud;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLa01GeefZaakDetails;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLa01LijstZaakdocumenten;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class Replicator {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private Converter converter;
        
    @Autowired
    private Replicator() {
    }
    

	public Replicator(Converter converter) {
		this.converter = converter;
	}

	public void replicateZaak(String zaakidentificatie) {
		log.info("replicateZaak for zaakidentificatie:" + zaakidentificatie);				
		String rsin = this.converter.getZaakService().getRSIN(this.converter.getZdsDocument().stuurgegevens.zender.organisatie);
		
		// altijd de controle of de zaak al bestaat
		var zgwZaak = this.converter.getZaakService().zgwClient.getZaakByIdentificatie(zaakidentificatie);		
		if(zgwZaak == null) {
			// bestond nog niet, aanmaken
			var zdsUrl = this.converter.getZaakService().configService.getConfiguratie().getReplication().getGeefZaakdetails().getUrl();
			var zdsSoapAction = this.converter.getZaakService().configService.getConfiguratie().getReplication().getGeefZaakdetails().getSoapaction();
			var zdsRequest = new ZdsReplicateGeefZaakdetailsLv01();
			zdsRequest.stuurgegevens = this.converter.getZdsDocument().stuurgegevens;
			zdsRequest.parameters = new ZdsParameters();		
			//zdsRequest.parameters.setSortering("0");
			zdsRequest.parameters.setIndicatorVervolgvraag("false");
			zdsRequest.gelijk = new ZdsZaak();
			zdsRequest.gelijk.identificatie = zaakidentificatie;
			zdsRequest.scope = new ZdsScope();
			zdsRequest.scope.scope = "alles";
			var zdsResponse = this.converter.getZaakService().zdsClient.post(zdsUrl, zdsSoapAction, zdsRequest);
	
			// fetch the zaak details 
			log.debug("GeefZaakDetails response:" + zdsResponse);
			ZdsZakLa01GeefZaakDetails zakLa01 = (ZdsZakLa01GeefZaakDetails) XmlUtils.getStUFObject(zdsResponse.getBody().toString(), ZdsZakLa01GeefZaakDetails.class);
			var zdsZaak = zakLa01.antwoord.zaak.get(0);
	
			log.info("received data from zds-zaaksysteem, now storing in zgw-zaaksysteem");	
			this.converter.getZaakService().creeerZaak(rsin, zdsZaak);
		}
		else {
			log.info("replication: no need to copy, zaak with id #" + zaakidentificatie + " already in zgw");
		}
		
		// altijd de controle of de documenten al bestaan
		{
			var zdsUrl = this.converter.getZaakService().configService.getConfiguratie().getReplication().getGeefLijstZaakdocumenten().getUrl();
			var zdsSoapAction = this.converter.getZaakService().configService.getConfiguratie().getReplication().getGeefLijstZaakdocumenten().getSoapaction();
			var zdsRequest = new ZdsReplicateGeefLijstZaakdocumentenLv01();
			zdsRequest.stuurgegevens = this.converter.getZdsDocument().stuurgegevens;
			zdsRequest.parameters = new ZdsParameters();		
			//zdsRequest.parameters.setSortering("0");
			zdsRequest.parameters.setIndicatorVervolgvraag("false");
			zdsRequest.gelijk = new ZdsZaak();
			zdsRequest.gelijk.identificatie = zaakidentificatie;
			zdsRequest.scope = new ZdsScope();
			zdsRequest.scope.object = new ZdsZaak();
			zdsRequest.scope.object.heeftRelevant = new ZdsHeeftRelevant();
			zdsRequest.scope.object.heeftRelevant.gerelateerde = new ZdsZaakDocument();
						
			// fetch the document list
			var zdsResponse = this.converter.getZaakService().zdsClient.post(zdsUrl, zdsSoapAction, zdsRequest);			
			log.info("GeefLijstZaakdocumenten voor zaak:" + zaakidentificatie);
			var zakZakLa01 = (ZdsZakLa01LijstZaakdocumenten) XmlUtils.getStUFObject(zdsResponse.getBody().toString(), ZdsZakLa01LijstZaakdocumenten.class);
			
			for(ZdsHeeftRelevant relevant : zakZakLa01.antwoord.object.heeftRelevant) {
				var zaakdocumentidentificatie = relevant.gerelateerde.identificatie;
				log.info("GeefZaakdocumentenLezen voor zaak:" + zaakidentificatie + " met document:" + zaakdocumentidentificatie);
/*				
				zdsUrl = this.converter.getZaakService().configService.getConfiguratie().getReplication().getGeefLijstZaakdocumenten().getUrl();
				zdsSoapAction = this.converter.getZaakService().configService.getConfiguratie().getReplication().getGeefLijstZaakdocumenten().getSoapaction();
				zdsRequest = new ZdsReplicateGeefLijstZaakdocumentenLv01();
				zdsRequest.stuurgegevens = this.converter.getZdsDocument().stuurgegevens;
				zdsRequest.parameters = new ZdsParameters();		
				//zdsRequest.parameters.setSortering("0");
				zdsRequest.parameters.setIndicatorVervolgvraag("false");
				zdsRequest.gelijk = new ZdsZaak();
				zdsRequest.gelijk.identificatie = zaakidentificatie;
				zdsRequest.scope = new ZdsScope();
				zdsRequest.scope.object = new ZdsZaak();
				zdsRequest.scope.object.heeftRelevant = new ZdsHeeftRelevant();
				zdsRequest.scope.object.heeftRelevant.gerelateerde = new ZdsZaakDocument();
				
				var zdsResponse = this.converter.getZaakService().zdsClient.post(zdsUrl, zdsSoapAction, zdsRequest);		
				// fetch the zaak details 
				log.info("GeefLijstZaakdocumenten response:" + zdsResponse);
				var zakZakLa01 = (ZdsZakLa01LijstZaakdocumenten) XmlUtils.getStUFObject(zdsResponse.getBody().toString(), ZdsZakLa01LijstZaakdocumenten.class);
*/
			}
		}
	}

	public ResponseEntity<?>  proxy() {
		var url = this.converter.getTranslation().getLegacyservice();
		var soapaction = this.converter.getTranslation().getSoapAction();
		var request = this.converter.getContext().getRequestBody();
		log.info("relaying request to url: " + url + " with soapaction: " + soapaction + " request-size:" + request.length());
		return this.converter.getZaakService().zdsClient.post(url, soapaction, request);
	}
}
