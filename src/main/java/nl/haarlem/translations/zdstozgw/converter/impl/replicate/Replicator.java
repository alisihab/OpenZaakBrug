package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsParameters;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsScope;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsStuurgegevens;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakIdentificatie;
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

	public void replicateZaak(String zaakidentificatie) {
		log.info("replicateZaak for zaakidentificatie:" + zaakidentificatie);
		
		var zgwZaak = this.zaakservice.zgwClient.getZaakByIdentificatie(zaakidentificatie);
		if(zgwZaak != null)  {
			log.info("replication: no need to copy, zaak with id #" + zaakidentificatie + " already in zgw");
			// nothing to do here
			return;
		}
		var zdsUrl = this.zaakservice.configService.getConfiguratie().getGeefZaakdetails().getUrl();
		var zdsSoapAction = this.zaakservice.configService.getConfiguratie().getGeefZaakdetails().getSoapaction();
		var zdsRequest = new ZdsZakLv01();
		zdsRequest.stuurgegevens = stuurgegevens;
		zdsRequest.parameters = new ZdsParameters();		
		zdsRequest.parameters.setSortering("0");
		zdsRequest.parameters.setIndicatorVervolgvraag("false");
		zdsRequest.gelijk = new ZdsZaak();
		zdsRequest.sope = new ZdsScope();
		var zdsResponse = this.zaakservice.zdsClient.post(zdsUrl, zdsSoapAction, zdsRequest);

		// get the zaak details
		log.info("response:" + zdsResponse);
		ZdsZakLa01GeefZaakDetails zakLa01 = (ZdsZakLa01GeefZaakDetails) XmlUtils.getStUFObject(zdsResponse.getBody().toString(), ZdsZakLa01GeefZaakDetails.class);
		//ZdsZaak zdsZaak = zakLa01.antwoord;
//	
//		// use the info to create the zaak with this info
//		creeerZaak(stuurgegevens, zdsZaak);
//		
//		// TODO: ook de documenten copieren!
//		log.error("Also copy the files for zaakid:" + zaakidentificatie);
//		
		throw new ConverterException("not yet implemented!");
	}

	private void replicateDocuments(String zaakidentificatie, String documentidentificatie) {		
	}
}
