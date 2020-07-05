package nl.haarlem.translations.zdstozgw.translation.zds.services;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.ZgwRolOmschrijving;
import nl.haarlem.translations.zdstozgw.translation.ZaakTranslator;
import nl.haarlem.translations.zdstozgw.translation.zds.model.*;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.*;
import org.modelmapper.ModelMapper;
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

    private final ZGWClient zgwClient;

    private final ModelMapper modelMapper;

    private final ZaakTranslator zaakTranslator;

    private final ConfigService configService;

    @Autowired
    public ZaakService(ZGWClient zgwClient, ModelMapper modelMapper, ZaakTranslator zaakTranslator, ConfigService configService) {
        this.zgwClient = zgwClient;
        this.modelMapper = modelMapper;
        this.zaakTranslator = zaakTranslator;
        this.configService = configService;
    }


    public ZgwZaak creeerZaak(ZakLk01 zakLk01) throws Exception {


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

        return zgwClient.getZaak(parameters);
    }


    public EdcLa01 getZaakDocumentLezen(EdcLv01 edcLv01) {

        //Get Enkelvoudig informatie object. This contains document meta data and a link to the document
        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = zgwClient.getZgwEnkelvoudigInformatieObject(edcLv01.gelijk.identificatie);
        var inhoud = zgwClient.getBas64Inhoud(zgwEnkelvoudigInformatieObject.getInhoud());

        //Get zaakinformatieobject, this contains the link to the zaak
        var zgwZaakInformatieObject = getZgwZaakInformatieObject(zgwEnkelvoudigInformatieObject);
        //Get the zaak, to get the zaakidentificatie
        var zgwZaak = zgwClient.getZaakByUrl(zgwZaakInformatieObject.getZaak());

        var edcLa01 = zaakTranslator.getEdcLa01FromZgwEnkelvoudigInformatieObject(zgwEnkelvoudigInformatieObject);

        //Add Inhoud
        edcLa01.isRelevantVoor = new IsRelevantVoor();
        edcLa01.isRelevantVoor.gerelateerde = new Gerelateerde();
        edcLa01.isRelevantVoor.entiteittype = "EDCZAK";
        edcLa01.isRelevantVoor.gerelateerde.entiteittype = "ZAK";
        edcLa01.isRelevantVoor.gerelateerde.identificatie = zgwZaak.getIdentificatie();
        edcLa01.antwoord.object.inhoud = inhoud;

        return edcLa01;
    }


    private ZgwZaakInformatieObject getZgwZaakInformatieObject(ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject) {
        Map<String, String> parameters = new HashMap();
        parameters.put("informatieobject", zgwEnkelvoudigInformatieObject.getUrl());
        return zgwClient.getZgwZaakInformatieObjects(parameters).get(0);
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

    public ZgwZaak actualiseerZaakstatus(ZakLk01 zakLk01) {
        ZakLk01.Object object = zakLk01.objects.get(1);
        ZgwZaak zgwZaak = getZaak(object.identificatie);

        zaakTranslator.setZakLk01(zakLk01);
        ZgwStatus zgwStatus = zaakTranslator.getZgwStatus();
        zgwStatus.zaak = zgwZaak.url;
        zgwStatus.statustype = getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype, Integer.valueOf(object.heeft.gerelateerde.volgnummer)).url;

        zgwClient.actualiseerZaakStatus(zgwStatus);
        return zgwZaak;
    }

    //todo: handle exceptions
    public ZakLa01 getZaakDetails(ZakLv01_v2 zakLv01) {
        ZakLa01 zakLa01 = new ZakLa01();

        if (zakLv01.gelijk != null && zakLv01.gelijk.identificatie != null) {
            var zgwZaak = this.getZaak(zakLv01.gelijk.identificatie);
            if (zgwZaak == null)
                throw new RuntimeException("Zaak niet gevonden voor identificatie: '" + zakLv01.gelijk.identificatie + "'");

            zakLa01.stuurgegevens = new Stuurgegevens(zakLv01.stuurgegevens);
            zakLa01.stuurgegevens.berichtcode = "La01";
            zakLa01.antwoord = new ZakLa01.Antwoord();
            zakLa01.antwoord.zaak = modelMapper.map(zgwZaak, ZakLa01.Antwoord.Zaak.class);

            ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguratie().getZgwRolOmschrijving();

            this.getRollenByZaakUrl(zgwZaak.url).forEach(zgwRol -> {

                if (zgwRolOmschrijving.getHeeftAlsInitiator() != null
                        && zgwRolOmschrijving.getHeeftAlsInitiator().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01.antwoord.zaak.heeftAlsInitiator = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsInitiator());
                }else if (zgwRolOmschrijving.getHeeftAlsBelanghebbende() != null
                        && zgwRolOmschrijving.getHeeftAlsBelanghebbende().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01.antwoord.zaak.heeftAlsBelanghebbende = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsBelanghebbende());
                }else if (zgwRolOmschrijving.getHeeftAlsUitvoerende() != null
                        && zgwRolOmschrijving.getHeeftAlsUitvoerende().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01.antwoord.zaak.heeftAlsUitvoerende = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsUitvoerende());
                }else if (zgwRolOmschrijving.getHeeftAlsVerantwoordelijke() != null
                        && zgwRolOmschrijving.getHeeftAlsVerantwoordelijke().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01.antwoord.zaak.heeftAlsVerantwoordelijke = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsVerantwoordelijke());
                }else if (zgwRolOmschrijving.getHeeftAlsGemachtigde() != null
                        && zgwRolOmschrijving.getHeeftAlsGemachtigde().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01.antwoord.zaak.heeftAlsGemachtigde = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsGemachtigde());
                }else if (zgwRolOmschrijving.getHeeftAlsOverigBetrokkene() != null
                        && zgwRolOmschrijving.getHeeftAlsOverigBetrokkene().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01.antwoord.zaak.heeftAlsOverigBetrokkene = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsOverigBetrokkene());
                }

            });

            ZgwZaakType zgwZaakType = this.getZaakTypeByUrl(zgwZaak.zaaktype);
            zakLa01.object = new ZakLa01.Object();
            zakLa01.object.isVan = new Rol();
            zakLa01.object.isVan.gerelateerde = new Gerelateerde();
            zakLa01.object.isVan.gerelateerde.code = zgwZaakType.identificatie;
            zakLa01.object.isVan.gerelateerde.omschrijving = zgwZaakType.omschrijving;

            zakLa01.antwoord.zaak.kenmerk = zgwZaak.getKenmerken() != null && !zgwZaak.getKenmerken().isEmpty()?  modelMapper.map(zgwZaak.getKenmerken().get(0), ZakLa01.Antwoord.Zaak.Kenmerk.class) : null;
            zakLa01.antwoord.zaak.opschorting = zgwZaak.getOpschorting() != null? modelMapper.map(zgwZaak.getOpschorting(), ZakLa01.Antwoord.Zaak.Opschorting.class): null;
            zakLa01.antwoord.zaak.verlening = zgwZaak.getVerlenging() != null? modelMapper.map(zgwZaak.getVerlenging(), ZakLa01.Antwoord.Zaak.Verlenging.class): null;

            if(zgwZaak.getStatus() != null ){
                ZgwStatus zgwStatus = zgwClient.getStatus(zgwZaak.status);
                zakLa01.antwoord.zaak.heeft = modelMapper.map(zgwStatus, ZakLa01.Antwoord.Zaak.Status.class);
            }

        }

        return zakLa01;
    }

    private ZgwZaakType getZaakTypeByUrl(String url) {
        return zgwClient.getZaakTypes(null)
                .stream()
                .filter(zgwZaakType -> zgwZaakType.url.equalsIgnoreCase(url))
                .findFirst()
                .orElse(null);
    }

    private Rol getZdsRol(ZgwZaak zgwZaak, String rolOmschrijving){
        ZgwRol zgwRol = this.getRolByZaakUrlAndOmschrijvingGeneriek(zgwZaak.url, rolOmschrijving);
        if (zgwRol == null) return null;
        return this.modelMapper.map(zgwRol, Rol.class);
    }

    private List<ZgwRol> getRollenByZaakUrl(String zaakUrl) {
        Map<String, String> parameters = new HashMap();
        parameters.put("zaak", zaakUrl);

        return zgwClient.getRollen(parameters);
    }

    private ZgwRol getRolByZaakUrlAndOmschrijvingGeneriek(String zaakUrl, String omschrijvingGeneriek) {
        Map<String, String> parameters = new HashMap();
        parameters.put("zaak", zaakUrl);
        parameters.put("omschrijvingGeneriek", omschrijvingGeneriek);

        return zgwClient.getRollen(parameters)
                .stream()
                .findFirst()
                .orElse(null);
    }

}

