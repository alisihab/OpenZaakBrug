package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsParameters;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsScope;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsStuurgegevens;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLa01GeefZaakDetails;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class Replicator {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private ZaakService zaakservice;
    private ZdsStuurgegevens stuurgegevens;
        
    @Autowired
    private Replicator() {
    }
    
	public Replicator(ZaakService zaakservice, ZdsStuurgegevens stuurgegevens) {
		this.zaakservice = zaakservice;
		this.stuurgegevens = stuurgegevens;
	}

	public void replicateZaak(String rsin, String zaakidentificatie) {
		log.info("replicateZaak for zaakidentificatie:" + zaakidentificatie);
		
		var zgwZaak = this.zaakservice.zgwClient.getZaakByIdentificatie(zaakidentificatie);

		if(zgwZaak == null) {
			// bestond nog niet, aanmaken
			var zdsUrl = this.zaakservice.configService.getConfiguratie().getReplication().getGeefZaakdetails().getUrl();
			var zdsSoapAction = this.zaakservice.configService.getConfiguratie().getReplication().getGeefZaakdetails().getSoapaction();
			var zdsRequest = new ZdsZakLv01();
			zdsRequest.stuurgegevens = stuurgegevens;
			zdsRequest.parameters = new ZdsParameters();		
			//zdsRequest.parameters.setSortering("0");
			zdsRequest.parameters.setIndicatorVervolgvraag("false");
			zdsRequest.gelijk = new ZdsZaak();
			zdsRequest.scope = new ZdsScope();
			var zdsResponse = this.zaakservice.zdsClient.post(zdsUrl, zdsSoapAction, zdsRequest);
	
			// fetch the zaak details 
			log.info("GeefZaakDetails response:" + zdsResponse);
			ZdsZakLa01GeefZaakDetails zakLa01 = (ZdsZakLa01GeefZaakDetails) XmlUtils.getStUFObject(zdsResponse.getBody().toString(), ZdsZakLa01GeefZaakDetails.class);
			var zdsZaak = zakLa01.antwoord.zaak.get(0);
	
			log.info("received data from zds-zaaksysteem, now storing in zgw-zaaksysteem");	
			this.zaakservice.creeerZaak(rsin, zdsZaak);
		}
		else {
			log.info("replication: no need to copy, zaak with id #" + zaakidentificatie + " already in zgw");
		}
/////////////////////////

		// documenten moeten we altijd controleren of ze bestaan		
		var zdsUrl = this.zaakservice.configService.getConfiguratie().getReplication().getGeefLijstZaakdocumenten().getUrl();
		var zdsSoapAction = this.zaakservice.configService.getConfiguratie().getReplication().getGeefLijstZaakdocumenten().getSoapaction();
		var zdsRequest = new ZdsZakLv01();
		zdsRequest.stuurgegevens = stuurgegevens;
		zdsRequest.parameters = new ZdsParameters();		
		//zdsRequest.parameters.setSortering("0");
		zdsRequest.parameters.setIndicatorVervolgvraag("false");
		zdsRequest.gelijk = new ZdsZaak();
		zdsRequest.scope = new ZdsScope();
		var zdsResponse = this.zaakservice.zdsClient.post(zdsUrl, zdsSoapAction, zdsRequest);

		// fetch the zaak details 
		log.info("GeefLijstZaakdocumenten response:" + zdsResponse);
		var zakLa01 = (ZdsZakLa01GeefZaakDetails) XmlUtils.getStUFObject(zdsResponse.getBody().toString(), ZdsZakLa01GeefZaakDetails.class);
		var zdsZaak = zakLa01.antwoord.zaak;
	}

	private void replicateDocuments(String zaakidentificatie, String documentidentificatie) {		
	}
}
