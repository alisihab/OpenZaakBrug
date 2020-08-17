package nl.haarlem.translations.zdstozgw.translation.zds.services;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.Organisatie;
import nl.haarlem.translations.zdstozgw.config.model.ZgwRolOmschrijving;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
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

import java.io.IOException;
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

    public ZgwZaak creeerZaak(ZdsZakLk01 zdsZakLk01CreeerZaak) {
        var zaak = zdsZakLk01CreeerZaak.objects.get(0);

        ZgwZaak zgwZaak = modelMapper.map(zaak, ZgwZaak.class);
        var zaaktypecode = zaak.isVan.zdsGerelateerde.code;
        zgwZaak.zaaktype = zgwClient.getZgwZaakTypeByIdentificatie(zaaktypecode).url;
        zgwZaak.bronorganisatie = getRSIN(zdsZakLk01CreeerZaak.zdsStuurgegevens.zdsZender.organisatie);
        zgwZaak.verantwoordelijkeOrganisatie = getRSIN(zdsZakLk01CreeerZaak.zdsStuurgegevens.zdsOntvanger.organisatie);
        if (zaak.getKenmerk() != null && !zaak.getKenmerk().isEmpty()) {
            zgwZaak.kenmerk = new ArrayList<>();
            zaak.getKenmerk().forEach(kenmerk -> {
                zgwZaak.kenmerk.add(modelMapper.map(kenmerk, ZgwKenmerk.class));
            });
        }

        var createdZaak = zgwClient.addZaak(zgwZaak);
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

    private void addRolToZgw(ZdsRol zdsRol, String rolOmschrijvingGeneriek, ZgwZaak createdZaak) {
        if (zdsRol == null) return;
        ZgwRol zgwRol = new ZgwRol();
        if (zdsRol.zdsGerelateerde.zdsMedewerker != null) {
            zgwRol.betrokkeneIdentificatie = modelMapper.map(zdsRol.zdsGerelateerde.zdsMedewerker, ZgwBetrokkeneIdentificatie.class);
            zgwRol.betrokkeneType = BetrokkeneType.MEDEWERKER.getDescription();
        } else if (zdsRol.zdsGerelateerde.zdsNatuurlijkPersoon != null) {
            zgwRol.betrokkeneIdentificatie = modelMapper.map(zdsRol.zdsGerelateerde.zdsNatuurlijkPersoon, ZgwBetrokkeneIdentificatie.class);
            zgwRol.betrokkeneType = BetrokkeneType.NATUURLIJK_PERSOON.getDescription();
        } else {
            throw new RuntimeException("Natuurlijkpersoon or medewerker missing for adding roltype to case");
        }
        zgwRol.roltoelichting = rolOmschrijvingGeneriek;
        zgwRol.roltype = zgwClient.getRoltypeByZaakTypeUrlAndOmschrijvingGeneriek(createdZaak.zaaktype, rolOmschrijvingGeneriek).url;
        zgwRol.zaak = createdZaak.getUrl();
        zgwClient.addZgwRol(zgwRol);
    }

    public ZdsZakLa01LijstZaakdocumenten geefLijstZaakdocumenten(ZdsZakLv01 zdsZakLv01)  {
        ZgwZaak zgwZaak = zgwClient.getZaak(zdsZakLv01.zdsGelijk.identificatie);

        ZdsZakLa01LijstZaakdocumenten zdsZakLa01LijstZaakdocumenten = new ZdsZakLa01LijstZaakdocumenten();
        zdsZakLa01LijstZaakdocumenten.antwoord = new ZdsZakLa01LijstZaakdocumenten.Antwoord();
        zdsZakLa01LijstZaakdocumenten.antwoord.object = new ZdsZakLa01LijstZaakdocumenten.Antwoord.Object();
        zdsZakLa01LijstZaakdocumenten.antwoord.object.identificatie = zgwZaak.identificatie;
        zdsZakLa01LijstZaakdocumenten.antwoord.object.heeftRelevant = new ArrayList<>();

        zdsZakLa01LijstZaakdocumenten.zdsStuurgegevens = new ZdsStuurgegevens(zdsZakLv01.zdsStuurgegevens);
        zdsZakLa01LijstZaakdocumenten.zdsStuurgegevens.berichtcode = "La01";

        zgwClient.getZaakInformatieObjectenByZaak(zgwZaak.url).forEach(zgwZaakInformatieObject -> {
            ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = zgwClient.getZaakDocument(zgwZaakInformatieObject.informatieobject);
            ZdsZaakDocument zdsZaakDocument = null;
            if (zgwEnkelvoudigInformatieObject != null) {
                zdsZaakDocument = modelMapper.map(zgwEnkelvoudigInformatieObject, ZdsZaakDocument.class);
            }
            zdsZakLa01LijstZaakdocumenten.antwoord.object.heeftRelevant.add(modelMapper.map(zgwZaakInformatieObject, ZdsZakLa01LijstZaakdocumenten.Antwoord.Object.HeeftRelevant.class)
                    .setGerelateerde(zdsZaakDocument));
        });

        return zdsZakLa01LijstZaakdocumenten;
    }

    public ZgwZaakInformatieObject voegZaakDocumentToe(ZdsEdcLk01 zdsEdcLk01)  throws ConverterException {
        var informatieObjectType = zgwClient.getZgwInformatieObjectTypeByOmschrijving(zdsEdcLk01.objects.get(0).omschrijving);
        if(informatieObjectType == null) throw new RuntimeException("Documenttype not found for omschrijving: "+ zdsEdcLk01.objects.get(0).omschrijving);
        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = modelMapper.map(zdsEdcLk01.objects.get(0), ZgwEnkelvoudigInformatieObject.class);
        zgwEnkelvoudigInformatieObject.informatieobjecttype = informatieObjectType.url;
        zgwEnkelvoudigInformatieObject.bronorganisatie = getRSIN(zdsEdcLk01.zdsStuurgegevens.zdsZender.organisatie);

        zgwEnkelvoudigInformatieObject = zgwClient.addZaakDocument(zgwEnkelvoudigInformatieObject);
        String zaakUrl = zgwClient.getZaak(zdsEdcLk01.objects.get(0).isRelevantVoor.zdsGerelateerde.identificatie).url;
        return addZaakInformatieObject(zgwEnkelvoudigInformatieObject, zaakUrl);
    }

    private ZgwZaakInformatieObject addZaakInformatieObject(ZgwEnkelvoudigInformatieObject doc, String zaakUrl) throws ConverterException {
        var zgwZaakInformatieObject = new ZgwZaakInformatieObject();
        zgwZaakInformatieObject.setZaak(zaakUrl);
        zgwZaakInformatieObject.setInformatieobject(doc.getUrl());
        zgwZaakInformatieObject.setTitel(doc.getTitel());
        return zgwClient.addDocumentToZaak(zgwZaakInformatieObject);
    }

    public ZdsEdcLa01 getZaakDocumentLezen(ZdsEdcLv01 zdsEdcLv01) throws ConverterException {
        //Get Enkelvoudig informatie object. This contains document meta data and a link to the document
        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = zgwClient.getZgwEnkelvoudigInformatieObject(zdsEdcLv01.zdsGelijk.identificatie);
        var inhoud = zgwClient.getBas64Inhoud(zgwEnkelvoudigInformatieObject.getInhoud());

        //Get zaakinformatieobject, this contains the link to the zaak
        var zgwZaakInformatieObject = zgwClient.getZgwZaakInformatieObject(zgwEnkelvoudigInformatieObject);
        //Get the zaak, to get the zaakidentificatie
        var zgwZaak = zgwClient.getZaakByUrl(zgwZaakInformatieObject.getZaak());

        var edcLa01 = new ZdsEdcLa01();
        edcLa01.zdsStuurgegevens = new ZdsStuurgegevens(zdsEdcLv01.zdsStuurgegevens);
        edcLa01.zdsStuurgegevens.berichtcode = "La01";

        edcLa01.antwoord = new ZdsEdcLa01.Antwoord();
        edcLa01.antwoord.object = modelMapper.map(zgwEnkelvoudigInformatieObject, ZdsEdcLa01.Object.class);

        //Add Inhoud
        edcLa01.zdsIsRelevantVoor = new ZdsIsRelevantVoor();
        edcLa01.zdsIsRelevantVoor.zdsGerelateerde = new ZdsGerelateerde();
        edcLa01.zdsIsRelevantVoor.entiteittype = "EDCZAK";
        edcLa01.zdsIsRelevantVoor.zdsGerelateerde.entiteittype = "ZAK";
        edcLa01.zdsIsRelevantVoor.zdsGerelateerde.identificatie = zgwZaak.getIdentificatie();
        edcLa01.antwoord.object.inhoud = inhoud;

        return edcLa01;
    }

    public ZgwZaak actualiseerZaakstatus(ZdsZakLk01ActualiseerZaakstatus zakLk01) {
        ZdsZakLk01ActualiseerZaakstatus.Object object = zakLk01.objects.get(1);
        ZgwZaak zgwZaak = zgwClient.getZaak(object.identificatie);

        ZgwStatus zgwStatus = modelMapper.map(object.zdsHeeft, ZgwStatus.class);
        zgwStatus.zaak = zgwZaak.url;
        zgwStatus.statustype = zgwClient.getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype, Integer.valueOf(object.zdsHeeft.zdsGerelateerde.volgnummer)).url;

        zgwClient.actualiseerZaakStatus(zgwStatus);
        return zgwZaak;
    }

    public ZdsZakLa01GeefZaakDetails getZaakDetails(ZdsZakLv01 zdsZakLv01) {
        ZdsZakLa01GeefZaakDetails zdsZakLa01GeefZaakDetails = new ZdsZakLa01GeefZaakDetails();

        if (zdsZakLv01.zdsGelijk != null && zdsZakLv01.zdsGelijk.identificatie != null) {
            var zgwZaak = zgwClient.getZaak(zdsZakLv01.zdsGelijk.identificatie);
            if (zgwZaak == null)
                throw new RuntimeException("Zaak not found for identification: '" + zdsZakLv01.zdsGelijk.identificatie + "'");

            zdsZakLa01GeefZaakDetails.zdsStuurgegevens = new ZdsStuurgegevens(zdsZakLv01.zdsStuurgegevens);
            zdsZakLa01GeefZaakDetails.zdsStuurgegevens.berichtcode = "La01";

            zdsZakLa01GeefZaakDetails.antwoord = new ZdsZakLa01GeefZaakDetails.Antwoord();
            zdsZakLa01GeefZaakDetails.antwoord.zaak = new ZdsZakLa01GeefZaakDetails.Antwoord.Object();
            zdsZakLa01GeefZaakDetails.antwoord.zaak = modelMapper.map(zgwZaak, ZdsZakLa01GeefZaakDetails.Antwoord.Object.class);

            ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguratie().getZgwRolOmschrijving();

            zgwClient.getRollenByZaakUrl(zgwZaak.url).forEach(zgwRol -> {

                if (zgwRolOmschrijving.getHeeftAlsInitiator() != null
                        && zgwRolOmschrijving.getHeeftAlsInitiator().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zdsZakLa01GeefZaakDetails.antwoord.zaak.heeftAlsInitiator = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsInitiator(), "ZAKBTRINI");
                } else if (zgwRolOmschrijving.getHeeftAlsBelanghebbende() != null
                        && zgwRolOmschrijving.getHeeftAlsBelanghebbende().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zdsZakLa01GeefZaakDetails.antwoord.zaak.heeftAlsBelanghebbende = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsBelanghebbende(), "ZAKBTRBLH");
                } else if (zgwRolOmschrijving.getHeeftAlsUitvoerende() != null
                        && zgwRolOmschrijving.getHeeftAlsUitvoerende().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zdsZakLa01GeefZaakDetails.antwoord.zaak.heeftAlsUitvoerende = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsUitvoerende(), "ZAKBTRUTV");
                } else if (zgwRolOmschrijving.getHeeftAlsVerantwoordelijke() != null
                        && zgwRolOmschrijving.getHeeftAlsVerantwoordelijke().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zdsZakLa01GeefZaakDetails.antwoord.zaak.heeftAlsVerantwoordelijke = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsVerantwoordelijke(), "ZAKBTRVRA");
                } else if (zgwRolOmschrijving.getHeeftAlsGemachtigde() != null
                        && zgwRolOmschrijving.getHeeftAlsGemachtigde().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zdsZakLa01GeefZaakDetails.antwoord.zaak.heeftAlsGemachtigde = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsGemachtigde(), "ZAKBTRGMC");
                } else if (zgwRolOmschrijving.getHeeftAlsOverigBetrokkene() != null
                        && zgwRolOmschrijving.getHeeftAlsOverigBetrokkene().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zdsZakLa01GeefZaakDetails.antwoord.zaak.heeftAlsOverigBetrokkene = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsOverigBetrokkene(), "ZAKBTROVR");
                }

            });

            ZgwZaakType zgwZaakType = this.getZaakTypeByUrl(zgwZaak.zaaktype);
            zdsZakLa01GeefZaakDetails.antwoord.zaak.isVan = new ZdsRol();
            zdsZakLa01GeefZaakDetails.antwoord.zaak.isVan.entiteittype = "ZAKZKT";
            zdsZakLa01GeefZaakDetails.antwoord.zaak.isVan.zdsGerelateerde = new ZdsGerelateerde();
            zdsZakLa01GeefZaakDetails.antwoord.zaak.isVan.zdsGerelateerde.entiteittype = "ZKT";
            zdsZakLa01GeefZaakDetails.antwoord.zaak.isVan.zdsGerelateerde.code = zgwZaakType.identificatie;
            zdsZakLa01GeefZaakDetails.antwoord.zaak.isVan.zdsGerelateerde.omschrijving = zgwZaakType.omschrijving;

            if (zgwZaak.getKenmerk() != null && !zgwZaak.getKenmerk().isEmpty()) {
                zdsZakLa01GeefZaakDetails.antwoord.zaak.kenmerk = new ArrayList<>();
                zgwZaak.getKenmerk().forEach(zgwKenmerk -> zdsZakLa01GeefZaakDetails.antwoord.zaak.kenmerk.add(modelMapper.map(zgwKenmerk, ZdsZaak.Kenmerk.class)));
            }

            zdsZakLa01GeefZaakDetails.antwoord.zaak.opschorting = zgwZaak.getOpschorting() != null ? modelMapper.map(zgwZaak.getOpschorting(), ZdsZakLa01GeefZaakDetails.Antwoord.Object.Opschorting.class) : null;
            zdsZakLa01GeefZaakDetails.antwoord.zaak.verlenging = zgwZaak.getVerlenging() != null ? modelMapper.map(zgwZaak.getVerlenging(), ZdsZakLa01GeefZaakDetails.Antwoord.Object.Verlenging.class) : null;

            zdsZakLa01GeefZaakDetails.antwoord.zaak.heeft = zgwClient.getStatussenByZaakUrl(zgwZaak.url)
                    .stream()
                    .map(zgwStatus -> {
                        ZgwStatusType zgwStatusType = zgwClient.getResource(zgwStatus.statustype, ZgwStatusType.class);
                        return modelMapper.map(zgwStatus, ZdsZakLa01GeefZaakDetails.Status.class)
                                .setEntiteittype("ZAKSTT")
                                .setIndicatieLaatsteStatus(Boolean.valueOf(zgwStatusType.isEindstatus) ? "J" : "N");
                    })
                    .collect(Collectors.toList());

        }

        return zdsZakLa01GeefZaakDetails;
    }

    private ZgwZaakType getZaakTypeByUrl(String url) {
        return zgwClient.getZaakTypes(null)
                .stream()
                .filter(zgwZaakType -> zgwZaakType.url.equalsIgnoreCase(url))
                .findFirst()
                .orElse(null);
    }

    private ZdsRol getZdsRol(ZgwZaak zgwZaak, String rolOmschrijving, String entiteittype) {
        ZgwRol zgwRol = zgwClient.getRolByZaakUrlAndOmschrijvingGeneriek(zgwZaak.url, rolOmschrijving);
        if (zgwRol == null) return null;
        return this.modelMapper.map(zgwRol, ZdsRol.class).setEntiteittype(entiteittype);
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

    public void updateZaak(ZdsZakLk01 ZdsZakLk01) throws ConverterException {
        var zdsWasZaak = ZdsZakLk01.objects.get(0);
        var zdsWijzigingInZaak = ZdsZakLk01.objects.get(1);
        ZgwZaak zgwZaak = zgwClient.getZaak(zdsWasZaak.identificatie);
        if (zgwZaak == null)
            throw new RuntimeException("Zaak with identification " + zdsWasZaak.identificatie + " not found in ZGW");

        ChangeDetector changeDetector = new ChangeDetector();
        changeDetector.detect(zdsWasZaak, zdsWijzigingInZaak);

        if (changeDetector.getAllChangesByDeclaringClassAndFilter(ZdsZaak.class, ZdsRol.class).size() > 0) {
            ZgwZaakPut updatedZaak = this.modelMapper.map(zdsWijzigingInZaak, ZgwZaakPut.class);
            updatedZaak.zaaktype = zgwZaak.zaaktype;
            updatedZaak.bronorganisatie = zgwZaak.bronorganisatie;
            updatedZaak.verantwoordelijkeOrganisatie = zgwZaak.verantwoordelijkeOrganisatie;
            zgwClient.updateZaak(zgwZaak.uuid, updatedZaak);
        }

        Map<ChangeDetector.Change, ChangeDetector.ChangeType> rolChanges = changeDetector.getAllChangesByFieldType(ZdsRol.class);

        if (rolChanges.size() > 0) {
            changeDetector.filterChangesByType(rolChanges, ChangeDetector.ChangeType.NEW).forEach((change, changeType) -> {
                addRolToZgw((ZdsRol) change.getValue(), getRolOmschrijvingGeneriekByRolName(change.getField().getName()), zgwZaak);
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
        addRolToZgw((ZdsRol) value, omschrijvingGeneriek, zgwZaak);
    }

    private void deleteRolFromZgw(String omschrijvingGeneriek, ZgwZaak zgwZaak) {
        ZgwRol zgwRol = zgwClient.getRolByZaakUrlAndOmschrijvingGeneriek(zgwZaak.url, omschrijvingGeneriek);
        if (zgwRol == null) {
            log.warn("Attempted to delete rol " + zgwRol.roltoelichting + " from case " + zgwZaak.getUrl() + ", but rol hasn't been added to case.");
            return;
        }
        zgwClient.deleteRol(zgwRol.uuid);
    }

    public String getRolOmschrijvingGeneriekByRolName(String rolName) {
        ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguratie().getZgwRolOmschrijving();

        switch (rolName.toLowerCase()) {
            case "heeftalsbelanghebbende":
                return zgwRolOmschrijving.getHeeftAlsBelanghebbende();
            case "heeftalsinitiator":
                return zgwRolOmschrijving.getHeeftAlsInitiator();
            case "heeftalsuitvoerende":
                return zgwRolOmschrijving.getHeeftAlsUitvoerende();
            case "heeftalsverantwoordelijke":
                return zgwRolOmschrijving.getHeeftAlsVerantwoordelijke();
            case "heeftalsgemachtigde":
                return zgwRolOmschrijving.getHeeftAlsGemachtigde();
            case "heeftalsoverigBetrokkene":
                return zgwRolOmschrijving.getHeeftAlsOverigBetrokkene();
            default:
                return null;
        }
    }
}

