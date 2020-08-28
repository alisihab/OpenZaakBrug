package nl.haarlem.translations.zdstozgw.translation.zds.services;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.Organisatie;
import nl.haarlem.translations.zdstozgw.config.model.ZgwRolOmschrijving;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.BetrokkeneType;
import nl.haarlem.translations.zdstozgw.translation.zds.model.*;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLa01LijstZaakdocumenten.Antwoord.Object.HeeftRelevant;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.*;
import nl.haarlem.translations.zdstozgw.utils.ChangeDetector;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ZaakService {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public final ZGWClient zgwClient;
    private final ModelMapper modelMapper;
    private final ConfigService configService;


    @Autowired
    public ZaakService(ZGWClient zgwClient, ModelMapper modelMapper, ConfigService configService) {
        this.zgwClient = zgwClient;
        this.modelMapper = modelMapper;
        this.configService = configService;
    }
    


    public String getRSIN(String gemeenteCode) {
        List<Organisatie> organisaties = configService.getConfiguratie().getOrganisaties();
        for (Organisatie organisatie : organisaties) {
            if (organisatie.getGemeenteCode().equals(gemeenteCode)) {
                return organisatie.getRSIN();
            }
        }
        return "";
    }       
    
//    public ZgwZaak creeerZaak(ZdsZakLk01 zdsZakLk01CreeerZaak)   {
//        ZdsZaak zdsZaak = zdsZakLk01CreeerZaak.objects.get(0);
    public ZgwZaak creeerZaak(String rsin, ZdsZaak zdsZaak)   {    
        ZgwZaak zgwZaak = modelMapper.map(zdsZaak, ZgwZaak.class);
        
        var zaaktypecode = zdsZaak.isVan.gerelateerde.code;
        var zaaktype = zgwClient.getZgwZaakTypeByIdentificatie(zaaktypecode);
        if(zaaktype == null) {
        	throw new ConverterException("Zaaktype met code:" + zaaktypecode + " could not be found");
        }                		
        zgwZaak.zaaktype = zaaktype.url;
        zgwZaak.bronorganisatie = rsin;
        zgwZaak.verantwoordelijkeOrganisatie = rsin;
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

    private void addRolToZgw(ZdsRol zdsRol, String typeRolOmschrijving, ZgwZaak createdZaak)   {
        if (zdsRol == null) {
        	return;        	
        }
        if (zdsRol.gerelateerde == null) {
        	throw new ConverterException("Rol:" + typeRolOmschrijving + " zonder gerelateerde informatie");
        }        
        
        ZgwRol zgwRol = new ZgwRol();
        if (zdsRol.gerelateerde.medewerker != null) {
            zgwRol.betrokkeneIdentificatie = modelMapper.map(zdsRol.gerelateerde.medewerker, ZgwBetrokkeneIdentificatie.class);
            zgwRol.betrokkeneType = BetrokkeneType.MEDEWERKER.getDescription();
        } 
        if (zdsRol.gerelateerde.natuurlijkPersoon != null) {
        	if(zgwRol.betrokkeneIdentificatie == null) {
        		if(zgwRol.betrokkeneIdentificatie != null) {
        			throw new ConverterException("Rol: " + typeRolOmschrijving + " wordt al gebruikt voor medewerker");
        		}
        	}
            zgwRol.betrokkeneIdentificatie = modelMapper.map(zdsRol.gerelateerde.natuurlijkPersoon, ZgwBetrokkeneIdentificatie.class);
            zgwRol.betrokkeneType = BetrokkeneType.NATUURLIJK_PERSOON.getDescription();
        }         
        if(zgwRol.betrokkeneIdentificatie == null) {
        	throw new ConverterException("Rol: " + typeRolOmschrijving + " zonder Natuurlijkpersoon or Medewerker");
        }
        zgwRol.roltoelichting = typeRolOmschrijving;
        var roltype = zgwClient.getRolTypeByZaaktypeUrlAndOmschrijving(createdZaak.zaaktype, typeRolOmschrijving);
        if(roltype == null) {        	
        	var zaaktype = zgwClient.getZaakTypeByUrl(createdZaak.zaaktype);
        	throw new ConverterException("Rol: " + typeRolOmschrijving + " niet gevonden bij Zaaktype: " + zaaktype.identificatie);
        }
        zgwRol.roltype = roltype.url;
        zgwRol.zaak = createdZaak.getUrl();
        zgwClient.addZgwRol(zgwRol);
    }

  public List<HeeftRelevant> geefLijstZaakdocumenten(String zaakidentificatie)  {
	  	ZgwZaak zgwZaak = zgwClient.getZaakByIdentificatie(zaakidentificatie);

	  	var relevanteDocumenten = new ArrayList<HeeftRelevant>();
        zgwClient.getZaakInformatieObjectenByZaak(zgwZaak.url).forEach(zgwZaakInformatieObject -> {
            ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = zgwClient.getZaakDocument(zgwZaakInformatieObject.informatieobject);
            ZdsZaakDocument zdsZaakDocument = null;
            if (zgwEnkelvoudigInformatieObject != null) {
                zdsZaakDocument = modelMapper.map(zgwEnkelvoudigInformatieObject, ZdsZaakDocument.class);
            }
            relevanteDocumenten.add(modelMapper.map(zgwZaakInformatieObject, ZdsZakLa01LijstZaakdocumenten.Antwoord.Object.HeeftRelevant.class)
                    .setGerelateerde(zdsZaakDocument));
        });

        return relevanteDocumenten;
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
        String zaakUrl = zgwClient.getZaakByIdentificatie(zdsEdcLk01.objects.get(0).isRelevantVoor.gerelateerde.identificatie).url;
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
        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = zgwClient.getZgwEnkelvoudigInformatieObjectByIdentiticatie(documentIdentificatie);
        if(zgwEnkelvoudigInformatieObject == null) {
        	throw new ConverterException("ZgwEnkelvoudigInformatieObject #" + documentIdentificatie + " could not be found");
        }
        var inhoud = zgwClient.getBas64Inhoud(zgwEnkelvoudigInformatieObject.getInhoud());

        //Get zaakinformatieobject, this contains the link to the zaak
        var zgwZaakInformatieObject = zgwClient.getZgwZaakInformatieObject(zgwEnkelvoudigInformatieObject);
        //Get the zaak, to get the zaakidentificatie
        var zgwZaak = zgwClient.getZaakByUrl(zgwZaakInformatieObject.getZaak());

        var edcLa01 = new ZdsEdcLa01(zdsEdcLv01.stuurgegevens);

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

//    public ZgwZaak actualiseerZaakstatus(ZdsZakLk01ActualiseerZaakstatus zakLk01)   {
//        ZdsZakLk01ActualiseerZaakstatus.Object object = zakLk01.objects.get(1);
    public ZgwZaak actualiseerZaakstatus(ZdsZaak wasZaak, ZdsZaak wordtZaak)  {
//      ZdsZakLk01ActualiseerZaakstatus.Object object = zakLk01.objects.get(1);    
    	ZgwZaak zgwZaak = zgwClient.getZaakByIdentificatie(wasZaak.identificatie);
    	var zdsHeeft = wordtZaak.heeft.get(0);
        var zdsStatus = zdsHeeft.gerelateerde;
    	var zgwStatusType = zgwClient.getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype, zdsStatus.volgnummer, zdsStatus.omschrijving);        
        
        ZgwStatus zgwStatus = modelMapper.map(zdsHeeft, ZgwStatus.class);
        zgwStatus.zaak = zgwZaak.url;
        zgwStatus.statustype = zgwStatusType.url;

        zgwClient.actualiseerZaakStatus(zgwStatus);
        return zgwZaak;
    }

    public ZdsZakLa01GeefZaakDetails getZaakDetails(ZdsZakLv01 zdsZakLv01) {
        ZdsZakLa01GeefZaakDetails zdsZakLa01GeefZaakDetails = new ZdsZakLa01GeefZaakDetails();

        if (zdsZakLv01.gelijk != null && zdsZakLv01.gelijk.identificatie != null) {
        	var zaakidentificatie = zdsZakLv01.gelijk.identificatie;
            var zgwZaak = zgwClient.getZaakByIdentificatie(zaakidentificatie);
            if (zgwZaak == null) {
                throw new ConverterException("Zaak not found for identification: '" + zdsZakLv01.gelijk.identificatie + "'");
            }
            zdsZakLa01GeefZaakDetails.stuurgegevens = new ZdsStuurgegevens(zdsZakLv01.stuurgegevens);
            zdsZakLa01GeefZaakDetails.stuurgegevens.berichtcode = "La01";

            zdsZakLa01GeefZaakDetails.antwoord = new ZdsZakLa01GeefZaakDetails.Antwoord();
            zdsZakLa01GeefZaakDetails.antwoord.zaak = new ZdsZaak();
            zdsZakLa01GeefZaakDetails.antwoord.zaak = modelMapper.map(zgwZaak, ZdsZaak.class);

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

            zdsZakLa01GeefZaakDetails.antwoord.zaak.opschorting = zgwZaak.getOpschorting() != null ? modelMapper.map(zgwZaak.getOpschorting(), ZdsZaak.Opschorting.class) : null;
            zdsZakLa01GeefZaakDetails.antwoord.zaak.verlenging = zgwZaak.getVerlenging() != null ? modelMapper.map(zgwZaak.getVerlenging(), ZdsZaak.Verlenging.class) : null;

            var zgwStatussen = zgwClient.getStatussenByZaakUrl(zgwZaak.url);
            
            var zdsStatussen = zgwStatussen
            .stream()
            .map(zgwStatus -> {
                ZgwStatusType zgwStatusType = zgwClient.getResource(zgwStatus.statustype, ZgwStatusType.class);
                return modelMapper.map(zgwStatus, ZdsHeeft.class)
                        .setEntiteittype("ZAKSTT")
                        .setIndicatieLaatsteStatus(Boolean.valueOf(zgwStatusType.isEindstatus) ? "J" : "N");
            })
            .collect(Collectors.toList());
            
            zdsZakLa01GeefZaakDetails.antwoord.zaak.heeft = zdsStatussen;
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

    public void updateZaak(ZdsZaak zdsWasZaak, ZdsZaak zdsWordtZaak) {
        ZgwZaak zgwZaak = zgwClient.getZaakByIdentificatie(zdsWasZaak.identificatie);
        if (zgwZaak == null) {
            throw new RuntimeException("Zaak with identification " + zdsWasZaak.identificatie + " not found in ZGW");
        }
        ChangeDetector changeDetector = new ChangeDetector();
        changeDetector.detect(zdsWasZaak, zdsWordtZaak);

        var changed = false;
        // var fieldChanges = changeDetector.getAllChangesByFieldType(ZdsZaak.class);
        var fieldChanges = changeDetector.getAllChangesByDeclaringClassAndFilter(ZdsZaak.class, ZdsRol.class);
        if (fieldChanges.size() > 0) {
        	log.info("Update of zaakid:" + zdsWasZaak.identificatie + " has # " + fieldChanges.size() + " field changes");
        	
            ZgwZaakPut updatedZaak = this.modelMapper.map(zdsWordtZaak, ZgwZaakPut.class);
            // TODO: wrom niet in de mapper?            
            updatedZaak.zaaktype = zgwZaak.zaaktype;
            updatedZaak.bronorganisatie = zgwZaak.bronorganisatie;
            updatedZaak.verantwoordelijkeOrganisatie = zgwZaak.verantwoordelijkeOrganisatie;
            zgwClient.updateZaak(zgwZaak.uuid, updatedZaak);
            
            changed = true;
        }

        Map<ChangeDetector.Change, ChangeDetector.ChangeType> rolChanges = changeDetector.getAllChangesByFieldType(ZdsRol.class);
        if (rolChanges.size() > 0) {
        	log.info("Update of zaakid:" + zdsWasZaak.identificatie + " has # " + rolChanges.size() + " rol changes");

        	changeDetector.filterChangesByType(rolChanges, ChangeDetector.ChangeType.NEW).forEach((change, changeType) -> {
            	addRolToZgw((ZdsRol) change.getValue(), getRolOmschrijvingGeneriekByRolName(change.getField().getName()), zgwZaak);
            });

            changeDetector.filterChangesByType(rolChanges, ChangeDetector.ChangeType.DELETED).forEach((change, changeType) -> {
                deleteRolFromZgw(getRolOmschrijvingGeneriekByRolName(change.getField().getName()), zgwZaak);
            });

            changeDetector.filterChangesByType(rolChanges, ChangeDetector.ChangeType.CHANGED).forEach((change, changeType) -> {
            	updateRolInZgw(getRolOmschrijvingGeneriekByRolName(change.getField().getName()), zgwZaak, change.getValue());
            });
            changed = true;
        }
        if(!changed) {
        	log.warn("Update of zaakid:" + zdsWasZaak.identificatie + " without any changes");
        }
    }

    private void updateRolInZgw(String typeRolOmschrijving, ZgwZaak zgwZaak, Object value)   {
        //no put action for rollen, so first delete then add
        log.debug("Attempting to update rol by deleting and adding as new");
        deleteRolFromZgw(typeRolOmschrijving, zgwZaak);
        addRolToZgw((ZdsRol) value, typeRolOmschrijving, zgwZaak);
    }

    private void deleteRolFromZgw(String typeRolOmschrijving, ZgwZaak zgwZaak) {
        var roltype = zgwClient.getRolTypeByZaaktypeUrlAndOmschrijving(zgwZaak.zaaktype, typeRolOmschrijving);
        if(roltype == null) {        	
        	throw new ConverterException("Roltype: " + typeRolOmschrijving + " niet gevonden bij zaaktype voor zaak: " + zgwZaak.identificatie);
        }
        var rol = zgwClient.getRolByZaakUrlAndRolTypeUrl(zgwZaak.url, roltype.url);
        if(rol == null) {
        	throw new ConverterException("Rol: " + typeRolOmschrijving + " niet gevonden bij zaak: " + zgwZaak.identificatie);
        }
        zgwClient.deleteRol(roltype.uuid);
        
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

