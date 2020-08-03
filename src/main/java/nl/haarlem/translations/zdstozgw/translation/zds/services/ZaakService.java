package nl.haarlem.translations.zdstozgw.translation.zds.services;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.DocumentType;
import nl.haarlem.translations.zdstozgw.config.model.Organisatie;
import nl.haarlem.translations.zdstozgw.config.model.ZaakType;
import nl.haarlem.translations.zdstozgw.config.model.ZgwRolOmschrijving;
import nl.haarlem.translations.zdstozgw.translation.BetrokkeneType;
import nl.haarlem.translations.zdstozgw.translation.zds.model.*;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.*;
import nl.haarlem.translations.zdstozgw.utils.ChangeDetector;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ZaakService {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ZGWClient zgwClient;

    private final ModelMapper modelMapper;

    private final ConfigService configService;


    @Autowired
    public ZaakService(ZGWClient zgwClient, ModelMapper modelMapper, ConfigService configService) {
        this.zgwClient = zgwClient;
        this.modelMapper = modelMapper;
        this.configService = configService;
    }

    public ZgwZaak creeerZaak(ZakLk01CreeerZaak zakLk01CreeerZaak) throws Exception {
        var zaak = zakLk01CreeerZaak.objects.get(0);
        ZgwZaak zgwZaak = modelMapper.map(zaak, ZgwZaak.class);
        zgwZaak.zaaktype = getZgwZaakTypeByIdentificatie(zaak.isVan.gerelateerde.code).url;
        zgwZaak.bronorganisatie = getRSIN(zakLk01CreeerZaak.stuurgegevens.zender.organisatie);
        zgwZaak.verantwoordelijkeOrganisatie = getRSIN(zakLk01CreeerZaak.stuurgegevens.ontvanger.organisatie);

        try {
            var createdZaak = zgwClient.addZaak(zgwZaak);
            if (createdZaak.getUrl() != null) {
                log.info("Created a ZGW Zaak with UUID: " + createdZaak.getUuid());
                ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguratie().getZgwRolOmschrijving();
                addRolToZgw(zaak.heeftAlsInitiator, zgwRolOmschrijving.getHeeftAlsInitiator(), createdZaak);
                addRolToZgw(zaak.heeftAlsBelanghebbende, zgwRolOmschrijving.getHeeftAlsBelanghebbende(), createdZaak);
                addRolToZgw(zaak.heeftAlsGemachtigde, zgwRolOmschrijving.getHeeftAlsGemachtigde(), createdZaak);
                addRolToZgw(zaak.heeftAlsOverigBetrokkene, zgwRolOmschrijving.getHeeftAlsOverigBetrokkene(), createdZaak);
                addRolToZgw(zaak.heeftAlsUitvoerende, zgwRolOmschrijving.getHeeftAlsUitvoerende(), createdZaak);
                addRolToZgw(zaak.heeftAlsVerantwoordelijke, zgwRolOmschrijving.getHeeftAlsVerantwoordelijke(), createdZaak);
                return createdZaak;
            }

        } catch (Exception e) {
            //todo throw error, is case created; yes/no?
            throw e;
        }
        return null;
    }

    private void addRolToZgw(Rol rol, String rolOmschrijvingGeneriek, ZgwZaak createdZaak){
        if(rol==null)return;
        ZgwRol zgwRol = new ZgwRol();
        if(rol.gerelateerde.medewerker != null){
            zgwRol.betrokkeneIdentificatie = modelMapper.map(rol.gerelateerde.medewerker, ZgwBetrokkeneIdentificatie.class);
            zgwRol.betrokkeneType = BetrokkeneType.MEDEWERKER.getDescription();
        } else if (rol.gerelateerde.natuurlijkPersoon != null) {
            zgwRol.betrokkeneIdentificatie = modelMapper.map(rol.gerelateerde.natuurlijkPersoon, ZgwBetrokkeneIdentificatie.class);
            zgwRol.betrokkeneType = BetrokkeneType.NATUURLIJK_PERSOON.getDescription();
        } else {
            throw new RuntimeException("Natuurlijkpersoon or medewerker missing for adding roltype to case");
        }
        zgwRol.roltoelichting = rolOmschrijvingGeneriek;
        zgwRol.roltype = zgwClient.getRoltypeByZaakTypeUrlAndOmschrijvingGeneriek(createdZaak.zaaktype,rolOmschrijvingGeneriek).url;
        zgwRol.zaak = createdZaak.getUrl();
        zgwClient.addZgwRol(zgwRol);
    }

    public ZakLa01LijstZaakdocumenten geefLijstZaakdocumenten(ZakLv01 zakLv01) throws Exception {
        ZgwZaak zgwZaak = getZaak(zakLv01.gelijk.identificatie);

        ZakLa01LijstZaakdocumenten zakLa01LijstZaakdocumenten = new ZakLa01LijstZaakdocumenten();
        zakLa01LijstZaakdocumenten.antwoord = new ZakLa01LijstZaakdocumenten.Antwoord();
        zakLa01LijstZaakdocumenten.antwoord.object = new ZakLa01LijstZaakdocumenten.Antwoord.Object();
        zakLa01LijstZaakdocumenten.antwoord.object.identificatie = zgwZaak.identificatie;
        zakLa01LijstZaakdocumenten.antwoord.object.heeftRelevant = new ArrayList<>();

        zakLa01LijstZaakdocumenten.stuurgegevens = new Stuurgegevens(zakLv01.stuurgegevens);
        zakLa01LijstZaakdocumenten.stuurgegevens.berichtcode = "La01";

        this.getZaakInformatieObjectenByZaak(zgwZaak.url).forEach(zgwZaakInformatieObject -> {
            ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = zgwClient.getZaakDocument(zgwZaakInformatieObject.informatieobject);
            ZaakDocument zaakDocument = null;
            if(zgwEnkelvoudigInformatieObject!= null){
                zaakDocument = modelMapper.map(zgwEnkelvoudigInformatieObject, ZaakDocument.class);
            }
            zakLa01LijstZaakdocumenten.antwoord.object.heeftRelevant.add(modelMapper.map(zgwZaakInformatieObject, ZakLa01LijstZaakdocumenten.Antwoord.Object.HeeftRelevant.class)
                                                                                        .setGerelateerde(zaakDocument));
        });

        return zakLa01LijstZaakdocumenten;
    }

    private List<ZgwZaakInformatieObject> getZaakInformatieObjectenByZaak(String zaakUrl){
        Map<String, String> parameters = new HashMap();
        parameters.put("zaak", zaakUrl);

        return this.zgwClient.getZgwZaakInformatieObjects(parameters);
    }

    public ZgwZaakInformatieObject voegZaakDocumentToe(EdcLk01 edcLk01) throws Exception {
        ZgwZaakInformatieObject result = null;

        var informatieObjectType = configService.getConfiguratie().getDocumentTypes().get(0).getDocumentType();
        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = modelMapper.map(edcLk01.objects.get(0), ZgwEnkelvoudigInformatieObject.class);
        zgwEnkelvoudigInformatieObject.informatieobjecttype = informatieObjectType;
        zgwEnkelvoudigInformatieObject.bronorganisatie = getRSIN(edcLk01.stuurgegevens.zender.organisatie);

        zgwEnkelvoudigInformatieObject = zgwClient.addZaakDocument(zgwEnkelvoudigInformatieObject);

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

        var edcLa01 = new EdcLa01();
        edcLa01.stuurgegevens = new Stuurgegevens(edcLv01.stuurgegevens);
        edcLa01.stuurgegevens.berichtcode = "La01";

        edcLa01.antwoord = new EdcLa01.Antwoord();
        edcLa01.antwoord.object = modelMapper.map(zgwEnkelvoudigInformatieObject, EdcLa01.Object.class);

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

    public ZgwZaak actualiseerZaakstatus(ZakLk01ActualiseerZaakstatus zakLk01) {
        ZakLk01ActualiseerZaakstatus.Object object = zakLk01.objects.get(1);
        ZgwZaak zgwZaak = getZaak(object.identificatie);

        ZgwStatus zgwStatus = modelMapper.map(object.heeft, ZgwStatus.class);
        zgwStatus.zaak = zgwZaak.url;
        zgwStatus.statustype = getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype, Integer.valueOf(object.heeft.gerelateerde.volgnummer)).url;

        zgwClient.actualiseerZaakStatus(zgwStatus);
        return zgwZaak;
    }

    public ZakLa01GeefZaakDetails getZaakDetails(ZakLv01 zakLv01) {
        ZakLa01GeefZaakDetails zakLa01GeefZaakDetails = new ZakLa01GeefZaakDetails();

        if (zakLv01.gelijk != null && zakLv01.gelijk.identificatie != null) {
            var zgwZaak = this.getZaak(zakLv01.gelijk.identificatie);
            if (zgwZaak == null)
                throw new RuntimeException("Zaak niet gevonden voor identificatie: '" + zakLv01.gelijk.identificatie + "'");

            zakLa01GeefZaakDetails.stuurgegevens = new Stuurgegevens(zakLv01.stuurgegevens);
            zakLa01GeefZaakDetails.stuurgegevens.berichtcode = "La01";

            zakLa01GeefZaakDetails.antwoord = new ZakLa01GeefZaakDetails.Antwoord();
            zakLa01GeefZaakDetails.antwoord.zaak = modelMapper.map(zgwZaak, ZakLa01GeefZaakDetails.Antwoord.Object.class);

            ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguratie().getZgwRolOmschrijving();

            this.getRollenByZaakUrl(zgwZaak.url).forEach(zgwRol -> {

                if (zgwRolOmschrijving.getHeeftAlsInitiator() != null
                        && zgwRolOmschrijving.getHeeftAlsInitiator().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsInitiator = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsInitiator());
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsInitiator.entiteittype = "ZAKBTRINI";
                }else if (zgwRolOmschrijving.getHeeftAlsBelanghebbende() != null
                        && zgwRolOmschrijving.getHeeftAlsBelanghebbende().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsBelanghebbende = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsBelanghebbende());
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsBelanghebbende.setEntiteittype("ZAKBTRBLH");
                }else if (zgwRolOmschrijving.getHeeftAlsUitvoerende() != null
                        && zgwRolOmschrijving.getHeeftAlsUitvoerende().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsUitvoerende = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsUitvoerende());
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsUitvoerende.entiteittype = "ZAKBTRUTV";
                }else if (zgwRolOmschrijving.getHeeftAlsVerantwoordelijke() != null
                        && zgwRolOmschrijving.getHeeftAlsVerantwoordelijke().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsVerantwoordelijke = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsVerantwoordelijke());
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsVerantwoordelijke.entiteittype = "ZAKBTRVRA";
                }else if (zgwRolOmschrijving.getHeeftAlsGemachtigde() != null
                        && zgwRolOmschrijving.getHeeftAlsGemachtigde().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsGemachtigde = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsGemachtigde());
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsGemachtigde.setEntiteittype("ZAKBTRGMC");
                }else if (zgwRolOmschrijving.getHeeftAlsOverigBetrokkene() != null
                        && zgwRolOmschrijving.getHeeftAlsOverigBetrokkene().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsOverigBetrokkene = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsOverigBetrokkene());
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsOverigBetrokkene.setEntiteittype("ZAKBTROVR");
                }

            });

            ZgwZaakType zgwZaakType = this.getZaakTypeByUrl(zgwZaak.zaaktype);
            zakLa01GeefZaakDetails.object = new ZakLa01GeefZaakDetails.Object();
            zakLa01GeefZaakDetails.object.isVan = new Rol();
            zakLa01GeefZaakDetails.object.isVan.entiteittype = "ZAKZKT";
            zakLa01GeefZaakDetails.object.isVan.gerelateerde = new Gerelateerde();
            zakLa01GeefZaakDetails.object.isVan.gerelateerde.entiteittype = "ZKT";
            zakLa01GeefZaakDetails.object.isVan.gerelateerde.code = zgwZaakType.identificatie;
            zakLa01GeefZaakDetails.object.isVan.gerelateerde.omschrijving = zgwZaakType.omschrijving;

            zakLa01GeefZaakDetails.antwoord.zaak.kenmerk = zgwZaak.getKenmerken() != null && !zgwZaak.getKenmerken().isEmpty()?  modelMapper.map(zgwZaak.getKenmerken().get(0), ZakLa01GeefZaakDetails.Antwoord.Object.Kenmerk.class) : null;
            zakLa01GeefZaakDetails.antwoord.zaak.opschorting = zgwZaak.getOpschorting() != null? modelMapper.map(zgwZaak.getOpschorting(), ZakLa01GeefZaakDetails.Antwoord.Object.Opschorting.class): null;
            zakLa01GeefZaakDetails.antwoord.zaak.verlenging = zgwZaak.getVerlenging() != null? modelMapper.map(zgwZaak.getVerlenging(), ZakLa01GeefZaakDetails.Antwoord.Object.Verlenging.class): null;

            zakLa01GeefZaakDetails.antwoord.zaak.heeft = getStatussenByZaakUrl(zgwZaak.url)
                    .stream()
                    .map(zgwStatus -> {
                        ZgwStatusType zgwStatusType = zgwClient.getResource(zgwStatus.statustype, ZgwStatusType.class);
                        return modelMapper.map(zgwStatus, ZakLa01GeefZaakDetails.Status.class)
                                .setEntiteittype("ZAKSTT")
                                .setIndicatieLaatsteStatus(Boolean.valueOf(zgwStatusType.isEindstatus)?"J":"N");
                    })
                    .collect(Collectors.toList());

        }

        return zakLa01GeefZaakDetails;
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

    public ZgwRol getRolByZaakUrlAndOmschrijvingGeneriek(String zaakUrl, String omschrijvingGeneriek) {
        Map<String, String> parameters = new HashMap();
        parameters.put("zaak", zaakUrl);
        parameters.put("omschrijvingGeneriek", omschrijvingGeneriek);

        return zgwClient.getRollen(parameters)
                .stream()
                .findFirst()
                .orElse(null);
    }

    private List<ZgwStatus> getStatussenByZaakUrl(String zaakUrl){
        Map<String, String> parameters = new HashMap();
        parameters.put("zaak", zaakUrl);

        return zgwClient.getStatussen(parameters);
    }

    private ZaakType getZaakTypeByZGWZaakType(String zgwZaakType) {
        List<ZaakType> zaakTypes = configService.getConfiguratie().getZaakTypes();
        for (ZaakType zaakType : zaakTypes) {
            if (zaakType.getZaakType().equals(zgwZaakType)) {
                return zaakType;
            }
        }
        return null;
    }

    private String getDocumentTypeOmschrijving(String documentType) {
        List<DocumentType> documentTypes = configService.getConfiguratie().getDocumentTypes();
        for (DocumentType type : documentTypes) {
            if (type.getDocumentType().equals(documentType)) {
                return type.getOmschrijving();
            }
        }
        return null;
    }

    public ZgwZaakType getZgwZaakTypeByIdentificatie(String identificatie){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("identificatie", identificatie);

        return zgwClient.getZaakTypes(parameters).get(0);
    }

    public ZaakType getZaakTypeByZDSCode(String zaakTypeCode) {
        List<ZaakType> zaakTypes = configService.getConfiguratie().getZaakTypes();
        for (ZaakType zaakType : zaakTypes) {
            if (zaakType.getCode().equals(zaakTypeCode)) {
                return zaakType;
            }
        }
        return null;
    }

    private String getRSIN(String gemeenteCode) {
        List<Organisatie> organisaties = configService.getConfiguratie().getOrganisaties();
        for (Organisatie organisatie : organisaties) {
            if (organisatie.getGemeenteCode().equals(gemeenteCode)) {
                return organisatie.getRSIN();
            }
        }
        return "";
    }

    public void updateZaak(ZakLk01UpdateZaak ZakLk01UpdateZaak) throws IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        var zdsWasZaak = ZakLk01UpdateZaak.objects.get(0);
        var zdsWijzigingInZaak = ZakLk01UpdateZaak.objects.get(1);

        ChangeDetector changeDetector = new ChangeDetector();
        changeDetector.detect(zdsWasZaak, zdsWijzigingInZaak);

        if(changeDetector.getAllChangesByClass(Zaak.class).size()>0){
            System.out.println("jawol, zaak");
            //         this.modelMapper.map(zdsWijzigingInZaak, ZgwZaak.class);

            //todo: check nested objects that don't have to be updated seperately.. like opschorting, verlenging, etc
        }

        Map<ChangeDetector.Change, ChangeDetector.ChangeType> rolChanges = changeDetector.getAllChangesByClass(Rol.class);

        if(rolChanges.size()>0){

            changeDetector.filterChangesByType(rolChanges, ChangeDetector.ChangeType.NEW).forEach((change, changeType) -> {
                //todo: add rol to ZGW
            });

            changeDetector.filterChangesByType(rolChanges, ChangeDetector.ChangeType.DELETED).forEach((change, changeType) -> {
                //todo: delete rol from ZGW
            });

            changeDetector.filterChangesByType(rolChanges, ChangeDetector.ChangeType.CHANGED).forEach((change, changeType) -> {
                //todo: change rol in ZGW
            });

        }

    }



//	public String updateZaak(ZakLk01 zakLk01) {
//        var zdsWasZaak = zakLk01.objects.get(0);
//        var zdsWordtZaak = zakLk01.objects.get(1);
//
//        if (zdsWasZaak.identificatie.length() == 0) throw new RuntimeException("zaak identificatie is verplicht");
//        var zgwZaak = getZaak(zdsWasZaak.identificatie);
//        if(zgwZaak == null) throw new RuntimeException("zaak met identificatie: " + zdsWasZaak.identificatie + " niet gevonden");
//
//        var changed = new ChangeDetector();
//        changed.compare(zdsWasZaak.identificatie, zdsWordtZaak.identificatie,  new Runnable() { public void run() {zgwZaak.identificatie = zdsWordtZaak.identificatie; } } );
//        changed.compare(zdsWasZaak.omschrijving, zdsWordtZaak.omschrijving,  new Runnable() { public void run() {zgwZaak.omschrijving = zdsWordtZaak.omschrijving; } } );
//        changed.compare(zdsWasZaak.toelichting, zdsWordtZaak.toelichting,  new Runnable() { public void run() {zgwZaak.toelichting = zdsWordtZaak.toelichting; } } );
//        changed.compare(zdsWasZaak.registratiedatum, zdsWordtZaak.registratiedatum,  new Runnable() { public void run() {zgwZaak.registratiedatum = getDateStringFromZdsDate(zdsWordtZaak.registratiedatum); } } );
//        changed.compare(zdsWasZaak.startdatum, zdsWordtZaak.startdatum,  new Runnable() { public void run() {zgwZaak.startdatum = getDateStringFromZdsDate(zdsWordtZaak.startdatum); } } );
//        changed.compare(zdsWasZaak.einddatum, zdsWordtZaak.einddatum,  new Runnable() { public void run() {zgwZaak.einddatum = getDateStringFromZdsDate(zdsWordtZaak.einddatum); } } );
//        changed.compare(zdsWasZaak.einddatumGepland, zdsWordtZaak.einddatumGepland,  new Runnable() { public void run() {zgwZaak.einddatumGepland = getDateStringFromZdsDate(zdsWordtZaak.einddatumGepland); } } );
//
//        if(zdsWasZaak.opschorting != null || zdsWordtZaak.opschorting != null) {
//            if(zdsWasZaak.opschorting == null) {
//                zdsWasZaak.opschorting = new ZdsOpschorting();
//            }
//            if(zdsWordtZaak.opschorting == null) {
//                zdsWordtZaak.opschorting = new ZdsOpschorting();
//            }
//            if(zgwZaak.opschorting == null) {
//                zgwZaak.opschorting = new Opschorting();
//            }
//            changed.compare(zdsWasZaak.opschorting.indicatie, zdsWordtZaak.opschorting.indicatie,  new Runnable() { public void run() {zgwZaak.opschorting.indicatie = zdsWordtZaak.opschorting.indicatie.equals("J"); } } );
//            changed.compare(zdsWasZaak.opschorting.reden, zdsWordtZaak.opschorting.reden,  new Runnable() { public void run() {zgwZaak.opschorting.reden = zdsWordtZaak.opschorting.reden; } } );
//        }
//
//        if(changed.isDirty()) {
//            zgwClient.put(zgwZaak);
//        }
//
//        var rollen = zgwClient.getRollenByZaak(zgwZaak.url);
//        updateZaak(zgwZaak, rollen, "Betrekking", zdsWasZaak.heeftBetrekkingOp, zdsWordtZaak.heeftBetrekkingOp);
//        updateZaak(zgwZaak, rollen, "Belanghebbende", zdsWasZaak.heeftAlsBelanghebbende, zdsWordtZaak.heeftAlsBelanghebbende);
//        updateZaak(zgwZaak, rollen, "Initiator", zdsWasZaak.heeftAlsInitiator, zdsWordtZaak.heeftAlsInitiator);
//        updateZaak(zgwZaak, rollen, "Uitvoerende", zdsWasZaak.heeftAlsUitvoerende, zdsWordtZaak.heeftAlsUitvoerende);
//        updateZaak(zgwZaak, rollen, "Verantwoordelijke", zdsWasZaak.heeftAlsVerantwoordelijke, zdsWordtZaak.heeftAlsVerantwoordelijke);
//
//        return zgwZaak;
//	}

//    private void updateZaak(ZgwBasicZaak zgwZaak, List<ZgwRol> zgwRollen, String rolnaam, ZdsRol zdsWasRol, ZdsRol zdsWordtRol) throws ZaakTranslatorException, ZGWClientException {
//        var zgwRoltype = zgwClient.getRolTypeByOmschrijving(zgwZaak.zaaktype, rolnaam);
//        if(zgwRoltype == null) throw new ZaakTranslatorException("Geen roltype niet gevonden ZTC voor identificatie: '" + rolnaam);
//        // filter waar het ons roltype betreft
//        zgwRollen = zgwRollen.stream().filter(r -> r.roltype.equals(zgwRoltype.url)).collect(Collectors.toList());
//
//        // nu gaan we het vergelijken starten
//        var wasRollen = getRollen(zgwZaak, zdsWasRol, rolnaam);
//        var wordtRollen = getRollen(zgwZaak, zdsWordtRol, rolnaam);
//
//        // acties die moeten gebeuren
//        var postRollen = new java.util.ArrayList<ZgwRol>();
//        var putRollen = new java.util.ArrayList<ZgwRol>();
//        var deleteRollen = new java.util.ArrayList<ZgwRol>();
//        var nothingRollen = new java.util.ArrayList<ZgwRol>();
//
//        // alles wat we gehad hebben verwijderen we uit de lijst
//        while(wasRollen.size() > 0 || wordtRollen.size() > 0 ) {
//            if(wasRollen.size() == 0) {
//                // er moet een rol bijkomen
//                var wordtRol = wordtRollen.get(0);
//                wordtRol.setZaak(zgwZaak.getUrl());
//                postRollen.add(wordtRol);
//                // deze hebben we gehad
//                wordtRollen.remove(0);
//            }
//            else if(wordtRollen.size() == 0) {
//                // deze rol moet verwijderd worden
//                var wasRol = wasRollen.get(0);
//                Boolean gevonden = false;
//                for(ZgwRol zgwRol : zgwRollen) {
//                    if(zgwRol.betrokkeneType.equals(wasRol.betrokkeneType)
//                            &&
//                            (
//                                    (zgwRol.betrokkeneIdentificatie.inpBsn == null && wasRol.betrokkeneIdentificatie.inpBsn == null)
//                                            ||
//                                            zgwRol.betrokkeneIdentificatie.inpBsn.equals(wasRol.betrokkeneIdentificatie.inpBsn)
//                            )
//                            &&
//                            (
//                                    (zgwRol.betrokkeneIdentificatie.identificatie != null && wasRol.betrokkeneIdentificatie.identificatie != null)
//                                            ||
//                                            zgwRol.betrokkeneIdentificatie.identificatie.equals(wasRol.betrokkeneIdentificatie.identificatie)
//                            )
//                    )
//                    {
//                        // er is een rol teveel
//                        gevonden = true;
//                        deleteRollen.add(zgwRol);
//                        break;
//                    }
//                }
//                if(!gevonden) log.warn("rol niet terug gevonden in openzaak!");
//                // deze hebben we gehad
//                wasRollen.remove(0);
//            }
//            else {
//                var wordtRol = wordtRollen.get(0);
//                Boolean gevonden = false;
//                for(ZgwRol wasRol : wasRollen) {
//                    if(
//                            wasRol.betrokkeneType.equals(wordtRol.betrokkeneType)
//                                    &&
//                                    (
//                                            (wasRol.betrokkeneIdentificatie.inpBsn == null && wordtRol.betrokkeneIdentificatie.inpBsn == null)
//                                                    ||
//                                                    wasRol.betrokkeneIdentificatie.inpBsn.equals(wordtRol.betrokkeneIdentificatie.inpBsn)
//                                    )
//                                    &&
//                                    (
//                                            (wasRol.betrokkeneIdentificatie.identificatie != null && wordtRol.betrokkeneIdentificatie.identificatie != null)
//                                                    ||
//                                                    wasRol.betrokkeneIdentificatie.identificatie.equals(wordtRol.betrokkeneIdentificatie.identificatie)
//                                    )
//                    )
//                    {
//                        ZgwRol zgwRol = null;
//                        for(ZgwRol rol : zgwRollen) {
//                            if(rol.betrokkeneType.equals(wordtRol.betrokkeneType)
//                                    &&
//                                    (
//                                            (rol.betrokkeneIdentificatie.inpBsn == null && wordtRol.betrokkeneIdentificatie.inpBsn == null)
//                                                    ||
//                                                    rol.betrokkeneIdentificatie.inpBsn.equals(wordtRol.betrokkeneIdentificatie.inpBsn)
//                                    )
//                                    &&
//                                    (
//                                            (rol.betrokkeneIdentificatie.identificatie != null && wordtRol.betrokkeneIdentificatie.identificatie != null)
//                                                    ||
//                                                    rol.betrokkeneIdentificatie.identificatie.equals(wordtRol.betrokkeneIdentificatie.identificatie)
//                                    )
//                            )
//                            {
//                                // er is een rol teveel
//                                zgwRol = rol;
//                                break;
//                            }
//                        }
//                        if(zgwRol == null) log.warn("rol niet terug gevonden in openzaak!");
//                        {
//                            // for runnable has to be from final
//                            final ZgwRol rol = zgwRol;
//
//                            var changed = new ChangeDetector();
//                            changed.compare(wasRol.betrokkeneIdentificatie.anpIdentificatie, wordtRol.betrokkeneIdentificatie.anpIdentificatie,  new Runnable() { public void run() { rol.betrokkeneIdentificatie.anpIdentificatie = wordtRol.betrokkeneIdentificatie.anpIdentificatie; } } );
//                            changed.compare(wasRol.betrokkeneIdentificatie.inpA_nummer, wordtRol.betrokkeneIdentificatie.inpA_nummer,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.inpA_nummer = wordtRol.betrokkeneIdentificatie.inpA_nummer; } } );
//                            changed.compare(wasRol.betrokkeneIdentificatie.inpgeslachtsnaam, wordtRol.betrokkeneIdentificatie.inpgeslachtsnaam,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.inpgeslachtsnaam = wordtRol.betrokkeneIdentificatie.inpgeslachtsnaam; } } );
//                            changed.compare(wasRol.betrokkeneIdentificatie.geslachtsnaam, wordtRol.betrokkeneIdentificatie.geslachtsnaam,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.geslachtsnaam = wordtRol.betrokkeneIdentificatie.geslachtsnaam; } } );
//                            changed.compare(wasRol.betrokkeneIdentificatie.voorvoegselGeslachtsnaam, wordtRol.betrokkeneIdentificatie.voorvoegselGeslachtsnaam,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.voorvoegselGeslachtsnaam = wordtRol.betrokkeneIdentificatie.voorvoegselGeslachtsnaam; } } );
//                            changed.compare(wasRol.betrokkeneIdentificatie.voornamen, wordtRol.betrokkeneIdentificatie.voornamen,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.voornamen = wordtRol.betrokkeneIdentificatie.voornamen; } } );
//                            changed.compare(wasRol.betrokkeneIdentificatie.geslachtsaanduiding, wordtRol.betrokkeneIdentificatie.geslachtsaanduiding,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.geslachtsaanduiding = wordtRol.betrokkeneIdentificatie.geslachtsaanduiding; } } );
//                            changed.compare(wasRol.betrokkeneIdentificatie.geboortedatum, wordtRol.betrokkeneIdentificatie.geboortedatum,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.geboortedatum = wordtRol.betrokkeneIdentificatie.geboortedatum; } } );
//// TODO: verhuizingen!
////								changed.compare(wasRol.betrokkeneIdentificatie.verblijfsadres, wordtRol.betrokkeneIdentificatie.verblijfsadres,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.verblijfsadres = wordtRol.betrokkeneIdentificatie.verblijfsadres; } } );
////								changed.compare(wasRol.betrokkeneIdentificatie.subVerblijfBuitenland, wordtRol.betrokkeneIdentificatie.subVerblijfBuitenland,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.subVerblijfBuitenland = wordtRol.betrokkeneIdentificatie.subVerblijfBuitenland; } } );
//                            if(changed.isDirty()) {
//                                putRollen.add(rol);
//                            } else {
//                                nothingRollen.add(rol);
//                            }
//
//                        }
//                        // deze hebben we gehad
//                        if(!wasRollen.remove(wasRol)) throw new ZaakTranslatorException("fout bij het verwijderen van rol uit de rollenlijst");
//                        break;
//                    }
//                }
//                // deze hebben we gehad
//                wordtRollen.remove(0);
//            }
//        }
//        log.info("nothing rollen:" + nothingRollen.size());
//        log.info("post rollen:" + postRollen.size());
//        log.info("put rollen:" + putRollen.size());
//        log.info("delete rollen:" + deleteRollen.size());
//
//        for(ZgwRol rol : postRollen) {
//            zgwClient.postRol(rol);
//        }
//        for(ZgwRol rol : putRollen) {
//            zgwClient.put(rol);
//        }
//        for(ZgwRol rol : deleteRollen) {
//            zgwClient.delete(rol);
//        }
//    }

//    class ChangeDetector {
//        private Boolean dirty = false;
//
//        public Boolean compare(String eersteWaarde, String tweedeWaarde) {
//            if(eersteWaarde == null && tweedeWaarde == null) return false;
//            if(eersteWaarde == null) return true;
//            return !eersteWaarde.equals(tweedeWaarde);
//        }
//
//        public void compare(String eersteWaarde, String tweedeWaarde, Runnable bijverschil) {
//            Boolean different = compare(eersteWaarde, tweedeWaarde);
//            if(different) {
//                bijverschil.run();
//                dirty = true;
//            }
//        }
//
//        public Boolean isDirty() {
//            return dirty;
//        }
//    }
}

