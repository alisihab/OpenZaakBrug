package nl.haarlem.translations.zdstozgw.translation.zds.services;

import nl.haarlem.translations.zdstozgw.translation.ZaakTranslator;
import nl.haarlem.translations.zdstozgw.translation.zds.model.*;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZaakService {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private ZGWClient zgwClient;

    @Autowired
    private ZaakTranslator zaakTranslator;

    public ZgwZaak creeerZaak(ZakLk01_v2 zakLk01) throws Exception {


        //zaakTranslator.setDocument((Document) zakLk01).zdsZaakToZgwZaak();
        zaakTranslator.setZakLk01(zakLk01).zdsZaakToZgwZaak();

        ZgwZaak zaak = zaakTranslator.getZgwZaak();

        try {
            var createdZaak = zgwClient.addZaak(zaak);
            if (createdZaak.getUrl() != null) {
                log.info("Created a ZGW Zaak with UUID: " + createdZaak.getUuid());
                var rol = zaakTranslator.getRolInitiator();
                if(rol != null){
                    rol.setZaak(createdZaak.getUrl());
                    zgwClient.addRolNPS(rol);
                }
                return createdZaak;
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    public Document getZaakDetails(ZakLv01 zakLv01) throws Exception {

        Map<String, String> parameters = new HashMap();
        parameters.put("identificatie", zakLv01.getIdentificatie());

        ZgwZaak zgwZaak = zgwClient.getZaakDetails(parameters);

        zaakTranslator.setZgwZaak(zgwZaak);
        zaakTranslator.zgwZaakToZakLa01();

        return zaakTranslator.getDocument();

    }

    public Document getLijstZaakdocumenten(ZakLv01 zakLv01) throws Exception {

        Map<String, String> parameters = new HashMap();
        parameters.put("identificatie", zakLv01.getIdentificatie());

        ZgwZaak zgwZaak = zgwClient.getZaakDetails(parameters);

        parameters = new HashMap();
        parameters.put("zaak", zgwZaak.getUrl());

        var zaakInformatieObjecten = zgwClient.getLijstZaakDocumenten(parameters);


        zaakTranslator.setZgwEnkelvoudigInformatieObjectList(zaakInformatieObjecten);
        zaakTranslator.zgwEnkelvoudingInformatieObjectenToZSDLijstZaakDocumenten();

        return zaakTranslator.getDocument();

    }

    public ZgwZaakInformatieObject voegZaakDocumentToe(EdcLk01 edcLk01) throws Exception {
        ZgwZaakInformatieObject result = null;

        zaakTranslator.setEdcLk01(edcLk01).zdsDocumentToZgwDocument();

        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = zgwClient.addDocument(zaakTranslator.getZgwEnkelvoudigInformatieObject());

        if (zgwEnkelvoudigInformatieObject.getUrl() != null) {
            String zaakUrl = getZaakUrl(edcLk01.objects.get(0).isRelevantVoor.gerelateerde.identificatie);
            result = addZaakInformatieObject(zgwEnkelvoudigInformatieObject, zaakUrl);
        } else {
            throw new Exception("Document not added");
        }
        return result;
    }


    private ZgwZaakInformatieObject addZaakInformatieObject(ZgwEnkelvoudigInformatieObject doc, String zaakUrl) throws Exception {
        ZgwZaakInformatieObject result = null;
        try {
            var zgwZaakInformatieObject = new ZgwZaakInformatieObject();
            zgwZaakInformatieObject.setZaak(zaakUrl);
            zgwZaakInformatieObject.setInformatieobject(doc.getUrl());
            zgwZaakInformatieObject.setTitel(doc.getTitel());
            result = zgwClient.addDocumentToZaak(zgwZaakInformatieObject);

        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    private String getZaakUrl(String zaakIdentificatie) {
        Map<String, String> options = new HashMap();
        Map<String, String> parameters = new HashMap();
        parameters.put("identificatie", zaakIdentificatie);

        ZgwZaak zgwZaak = zgwClient.getZaakDetails(parameters);
        return zgwZaak.getUrl();
    }

    public EdcLa01 getZaakDoumentLezen(EdcLv01 edcLv01) {
        EdcLa01 edcLa01 = new EdcLa01();

        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = zgwClient.getZgwEnkelvoudigInformatieObject(edcLv01.gelijk.identificatie);

        edcLa01 = zaakTranslator.getEdcLa01FromZgwEnkelvoudigInformatieObject(zgwEnkelvoudigInformatieObject);

        return edcLa01;
    }

    private String getStatusTypeUrl(String zaakTypeUrl, String statusTypeVolgnummer){
        Map<String, String> parameters = new HashMap();
        parameters.put("zaaktype", zaakTypeUrl);
        parameters.put("volgnummer", statusTypeVolgnummer);

        ZgwSatusType zgwSatusType= zgwClient.getStatusType(parameters);
        return zgwSatusType.url;

    }

    public void actualiseerZaakstatus(ZakLk01_v2 zakLk01) {
        zaakTranslator.setZakLk01(zakLk01);
        ZgwStatus zgwSatus = zaakTranslator.getZgwStatus();
        zgwSatus.zaak = getZaakUrl(zakLk01.getObjects().get(1).identificatie);

        zgwClient.actualiseerZaakStatus(zgwSatus);
    }
}

