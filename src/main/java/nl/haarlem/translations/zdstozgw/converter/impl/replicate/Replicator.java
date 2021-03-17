package nl.haarlem.translations.zdstozgw.converter.impl.replicate;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import nl.haarlem.translations.zdstozgw.config.SpringContext;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.impl.replicate.model.*;
import nl.haarlem.translations.zdstozgw.debug.Debugger;
import nl.haarlem.translations.zdstozgw.translation.zds.client.ZDSClient;
import nl.haarlem.translations.zdstozgw.translation.zds.model.*;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwEnkelvoudigInformatieObject;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

@Service
public class Replicator {
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private Converter converter;
    private final ZDSClient zdsClient;
    private static final Debugger debug = Debugger.getDebugger(MethodHandles.lookup().lookupClass());    

	@Autowired
	private Replicator(ZDSClient zdsClient) {
        this.zdsClient = zdsClient;
	}

	public Replicator(Converter converter) {
		this.converter = converter;
		this.zdsClient = SpringContext.getBean(ZDSClient.class);
	}

	public void replicateZaak(String zaakidentificatie) {
		debug.infopoint("replicatie", "replicateZaak for zaakidentificatie:" + zaakidentificatie);
		
		String rsin = this.converter.getZaakService().getRSIN(this.converter.getZdsDocument().stuurgegevens.zender.organisatie);

		var zgwZaak = this.converter.getZaakService().zgwClient.getZaakByIdentificatie(zaakidentificatie);
		if (zgwZaak == null) {
			debug.infopoint("replicatie", "zaak not found, copying zaak with identificatie #" + zaakidentificatie);
            checkCreeerZaak(zaakidentificatie, rsin);
        } else {
        	debug.infopoint("replicatie", "zaak already found, skipping zaak with identificatie #" + zaakidentificatie);
		}

        List<ZdsHeeftRelevant> relevanteDocumenten = getLijstZaakdocumenten(zaakidentificatie);
        checkVoegZaakDocumentToe(zaakidentificatie, rsin, relevanteDocumenten);
    }

    private void checkCreeerZaak(String zaakidentificatie, String rsin) {
        var zdsUrl = this.converter.getZaakService().configService.getConfiguration().getReplication().getGeefZaakdetails().getUrl();
        var zdsSoapAction = this.converter.getZaakService().configService.getConfiguration().getReplication().getGeefZaakdetails().getSoapaction();
        var zdsRequest = new ZdsReplicateGeefZaakdetailsLv01();
        zdsRequest.stuurgegevens = this.converter.getZdsDocument().stuurgegevens;
        zdsRequest.stuurgegevens.berichtcode = "Lv01";
        zdsRequest.parameters = new ZdsParametersMetSortering();
        zdsRequest.parameters.setSortering("0");
        zdsRequest.parameters.setIndicatorVervolgvraag("false");
        zdsRequest.gelijk = new ZdsZaak();
        zdsRequest.gelijk.identificatie = zaakidentificatie;
        zdsRequest.scope = new ZdsScope();
        zdsRequest.scope.object = new ZdsScopeObject();
        zdsRequest.scope.object.setEntiteittype("ZAK");
        zdsRequest.scope.object.setScope("alles");
        
        debug.infopoint("StUF:berichtcode", "StUF:berichtcode:" + zdsRequest.stuurgegevens.berichtcode);
        
        var zdsResponse = this.zdsClient.post(zdsUrl, zdsSoapAction, zdsRequest);

        // fetch the zaak details
        log.debug("GeefZaakDetails response:" + zdsResponse);
        ZdsZakLa01GeefZaakDetails zakLa01 = (ZdsZakLa01GeefZaakDetails) XmlUtils.getStUFObject(zdsResponse.getBody().toString(), ZdsZakLa01GeefZaakDetails.class);

        var zdsZaak = zakLa01.antwoord.zaak.get(0);

        debug.infopoint("replicatie", "received zaak-data from zds-zaaksysteem for zaak:" + zaakidentificatie + ", now storing in zgw-zaaksysteem");
        this.converter.getZaakService().creeerZaak(rsin, zdsZaak);
    }

    private List<ZdsHeeftRelevant> getLijstZaakdocumenten(String zaakidentificatie) {
        List<ZdsHeeftRelevant> relevanteDocumenten = null;
        var zdsUrl = this.converter.getZaakService().configService.getConfiguration().getReplication().getGeefLijstZaakdocumenten().getUrl();
        var zdsSoapAction = this.converter.getZaakService().configService.getConfiguration().getReplication().getGeefLijstZaakdocumenten().getSoapaction();
        var zdsRequest = new ZdsReplicateGeefLijstZaakdocumentenLv01();
        zdsRequest.stuurgegevens = this.converter.getZdsDocument().stuurgegevens;
        zdsRequest.stuurgegevens.berichtcode = "Lv01";
        zdsRequest.parameters = new ZdsParametersMetSortering();
        zdsRequest.parameters.setSortering("0");
        zdsRequest.parameters.setIndicatorVervolgvraag("false");
        zdsRequest.gelijk = new ZdsZaak();
        zdsRequest.gelijk.identificatie = zaakidentificatie;
        zdsRequest.scope = new ZdsScope();
        zdsRequest.scope.object = new ZdsScopeObject();
        zdsRequest.scope.object.entiteittype = "ZAK";
        zdsRequest.scope.object.heeftRelevant = new ZdsScopeHeeftRelevant();
        zdsRequest.scope.object.heeftRelevant.entiteittype = "ZAKEDC";
        zdsRequest.scope.object.heeftRelevant.gerelateerde = new ZdsScopeGerelateerde();
        zdsRequest.scope.object.heeftRelevant.gerelateerde.entiteittype = "EDC";

        debug.infopoint("StUF:berichtcode", "StUF:berichtcode:" + zdsRequest.stuurgegevens.berichtcode);
                
        var zdsResponse = this.zdsClient.post(zdsUrl, zdsSoapAction, zdsRequest);
        debug.infopoint("replicatie", "GeefLijstZaakdocumenten voor zaak:" + zaakidentificatie);
        var zakZakLa01 = (ZdsZakLa01LijstZaakdocumenten) XmlUtils.getStUFObject(zdsResponse.getBody().toString(),ZdsZakLa01LijstZaakdocumenten.class);

        relevanteDocumenten = zakZakLa01.antwoord.object.heeftRelevant;

        return relevanteDocumenten;
    }

    private void checkVoegZaakDocumentToe(String zaakidentificatie, String rsin, List<ZdsHeeftRelevant> relevanteDocumenten) {
    	debug.infopoint("replicatie", "Zaakdocumenten, count: " + relevanteDocumenten.size() + " for zaak with zaakidentificatie:" + zaakidentificatie);
        for (ZdsHeeftRelevant relevant : relevanteDocumenten) {
            var zaakdocumentidentificatie = relevant.gerelateerde.identificatie;
            debug.infopoint("replicatie", "WARN replicateZaakDocument for zaakidentificatie:" + zaakidentificatie + " with zaakdocumentidentificatie:" + zaakdocumentidentificatie);

            ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = this.converter.getZaakService().zgwClient.getZgwEnkelvoudigInformatieObjectByIdentiticatie(zaakdocumentidentificatie);
            if (zgwEnkelvoudigInformatieObject == null) {
            	debug.infopoint("replicatie", "documentidentificatie #" + zaakdocumentidentificatie);

                var zdsUrl = this.converter.getZaakService().configService.getConfiguration().getReplication().getGeefZaakdocumentLezen().getUrl();
                var zdsSoapAction = this.converter.getZaakService().configService.getConfiguration().getReplication().getGeefZaakdocumentLezen().getSoapaction();
                var zdsRequest = new ZdsReplicateGeefZaakdocumentLezenLv01();                
                 zdsRequest.stuurgegevens = this.converter.getZdsDocument().stuurgegevens;
                 zdsRequest.stuurgegevens.berichtcode = "Lv01";
                 zdsRequest.stuurgegevens.entiteittype = "EDC";
                 zdsRequest.parameters = new ZdsParametersMetSortering();
                 zdsRequest.parameters.setSortering("0");
                 zdsRequest.parameters.setIndicatorVervolgvraag("false");
                 zdsRequest.gelijk = new ZdsZaakDocument();
                 zdsRequest.gelijk.identificatie = zaakdocumentidentificatie;
                 zdsRequest.scope = new ZdsScope();
                 zdsRequest.scope.object = new ZdsScopeObject();
                 zdsRequest.scope.object.setEntiteittype("EDC");
                 zdsRequest.scope.object.setScope("alles");

                 debug.infopoint("StUF:berichtcode", "StUF:berichtcode:" + zdsRequest.stuurgegevens.berichtcode);
                		 
                 var zdsResponse = this.zdsClient.post(zdsUrl, zdsSoapAction, zdsRequest);
                 // fetch the document details
                 log.debug("getGeefZaakdocumentLezen response:" + zdsResponse.getBody().toString());
                 var zdsEdcLa01 =  (ZdsEdcLa01GeefZaakdocumentLezen) XmlUtils.getStUFObject(zdsResponse.getBody().toString(), ZdsEdcLa01GeefZaakdocumentLezen.class);
                 var zdsDocument = zdsEdcLa01.antwoord.document.get(0);

                 // put the zaak in the object, so voegZaakDocument works as expected
                 zdsDocument.isRelevantVoor = new ZdsIsRelevantVoor();
                 zdsDocument.isRelevantVoor.gerelateerde = new ZdsGerelateerde();
                 zdsDocument.isRelevantVoor.gerelateerde.identificatie = zaakidentificatie;

                 log.debug("received data from zds-zaaksysteem, now storing in zgw-zaaksysteem");
                 this.converter.getZaakService().voegZaakDocumentToe(rsin, zdsDocument);
            }
            else {
            	debug.infopoint("replicatie", "[skip] documentidentificatie #" + zaakdocumentidentificatie);
                // TODO: check if zaak-relation is there
            }
        }
    }

    public ResponseEntity<?> proxy() {
		var url = this.converter.getTranslation().getLegacyservice();
		var soapaction = this.converter.getTranslation().getSoapAction();
		var request = this.converter.getContext().getRequestBody();
		debug.infopoint("proxy", "relaying request to url: " + url + " with soapaction: " + soapaction + " request-size:" + request.length());
		return this.zdsClient.post(url, soapaction, request);
	}
}