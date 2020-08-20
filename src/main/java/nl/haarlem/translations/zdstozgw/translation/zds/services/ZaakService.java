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
    
    // START: Zou moeten gebeuren in de ModelMapperConfig
	private String getDateStringFromZdsDate(String zdsDate) {
		if(zdsDate == null) return null;
		if(zdsDate.length() == 0) return null;
		var year = zdsDate.substring(0, 4);
		var month = zdsDate.substring(4, 6);
		var day = zdsDate.substring(6, 8);
		return year + "-" + month + "-" + day;
	}	

	private String getDateStringFromZgwDate(String zgwDate) {
		if(zgwDate ==null) return null;
		return zgwDate.replace("-", "");
	}	
	
	
	private String getArchiefNominatieFromZds(String archiefNominatie) {
		if(archiefNominatie == null) return null;
		if (archiefNominatie.toUpperCase().equals("J")) {
			return "vernietigen";
		} else {
			return "blijvend_bewaren";
		}
	}

	private String getArchiefNominatieFromZgw(String archiefNominatie) {
		if (archiefNominatie == null) return null;
		if(archiefNominatie.equals("vernietigen")) {
			return "J";
		} else {
			return "N";
		}
	}
    // STOP: Zou moeten gebeuren in de ModelMapperConfig	
    
    public ZgwZaak creeerZaak(ZdsZakLk01 zdsZakLk01CreeerZaak)   {
        var zdsZaak = zdsZakLk01CreeerZaak.objects.get(0);

        // START: Zou moeten gebeuren in de ModelMapperConfig
        zdsZaak.registratiedatum = getDateStringFromZdsDate(zdsZaak.registratiedatum);
        zdsZaak.startdatum = getDateStringFromZdsDate(zdsZaak.startdatum);
        zdsZaak.einddatumGepland = getDateStringFromZdsDate(zdsZaak.einddatumGepland);
        zdsZaak.archiefnominatie = getArchiefNominatieFromZds(zdsZaak.archiefnominatie);
        // STOP: Zou moeten gebeuren in de ModelMapperConfig        
                
        ZgwZaak zgwZaak = modelMapper.map(zdsZaak, ZgwZaak.class);
        
        var zaaktypecode = zdsZaak.isVan.gerelateerde.code;
        zgwZaak.zaaktype = zgwClient.getZgwZaakTypeByIdentificatie(zaaktypecode).url;
        zgwZaak.bronorganisatie = getRSIN(zdsZakLk01CreeerZaak.stuurgegevens.zender.organisatie);
        zgwZaak.verantwoordelijkeOrganisatie = getRSIN(zdsZakLk01CreeerZaak.stuurgegevens.ontvanger.organisatie);
        if (zdsZaak.getKenmerk() != null && !zdsZaak.getKenmerk().isEmpty()) {
            zgwZaak.kenmerk = new ArrayList<>();
            zdsZaak.getKenmerk().forEach(kenmerk -> {
                zgwZaak.kenmerk.add(modelMapper.map(kenmerk, ZgwKenmerk.class));
            });
        }

        var createdZaak = zgwClient.addZaak(zgwZaak);
        log.debug("Created a ZGW Zaak with UUID: " + createdZaak.getUuid());
        ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguratie().getZgwRolOmschrijving();
        addRolToZgw(zdsZaak.heeftAlsInitiator, zgwRolOmschrijving.getHeeftAlsInitiator(), createdZaak);
        addRolToZgw(zdsZaak.heeftAlsBelanghebbende, zgwRolOmschrijving.getHeeftAlsBelanghebbende(), createdZaak);
        addRolToZgw(zdsZaak.heeftAlsGemachtigde, zgwRolOmschrijving.getHeeftAlsGemachtigde(), createdZaak);
        addRolToZgw(zdsZaak.heeftAlsOverigBetrokkene, zgwRolOmschrijving.getHeeftAlsOverigBetrokkene(), createdZaak);
        addRolToZgw(zdsZaak.heeftAlsUitvoerende, zgwRolOmschrijving.getHeeftAlsUitvoerende(), createdZaak);
        addRolToZgw(zdsZaak.heeftAlsVerantwoordelijke, zgwRolOmschrijving.getHeeftAlsVerantwoordelijke(), createdZaak);
        return createdZaak;
    }

    private void addRolToZgw(ZdsRol zdsRol, String rolOmschrijvingGeneriek, ZgwZaak createdZaak)   {
        if (zdsRol == null) {
        	return;        	
        }
        if (zdsRol.gerelateerde == null) {
        	throw new ConverterException("Rol:" + rolOmschrijvingGeneriek + " zonder gerelateerde informatie");
        }        
        
        ZgwRol zgwRol = new ZgwRol();
        if (zdsRol.gerelateerde.medewerker != null) {
            zgwRol.betrokkeneIdentificatie = modelMapper.map(zdsRol.gerelateerde.medewerker, ZgwBetrokkeneIdentificatie.class);
            zgwRol.betrokkeneType = BetrokkeneType.MEDEWERKER.getDescription();
        } 
        if (zdsRol.gerelateerde.natuurlijkPersoon != null) {
        	if(zgwRol.betrokkeneIdentificatie == null) {
        		if(zgwRol.betrokkeneIdentificatie != null) {
        			throw new ConverterException("Rol: " + rolOmschrijvingGeneriek + " wordt al gebruikt voor medewerker");
        		}
        	}
            zgwRol.betrokkeneIdentificatie = modelMapper.map(zdsRol.gerelateerde.natuurlijkPersoon, ZgwBetrokkeneIdentificatie.class);
            zgwRol.betrokkeneType = BetrokkeneType.NATUURLIJK_PERSOON.getDescription();
        }         
        if(zgwRol.betrokkeneIdentificatie == null) {
        	throw new ConverterException("Rol: " + rolOmschrijvingGeneriek + " zonder Natuurlijkpersoon or Medewerker");
        }
        zgwRol.roltoelichting = rolOmschrijvingGeneriek;
        var roltype = zgwClient.getRolTypeByZaaktypeUrlAndOmschrijving(createdZaak.zaaktype, rolOmschrijvingGeneriek);
        if(roltype == null) {        	
        	var zaaktype = zgwClient.getZaakTypeByUrl(createdZaak.zaaktype);
        	throw new ConverterException("Rol: " + rolOmschrijvingGeneriek + " niet gevonden bij Zaaktype: " + zaaktype.identificatie);
        }
        zgwRol.roltype = roltype.url;
        zgwRol.zaak = createdZaak.getUrl();
        zgwClient.addZgwRol(zgwRol);
    }

    public ZdsZakLa01LijstZaakdocumenten geefLijstZaakdocumenten(ZdsZakLv01 zdsZakLv01)  {
        ZgwZaak zgwZaak = zgwClient.getZaak(zdsZakLv01.gelijk.identificatie);

        ZdsZakLa01LijstZaakdocumenten zdsZakLa01LijstZaakdocumenten = new ZdsZakLa01LijstZaakdocumenten();
        zdsZakLa01LijstZaakdocumenten.antwoord = new ZdsZakLa01LijstZaakdocumenten.Antwoord();
        zdsZakLa01LijstZaakdocumenten.antwoord.object = new ZdsZakLa01LijstZaakdocumenten.Antwoord.Object();
        zdsZakLa01LijstZaakdocumenten.antwoord.object.identificatie = zgwZaak.identificatie;
        zdsZakLa01LijstZaakdocumenten.antwoord.object.heeftRelevant = new ArrayList<>();

        zdsZakLa01LijstZaakdocumenten.stuurgegevens = new ZdsStuurgegevens(zdsZakLv01.stuurgegevens);
        zdsZakLa01LijstZaakdocumenten.stuurgegevens.berichtcode = "La01";

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

    public ZgwZaakInformatieObject voegZaakDocumentToe(ZdsEdcLk01 zdsEdcLk01)   {
    	var zdsInformatieObject = zdsEdcLk01.objects.get(0);
    	
        var zgwInformatieObjectType = zgwClient.getZgwInformatieObjectTypeByOmschrijving(zdsInformatieObject.omschrijving);
        if(zgwInformatieObjectType == null) {
        	throw new RuntimeException("Documenttype not found for omschrijving: "+ zdsInformatieObject.omschrijving);
        }
        
        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = modelMapper.map(zdsEdcLk01.objects.get(0), ZgwEnkelvoudigInformatieObject.class);
        zgwEnkelvoudigInformatieObject.informatieobjecttype = zgwInformatieObjectType.url;
        zgwEnkelvoudigInformatieObject.bronorganisatie = getRSIN(zdsEdcLk01.stuurgegevens.zender.organisatie);        

        zgwEnkelvoudigInformatieObject = zgwClient.addZaakDocument(zgwEnkelvoudigInformatieObject);
        String zaakUrl = zgwClient.getZaak(zdsEdcLk01.objects.get(0).isRelevantVoor.gerelateerde.identificatie).url;
        return addZaakInformatieObject(zgwEnkelvoudigInformatieObject, zaakUrl);
    }

    private ZgwZaakInformatieObject addZaakInformatieObject(ZgwEnkelvoudigInformatieObject doc, String zaakUrl)  {
        var zgwZaakInformatieObject = new ZgwZaakInformatieObject();
        zgwZaakInformatieObject.setZaak(zaakUrl);
        zgwZaakInformatieObject.setInformatieobject(doc.getUrl());
        zgwZaakInformatieObject.setTitel(doc.getTitel());
        return zgwClient.addDocumentToZaak(zgwZaakInformatieObject);
    }

    public ZdsEdcLa01 getZaakDocumentLezen(ZdsEdcLv01 zdsEdcLv01)  {
        //Get Enkelvoudig informatie object. This contains document meta data and a link to the document
    	var documentIdentificatie = zdsEdcLv01.gelijk.identificatie;
    	log.info("getZgwEnkelvoudigInformatieObject:" + documentIdentificatie);
        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = zgwClient.getZgwEnkelvoudigInformatieObject(documentIdentificatie);
        if(zgwEnkelvoudigInformatieObject == null) {
        	throw new ConverterException("ZgwEnkelvoudigInformatieObject #" + documentIdentificatie + " could not be found");
        }
        var inhoud = zgwClient.getBas64Inhoud(zgwEnkelvoudigInformatieObject.getInhoud());

        //Get zaakinformatieobject, this contains the link to the zaak
        var zgwZaakInformatieObject = zgwClient.getZgwZaakInformatieObject(zgwEnkelvoudigInformatieObject);
        //Get the zaak, to get the zaakidentificatie
        var zgwZaak = zgwClient.getZaakByUrl(zgwZaakInformatieObject.getZaak());

        var edcLa01 = new ZdsEdcLa01();
        edcLa01.stuurgegevens = new ZdsStuurgegevens(zdsEdcLv01.stuurgegevens);
        edcLa01.stuurgegevens.berichtcode = "La01";

        edcLa01.antwoord = new ZdsEdcLa01.Antwoord();
        edcLa01.antwoord.object = modelMapper.map(zgwEnkelvoudigInformatieObject, ZdsEdcLa01.Object.class);

        //Add Inhoud
        edcLa01.isRelevantVoor = new ZdsIsRelevantVoor();
        edcLa01.isRelevantVoor.gerelateerde = new ZdsGerelateerde();
        edcLa01.isRelevantVoor.entiteittype = "EDCZAK";
        edcLa01.isRelevantVoor.gerelateerde.entiteittype = "ZAK";
        edcLa01.isRelevantVoor.gerelateerde.identificatie = zgwZaak.getIdentificatie();
        edcLa01.antwoord.object.inhoud = inhoud;

        return edcLa01;
    }

    public ZgwZaak actualiseerZaakstatus(ZdsZakLk01ActualiseerZaakstatus zakLk01)   {
        ZdsZakLk01ActualiseerZaakstatus.Object object = zakLk01.objects.get(1);
        ZgwZaak zgwZaak = zgwClient.getZaak(object.identificatie);

        var zdsStatus = object.heeft.gerelateerde;
        ZgwStatus zgwStatus = modelMapper.map(object.heeft, ZgwStatus.class);        
        zgwStatus.zaak = zgwZaak.url;
        var zgwStatusType = zgwClient.getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype, zdsStatus.volgnummer, zdsStatus.omschrijving);        
        zgwStatus.statustype = zgwStatusType.url;

        zgwClient.actualiseerZaakStatus(zgwStatus);
        return zgwZaak;
    }

    public ZdsZakLa01GeefZaakDetails getZaakDetails(ZdsZakLv01 zdsZakLv01) {
        ZdsZakLa01GeefZaakDetails zdsZakLa01GeefZaakDetails = new ZdsZakLa01GeefZaakDetails();

        if (zdsZakLv01.gelijk != null && zdsZakLv01.gelijk.identificatie != null) {
        	var zaakidentificatie = zdsZakLv01.gelijk.identificatie;
            var zgwZaak = zgwClient.getZaak(zaakidentificatie);
            if (zgwZaak == null) {
                throw new ConverterException("Zaak not found for identification: '" + zdsZakLv01.gelijk.identificatie + "'");
            }
            zdsZakLa01GeefZaakDetails.stuurgegevens = new ZdsStuurgegevens(zdsZakLv01.stuurgegevens);
            zdsZakLa01GeefZaakDetails.stuurgegevens.berichtcode = "La01";

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
            zdsZakLa01GeefZaakDetails.antwoord.zaak.isVan.gerelateerde = new ZdsGerelateerde();
            zdsZakLa01GeefZaakDetails.antwoord.zaak.isVan.gerelateerde.entiteittype = "ZKT";
            zdsZakLa01GeefZaakDetails.antwoord.zaak.isVan.gerelateerde.code = zgwZaakType.identificatie;
            zdsZakLa01GeefZaakDetails.antwoord.zaak.isVan.gerelateerde.omschrijving = zgwZaakType.omschrijving;

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
            return zdsZakLa01GeefZaakDetails;
        }
        else if (zdsZakLv01.gelijk != null && zdsZakLv01.gelijk.heeftAlsInitiator != null && zdsZakLv01.gelijk.heeftAlsInitiator.gerelateerde != null && zdsZakLv01.gelijk.heeftAlsInitiator.gerelateerde.identificatie != null) {
        	var gerelateerdeidentificatie = zdsZakLv01.gelijk.heeftAlsInitiator.gerelateerde.identificatie;
        	if(!gerelateerdeidentificatie.startsWith("11")) {
        		throw new ConverterException("gerelateerdeidentificatie: '" + gerelateerdeidentificatie + "' moet beginnen met '11' gevolgd door het bsnnummer");
        	}
        	var bsn = gerelateerdeidentificatie.substring(2);
        	var zgwRollen = zgwClient.getRollenByBsn(bsn);
        	var zdsZaken = new ArrayList<ZdsZaak>();
        	for(ZgwRol rol : zgwRollen) {
        		var zgwRolType = zgwClient.getRolTypeByUrl(rol.roltype);
        		ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguratie().getZgwRolOmschrijving();
        		if(zgwRolType.omschrijving.equals(zgwRolOmschrijving.getHeeftAlsInitiator())) {
        			// hier moeten we dus een zaak toevoegen aan zdsZaken
        			var zgwZaak = zgwClient.getZaakByUrl(rol.zaak);
        			log.info("TODO: toevoegen van zaak met zaakidentificatie:" + zgwZaak.identificatie);
        		}
        	}        	
        	throw new ConverterException("todo: ophalen bij bsnnummer:" + bsn);
        }
        else throw new ConverterException("Niet ondersteunde vraag binnengekregen");
    }

    private ZgwZaakType getZaakTypeByUrl(String url) {
        return zgwClient.getZaakTypes(null)
                .stream()
                .filter(zgwZaakType -> zgwZaakType.url.equalsIgnoreCase(url))
                .findFirst()
                .orElse(null);
    }

    private ZdsRol getZdsRol(ZgwZaak zgwZaak, String rolOmschrijving, String entiteittype) {
    	var zgwRolType = zgwClient.getRolTypeByZaaktypeUrlAndOmschrijving(zgwZaak.zaaktype, rolOmschrijving);
        ZgwRol zgwRol = zgwClient.getRolByZaakUrlAndRolTypeUrl(zgwZaak.url, zgwRolType.url);
        if (zgwRol == null) {
        	// geen rol voor deze
        	return null;
        }
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

    public void updateZaak(ZdsZakLk01 ZdsZakLk01) {
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

    private void updateRolInZgw(String omschrijvingGeneriek, ZgwZaak zgwZaak, Object value)   {
        //no put action for rollen, so first delete then add
        log.debug("Attempting to update rol by deleting and adding as new");
        deleteRolFromZgw(omschrijvingGeneriek, zgwZaak);
        addRolToZgw((ZdsRol) value, omschrijvingGeneriek, zgwZaak);
    }

    private void deleteRolFromZgw(String rolOmschrijving, ZgwZaak zgwZaak) {
    	//TODO
    	throw new ConverterException("niet omschrijvingGeneriek gebruiken, hier kijken naar rol met omschrijving = " + rolOmschrijving + " en mogelijk ook nog welk type object");
//        ZgwRol zgwRol = zgwClient.getRolByZaakUrlAndOmschrijvingGeneriek(zgwZaak.url, omschrijvingGeneriek);
//        if (zgwRol == null) {
//            log.warn("Attempted to delete rol " + zgwRol.roltoelichting + " from case " + zgwZaak.getUrl() + ", but rol hasn't been added to case.");
//            return;
//        }
//        zgwClient.deleteRol(zgwRol.uuid);
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

