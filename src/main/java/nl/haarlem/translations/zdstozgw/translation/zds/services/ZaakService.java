package nl.haarlem.translations.zdstozgw.translation.zds.services;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.Organisatie;
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

    public ZgwZaak creeerZaak(ZakLk01 zakLk01CreeerZaak) throws Exception {
        var zaak = zakLk01CreeerZaak.objects.get(0);

        ZgwZaak zgwZaak = modelMapper.map(zaak, ZgwZaak.class);
        zgwZaak.zaaktype = zgwClient.getZgwZaakTypeByIdentificatie(zaak.isVan.gerelateerde.code).url;
        zgwZaak.bronorganisatie = getRSIN(zakLk01CreeerZaak.stuurgegevens.zender.organisatie);
        zgwZaak.verantwoordelijkeOrganisatie = getRSIN(zakLk01CreeerZaak.stuurgegevens.ontvanger.organisatie);
        if(zaak.getKenmerk() != null && !zaak.getKenmerk().isEmpty()){
            zgwZaak.kenmerken =  new ArrayList<>();
            zaak.getKenmerk().forEach(kenmerk -> {
                zgwZaak.kenmerken.add(modelMapper.map(kenmerk, ZgwKenmerk.class));
            });
        }

        try {
            var createdZaak = zgwClient.addZaak(zgwZaak);
            if (createdZaak.getUrl() != null) {
                log.debug("Created a ZGW Zaak with UUID: " + createdZaak.getUuid());
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
        ZgwZaak zgwZaak = zgwClient.getZaak(zakLv01.gelijk.identificatie);

        ZakLa01LijstZaakdocumenten zakLa01LijstZaakdocumenten = new ZakLa01LijstZaakdocumenten();
        zakLa01LijstZaakdocumenten.antwoord = new ZakLa01LijstZaakdocumenten.Antwoord();
        zakLa01LijstZaakdocumenten.antwoord.object = new ZakLa01LijstZaakdocumenten.Antwoord.Object();
        zakLa01LijstZaakdocumenten.antwoord.object.identificatie = zgwZaak.identificatie;
        zakLa01LijstZaakdocumenten.antwoord.object.heeftRelevant = new ArrayList<>();

        zakLa01LijstZaakdocumenten.stuurgegevens = new Stuurgegevens(zakLv01.stuurgegevens);
        zakLa01LijstZaakdocumenten.stuurgegevens.berichtcode = "La01";

        zgwClient.getZaakInformatieObjectenByZaak(zgwZaak.url).forEach(zgwZaakInformatieObject -> {
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

    public ZgwZaakInformatieObject voegZaakDocumentToe(EdcLk01 edcLk01) throws Exception {
        ZgwZaakInformatieObject result = null;

        var informatieObjectType = configService.getConfiguratie().getDocumentTypes().get(0).getDocumentType();
        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = modelMapper.map(edcLk01.objects.get(0), ZgwEnkelvoudigInformatieObject.class);
        zgwEnkelvoudigInformatieObject.informatieobjecttype = informatieObjectType;
        zgwEnkelvoudigInformatieObject.bronorganisatie = getRSIN(edcLk01.stuurgegevens.zender.organisatie);

        zgwEnkelvoudigInformatieObject = zgwClient.addZaakDocument(zgwEnkelvoudigInformatieObject);

        if (zgwEnkelvoudigInformatieObject.getUrl() != null) {
            String zaakUrl = zgwClient.getZaak(edcLk01.objects.get(0).isRelevantVoor.gerelateerde.identificatie).url;
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

    public EdcLa01 getZaakDocumentLezen(EdcLv01 edcLv01) {
        //Get Enkelvoudig informatie object. This contains document meta data and a link to the document
        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = zgwClient.getZgwEnkelvoudigInformatieObject(edcLv01.gelijk.identificatie);
        var inhoud = zgwClient.getBas64Inhoud(zgwEnkelvoudigInformatieObject.getInhoud());

        //Get zaakinformatieobject, this contains the link to the zaak
        var zgwZaakInformatieObject = zgwClient.getZgwZaakInformatieObject(zgwEnkelvoudigInformatieObject);
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

    public ZgwZaak actualiseerZaakstatus(ZakLk01ActualiseerZaakstatus zakLk01) {
        ZakLk01ActualiseerZaakstatus.Object object = zakLk01.objects.get(1);
        ZgwZaak zgwZaak = zgwClient.getZaak(object.identificatie);

        ZgwStatus zgwStatus = modelMapper.map(object.heeft, ZgwStatus.class);
        zgwStatus.zaak = zgwZaak.url;
        zgwStatus.statustype = zgwClient.getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype, Integer.valueOf(object.heeft.gerelateerde.volgnummer)).url;

        zgwClient.actualiseerZaakStatus(zgwStatus);
        return zgwZaak;
    }

    public ZakLa01GeefZaakDetails getZaakDetails(ZakLv01 zakLv01) {
        ZakLa01GeefZaakDetails zakLa01GeefZaakDetails = new ZakLa01GeefZaakDetails();

        if (zakLv01.gelijk != null && zakLv01.gelijk.identificatie != null) {
            var zgwZaak = zgwClient.getZaak(zakLv01.gelijk.identificatie);
            if (zgwZaak == null)
                throw new RuntimeException("Zaak niet gevonden voor identificatie: '" + zakLv01.gelijk.identificatie + "'");

            zakLa01GeefZaakDetails.stuurgegevens = new Stuurgegevens(zakLv01.stuurgegevens);
            zakLa01GeefZaakDetails.stuurgegevens.berichtcode = "La01";

            zakLa01GeefZaakDetails.antwoord = new ZakLa01GeefZaakDetails.Antwoord();
            zakLa01GeefZaakDetails.antwoord.zaak = new ZakLa01GeefZaakDetails.Antwoord.Object();
            zakLa01GeefZaakDetails.antwoord.zaak = modelMapper.map(zgwZaak, ZakLa01GeefZaakDetails.Antwoord.Object.class);

            ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguratie().getZgwRolOmschrijving();

            zgwClient.getRollenByZaakUrl(zgwZaak.url).forEach(zgwRol -> {

                if (zgwRolOmschrijving.getHeeftAlsInitiator() != null
                        && zgwRolOmschrijving.getHeeftAlsInitiator().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsInitiator = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsInitiator(),"ZAKBTRINI");
                }else if (zgwRolOmschrijving.getHeeftAlsBelanghebbende() != null
                        && zgwRolOmschrijving.getHeeftAlsBelanghebbende().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsBelanghebbende = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsBelanghebbende(),"ZAKBTRBLH");
                }else if (zgwRolOmschrijving.getHeeftAlsUitvoerende() != null
                        && zgwRolOmschrijving.getHeeftAlsUitvoerende().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsUitvoerende = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsUitvoerende(),"ZAKBTRUTV");
                }else if (zgwRolOmschrijving.getHeeftAlsVerantwoordelijke() != null
                        && zgwRolOmschrijving.getHeeftAlsVerantwoordelijke().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsVerantwoordelijke = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsVerantwoordelijke(),"ZAKBTRVRA");
                }else if (zgwRolOmschrijving.getHeeftAlsGemachtigde() != null
                        && zgwRolOmschrijving.getHeeftAlsGemachtigde().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsGemachtigde = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsGemachtigde(),"ZAKBTRGMC");
                }else if (zgwRolOmschrijving.getHeeftAlsOverigBetrokkene() != null
                        && zgwRolOmschrijving.getHeeftAlsOverigBetrokkene().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zakLa01GeefZaakDetails.antwoord.zaak.heeftAlsOverigBetrokkene = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsOverigBetrokkene(),"ZAKBTROVR");
                }

            });

            ZgwZaakType zgwZaakType = this.getZaakTypeByUrl(zgwZaak.zaaktype);
            zakLa01GeefZaakDetails.antwoord.zaak.isVan = new Rol();
            zakLa01GeefZaakDetails.antwoord.zaak.isVan.entiteittype = "ZAKZKT";
            zakLa01GeefZaakDetails.antwoord.zaak.isVan.gerelateerde = new Gerelateerde();
            zakLa01GeefZaakDetails.antwoord.zaak.isVan.gerelateerde.entiteittype = "ZKT";
            zakLa01GeefZaakDetails.antwoord.zaak.isVan.gerelateerde.code = zgwZaakType.identificatie;
            zakLa01GeefZaakDetails.antwoord.zaak.isVan.gerelateerde.omschrijving = zgwZaakType.omschrijving;

            if(zgwZaak.getKenmerken() != null && !zgwZaak.getKenmerken().isEmpty()){
                zakLa01GeefZaakDetails.antwoord.zaak.kenmerk = new ArrayList<>();
                zgwZaak.getKenmerken().forEach(zgwKenmerk -> zakLa01GeefZaakDetails.antwoord.zaak.kenmerk.add(modelMapper.map(zgwKenmerk, Zaak.Kenmerk.class)));
            }

            zakLa01GeefZaakDetails.antwoord.zaak.opschorting = zgwZaak.getZgwOpschorting() != null? modelMapper.map(zgwZaak.getZgwOpschorting(), ZakLa01GeefZaakDetails.Antwoord.Object.Opschorting.class): null;
            zakLa01GeefZaakDetails.antwoord.zaak.verlenging = zgwZaak.getZgwVerlenging() != null? modelMapper.map(zgwZaak.getZgwVerlenging(), ZakLa01GeefZaakDetails.Antwoord.Object.Verlenging.class): null;

            zakLa01GeefZaakDetails.antwoord.zaak.heeft = zgwClient.getStatussenByZaakUrl(zgwZaak.url)
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

    private Rol getZdsRol(ZgwZaak zgwZaak, String rolOmschrijving, String entiteittype){
        ZgwRol zgwRol = zgwClient.getRolByZaakUrlAndOmschrijvingGeneriek(zgwZaak.url, rolOmschrijving);
        if (zgwRol == null) return null;
        return this.modelMapper.map(zgwRol, Rol.class).setEntiteittype(entiteittype);
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

    public void updateZaak(ZakLk01 ZakLk01) throws IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        var zdsWasZaak = ZakLk01.objects.get(0);
        var zdsWijzigingInZaak = ZakLk01.objects.get(1);
        ZgwZaak zgwZaak = zgwClient.getZaak(zdsWasZaak.identificatie);
        if(zgwZaak==null)throw new RuntimeException("Zaak with identification "+ zdsWasZaak.identificatie + " not found in ZGW");

        ChangeDetector changeDetector = new ChangeDetector();
        changeDetector.detect(zdsWasZaak, zdsWijzigingInZaak);

        if(changeDetector.getAllChangesByDeclaringClassAndFilter(Zaak.class, Rol.class).size()>0){
           ZgwZaak updatedZaak =  this.modelMapper.map(zdsWijzigingInZaak, ZgwZaak.class);
           updatedZaak.zaaktype = zgwZaak.zaaktype;
           updatedZaak.bronorganisatie = zgwZaak.bronorganisatie;
           updatedZaak.verantwoordelijkeOrganisatie = zgwZaak.verantwoordelijkeOrganisatie;
           zgwClient.updateZaak(zgwZaak.uuid, updatedZaak);
        }

        Map<ChangeDetector.Change, ChangeDetector.ChangeType> rolChanges = changeDetector.getAllChangesByFieldType(Rol.class);

        if(rolChanges.size()>0){
            changeDetector.filterChangesByType(rolChanges, ChangeDetector.ChangeType.NEW).forEach((change, changeType) -> {
                addRolToZgw((Rol) change.getValue(), getRolOmschrijvingGeneriekByRolName(change.getField().getName()), zgwZaak);
            });

            changeDetector.filterChangesByType(rolChanges, ChangeDetector.ChangeType.DELETED).forEach((change, changeType) -> {
                deleteRolFromZgw(getRolOmschrijvingGeneriekByRolName(change.getField().getName()), zgwZaak);
            });

            changeDetector.filterChangesByType(rolChanges, ChangeDetector.ChangeType.CHANGED).forEach((change, changeType) -> {
                updateRolInZgw(getRolOmschrijvingGeneriekByRolName(change.getField().getName()), zgwZaak, change.getValue());
            });

        }

    }

    private void updateRolInZgw(String omschrijvingGeneriek, ZgwZaak zgwZaak, Object value) {
        //no put action for rollen, so first delete then add
        log.debug("Attempting to update rol by deleting and adding as new");
        deleteRolFromZgw(omschrijvingGeneriek, zgwZaak);
        addRolToZgw((Rol) value, omschrijvingGeneriek, zgwZaak);
    }

    private void deleteRolFromZgw(String omschrijvingGeneriek, ZgwZaak zgwZaak) {
        ZgwRol zgwRol = zgwClient.getRolByZaakUrlAndOmschrijvingGeneriek(zgwZaak.url, omschrijvingGeneriek);
        if(zgwRol == null){
            log.warn("Attempted to delete rol " + zgwRol.roltoelichting + " from case "+ zgwZaak.getUrl()+ ", but rol hasn't been added to case.");
            return;
        }
        zgwClient.deleteRol(zgwRol.uuid);
    }

    public String getRolOmschrijvingGeneriekByRolName(String rolName){
        ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguratie().getZgwRolOmschrijving();

        switch(rolName.toLowerCase()){
            case "heeftalsbelanghebbende": return zgwRolOmschrijving.getHeeftAlsBelanghebbende();
            case "heeftalsinitiator": return zgwRolOmschrijving.getHeeftAlsInitiator();
            case "heeftalsuitvoerende": return zgwRolOmschrijving.getHeeftAlsUitvoerende();
            case "heeftalsverantwoordelijke": return zgwRolOmschrijving.getHeeftAlsVerantwoordelijke();
            case "heeftalsgemachtigde": return zgwRolOmschrijving.getHeeftAlsGemachtigde();
            case "heeftalsoverigBetrokkene": return zgwRolOmschrijving.getHeeftAlsOverigBetrokkene();
            default: return null;
        }
    }
}

