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
import java.util.List;
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
        ZgwZaak zgwZaak = getZaak(zakLv01.getIdentificatie());

        zaakTranslator.setZgwZaak(zgwZaak);
        zaakTranslator.zgwZaakToZakLa01();

        return zaakTranslator.getDocument();

    }

    public Document getLijstZaakdocumenten(ZakLv01 zakLv01) throws Exception {
        ZgwZaak zgwZaak = getZaak(zakLv01.getIdentificatie());

        Map<String, String> parameters = new HashMap();
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
            String zaakUrl = getZaak(edcLk01.objects.get(0).isRelevantVoor.gerelateerde.identificatie).url;
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

    private ZgwZaak getZaak(String zaakIdentificatie) {
        Map<String, String> parameters = new HashMap();
        parameters.put("identificatie", zaakIdentificatie);

        return zgwClient.getZaakDetails(parameters);
    }

    public EdcLa01 getZaakDoumentLezen(EdcLv01 edcLv01) {
        EdcLa01 edcLa01 = new EdcLa01();

        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = zgwClient.getZgwEnkelvoudigInformatieObject(edcLv01.gelijk.identificatie);

        edcLa01 = zaakTranslator.getEdcLa01FromZgwEnkelvoudigInformatieObject(zgwEnkelvoudigInformatieObject);

        return edcLa01;
    }

    private ZgwStatusType getStatusTypeByZaakTypeAndVolgnummer(String zaakTypeUrl, int volgnummer){
        Map<String, String> parameters = new HashMap();
        parameters.put("zaaktype", zaakTypeUrl);

        return zgwClient.getStatusTypes(parameters)
                .stream()
                .filter(zgwStatusType -> zgwStatusType.volgnummer == volgnummer)
                .findFirst()
                .orElse(null);
    }

    public ZgwZaak actualiseerZaakstatus(ZakLk01_v2 zakLk01) {
        ZakLk01_v2.Object object = zakLk01.objects.get(1);
        ZgwZaak zgwZaak = getZaak(object.identificatie);

        zaakTranslator.setZakLk01(zakLk01);
        ZgwStatus zgwStatus = zaakTranslator.getZgwStatus();
        zgwStatus.zaak = zgwZaak.url;
        zgwStatus.statustype = getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype, Integer.valueOf(object.heeft.gerelateerde.volgnummer)).url;

        zgwClient.actualiseerZaakStatus(zgwStatus);
        return zgwZaak;
    }
}

