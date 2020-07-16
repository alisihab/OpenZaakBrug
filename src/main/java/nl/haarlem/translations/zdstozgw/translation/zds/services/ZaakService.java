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
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
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

    public ZgwZaak creeerZaak(ZakLk01 zakLk01) throws Exception {
        var zaak = zakLk01.objects.get(0);
        ZgwZaak zgwZaak = modelMapper.map(zaak, ZgwZaak.class);
        zgwZaak.zaaktype = getZaakTypeByZDSCode(zaak.isVan.gerelateerde.code).zaakType;
        zgwZaak.bronorganisatie = getRSIN(zakLk01.stuurgegevens.zender.organisatie);
        zgwZaak.verantwoordelijkeOrganisatie = getRSIN(zakLk01.stuurgegevens.ontvanger.organisatie);

        try {
            var createdZaak = zgwClient.addZaak(zgwZaak);
            if (createdZaak.getUrl() != null) {
                log.info("Created a ZGW Zaak with UUID: " + createdZaak.getUuid());
                if(zaak.heeftAlsInitiator != null){
                    ZgwRol zgwRol = new ZgwRol();
                    zgwRol.betrokkeneIdentificatie = modelMapper.map(zaak.heeftAlsInitiator.gerelateerde.natuurlijkPersoon, ZgwBetrokkeneIdentificatie.class);
                    zgwRol.betrokkeneType = BetrokkeneType.NATUURLIJK_PERSOON.getDescription();
                    zgwRol.roltoelichting = this.configService.getConfiguratie().getZgwRolOmschrijving().getHeeftAlsInitiator();
                    zgwRol.roltype = getZaakTypeByZDSCode(zaak.isVan.gerelateerde.code).initiatorRolTypeUrl;
                    zgwRol.zaak = createdZaak.getUrl();
                    zgwClient.addZgwRol(zgwRol);
                }
                return createdZaak;
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    public ZakLa01LijstZaakdocumenten geefLijstZaakdocumenten(ZakLv01 zakLv01) throws Exception {
        ZgwZaak zgwZaak = getZaak(zakLv01.gelijk.identificatie);

        ZakLa01LijstZaakdocumenten zakLa01LijstZaakdocumenten = new ZakLa01LijstZaakdocumenten();
        zakLa01LijstZaakdocumenten.antwoord = new ZakLa01LijstZaakdocumenten.Antwoord();
        zakLa01LijstZaakdocumenten.antwoord.object = new ZakLa01LijstZaakdocumenten.Antwoord.Object();
        zakLa01LijstZaakdocumenten.antwoord.object.identificatie = zgwZaak.identificatie;
        zakLa01LijstZaakdocumenten.antwoord.object.heeftRelevant = new ArrayList<>();

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

    public ZgwZaak actualiseerZaakstatus(ZakLk01 zakLk01) {
        ZakLk01.Object object = zakLk01.objects.get(1);
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
            zakLa01GeefZaakDetails.antwoord.zaak = modelMapper.map(zgwZaak, ZakLa01GeefZaakDetails.Antwoord.Zaak.class);

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

            zakLa01GeefZaakDetails.antwoord.zaak.kenmerk = zgwZaak.getKenmerken() != null && !zgwZaak.getKenmerken().isEmpty()?  modelMapper.map(zgwZaak.getKenmerken().get(0), ZakLa01GeefZaakDetails.Antwoord.Zaak.Kenmerk.class) : null;
            zakLa01GeefZaakDetails.antwoord.zaak.opschorting = zgwZaak.getOpschorting() != null? modelMapper.map(zgwZaak.getOpschorting(), ZakLa01GeefZaakDetails.Antwoord.Zaak.Opschorting.class): null;
            zakLa01GeefZaakDetails.antwoord.zaak.verlenging = zgwZaak.getVerlenging() != null? modelMapper.map(zgwZaak.getVerlenging(), ZakLa01GeefZaakDetails.Antwoord.Zaak.Verlenging.class): null;

            zakLa01GeefZaakDetails.antwoord.zaak.heeft = getStatussenByZaakUrl(zgwZaak.url)
                    .stream()
                    .map(zgwStatus -> {
                        ZgwStatusType zgwStatusType = zgwClient.getResource(zgwStatus.statustype, ZgwStatusType.class);
                        return modelMapper.map(zgwStatus, ZakLa01GeefZaakDetails.Antwoord.Zaak.Status.class)
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

    private ZgwRol getRolByZaakUrlAndOmschrijvingGeneriek(String zaakUrl, String omschrijvingGeneriek) {
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

}

