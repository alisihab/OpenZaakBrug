package nl.haarlem.translations.zdstozgw.translation.zds.services;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.Organisatie;
import nl.haarlem.translations.zdstozgw.config.model.ZgwRolOmschrijving;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.BetrokkeneType;
import nl.haarlem.translations.zdstozgw.translation.zds.client.ZDSClient;
import nl.haarlem.translations.zdstozgw.translation.zds.model.*;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.*;
import nl.haarlem.translations.zdstozgw.utils.ChangeDetector;
import nl.haarlem.translations.zdstozgw.utils.ChangeDetector.Change;
import nl.haarlem.translations.zdstozgw.utils.ChangeDetector.ChangeType;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ZaakService {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public final ZGWClient zgwClient;
    public final ZDSClient zdsClient;

    private final ModelMapper modelMapper;
    public final ConfigService configService;


    @Autowired
    public ZaakService(ZGWClient zgwClient, ZDSClient zdsClient, ModelMapper modelMapper, ConfigService configService) {
        this.zgwClient = zgwClient;
        this.zdsClient = zdsClient;
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

  public List<ZdsHeeftRelevant> geefLijstZaakdocumenten(String zaakidentificatie)  {
	  	ZgwZaak zgwZaak = zgwClient.getZaakByIdentificatie(zaakidentificatie);

	  	var relevanteDocumenten = new ArrayList<ZdsHeeftRelevant>();
	  	for(ZgwZaakInformatieObject zgwZaakInformatieObject : zgwClient.getZaakInformatieObjectenByZaak(zgwZaak.url)) {
            ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = zgwClient.getZaakDocument(zgwZaakInformatieObject.informatieobject);
            if(zgwEnkelvoudigInformatieObject == null) {
            	throw new ConverterException("could not get the zaakdocument: " + zgwZaakInformatieObject.informatieobject + " for zaak:" + zaakidentificatie );
            }
            ZdsZaakDocument zdsZaakDocument = modelMapper.map(zgwEnkelvoudigInformatieObject, ZdsZaakDocument.class);            
            ZdsHeeftRelevant heeftRelevant = modelMapper.map(zgwZaakInformatieObject, ZdsHeeftRelevant.class);
            heeftRelevant.gerelateerde = zdsZaakDocument;
            relevanteDocumenten.add(heeftRelevant );
	  	}
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

//    public ZdsEdcLa01GeefZaakdocumentLezen getZaakDocumentLezen(ZdsEdcLv01 zdsEdcLv01)  {
        //Get Enkelvoudig informatie object. This contains document meta data and a link to the document
//    	var documentIdentificatie = zdsEdcLv01.gelijk.identificatie;
//    public ZdsEdcLa01GeefZaakdocumentLezen getZaakDocumentLezen(String documentIdentificatie)  {
    public ZdsZaakDocument getZaakDocumentLezen(String documentIdentificatie)  {

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

        ZdsZaakDocument result = modelMapper.map(zgwEnkelvoudigInformatieObject, ZdsZaakDocument.class);
        result.inhoud = new ZdsInhoud();
        var mimeType = URLConnection.guessContentTypeFromName(zgwEnkelvoudigInformatieObject.bestandsnaam);
        result.inhoud.contentType = mimeType;
        result.inhoud.bestandsnaam = zgwEnkelvoudigInformatieObject.bestandsnaam;
        result.inhoud.value = inhoud;
        result.link = null;
        return result;
        
/*        
        var edcLa01 = new ZdsEdcLa01GeefZaakdocumentLezen(zdsEdcLv01.stuurgegevens);
        edcLa01.antwoord = new ZdsAntwoord();
        edcLa01.antwoord.object = modelMapper.map(zgwEnkelvoudigInformatieObject, ZdsZaakDocument.class);

        //Add Inhoud
//        edcLa01.isRelevantVoor = new ZdsIsRelevantVoor();
//        edcLa01.isRelevantVoor.gerelateerde = new ZdsGerelateerde();
//        edcLa01.isRelevantVoor.entiteittype = "EDCZAK";
//        edcLa01.isRelevantVoor.gerelateerde.entiteittype = "ZAK";
//        edcLa01.isRelevantVoor.gerelateerde.identificatie = zgwZaak.getIdentificatie();
        edcLa01.antwoord.object.inhoud = new ZdsInhoud();
        edcLa01.antwoord.object.inhoud.contentType = "asd";
        edcLa01.antwoord.object.inhoud.bestandsnaam = "asd";
        edcLa01.antwoord.object.inhoud.value = inhoud;

        return edcLa01;
  */
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

    
    public List<ZdsZaak> getZaakDetailsByBsn(String bsn) {    	
    	var zgwRollen = zgwClient.getRollenByBsn(bsn);
    	var zdsZaken = new ArrayList<ZdsZaak>();
    	var result = new ArrayList<ZdsZaak>();
    	for(ZgwRol rol : zgwRollen) {
    		var zgwRolType = zgwClient.getRolTypeByUrl(rol.roltype);
    		ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguratie().getZgwRolOmschrijving();
    		if(zgwRolType.omschrijving.equals(zgwRolOmschrijving.getHeeftAlsInitiator())) {

    			// TODO: hier minder overhead!
    			// hier wordt nu 2 keer achterelkaar een getzaak op openzaak gedaan! 
    			var zgwZaak = zgwClient.getZaakByUrl(rol.zaak);
    			result.add(getZaakDetailsByIdentificatie(zgwZaak.identificatie));
    		}
    	}    
    	return result;
    }
    
    
//    public ZdsZakLa01GeefZaakDetails getZaakDetails(ZdsZakLv01 zdsZakLv01) {
//        ZdsZakLa01GeefZaakDetails zdsZakLa01GeefZaakDetails = new ZdsZakLa01GeefZaakDetails();
//        if (zdsZakLv01.gelijk != null && zdsZakLv01.gelijk.identificatie != null) {
    public ZdsZaak getZaakDetailsByIdentificatie(String zaakidentificatie) {            	
            var zgwZaak = zgwClient.getZaakByIdentificatie(zaakidentificatie);
            if (zgwZaak == null) {
                throw new ConverterException("Zaak not found for identification: '" + zaakidentificatie + "'");
            }
            ZdsZaak zaak = new ZdsZaak();
            zaak = modelMapper.map(zgwZaak, ZdsZaak.class);

            ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguratie().getZgwRolOmschrijving();

            for(ZgwRol zgwRol : zgwClient.getRollenByZaakUrl(zgwZaak.url)) {
            	var rolGeconverteerd = false;
                if (zgwRolOmschrijving.getHeeftAlsInitiator() != null 
                		&& zgwRolOmschrijving.getHeeftAlsInitiator().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zaak.heeftAlsInitiator = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsInitiator(), "ZAKBTRINI");
                    rolGeconverteerd  = true;
                }            	
				if (zgwRolOmschrijving.getHeeftAlsBelanghebbende() != null 
                		&& zgwRolOmschrijving.getHeeftAlsBelanghebbende().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zaak.heeftAlsBelanghebbende = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsBelanghebbende(), "ZAKBTRBLH");
                    rolGeconverteerd  = true;
                }
                if (zgwRolOmschrijving.getHeeftAlsUitvoerende() != null 
                		&& zgwRolOmschrijving.getHeeftAlsUitvoerende().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zaak.heeftAlsUitvoerende = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsUitvoerende(), "ZAKBTRUTV");
                    rolGeconverteerd  = true;
                }
                if (zgwRolOmschrijving.getHeeftAlsVerantwoordelijke() != null 
                		&& zgwRolOmschrijving.getHeeftAlsVerantwoordelijke().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zaak.heeftAlsVerantwoordelijke = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsVerantwoordelijke(), "ZAKBTRVRA");
                    rolGeconverteerd  = true;
                }                
                if (zgwRolOmschrijving.getHeeftAlsGemachtigde() != null 
                		&& zgwRolOmschrijving.getHeeftAlsGemachtigde().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zaak.heeftAlsGemachtigde = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsGemachtigde(), "ZAKBTRGMC");
                    rolGeconverteerd  = true;
                }
                if (zgwRolOmschrijving.getHeeftAlsOverigBetrokkene() != null 
                		&& zgwRolOmschrijving.getHeeftAlsOverigBetrokkene().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                    zaak.heeftAlsOverigBetrokkene = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsOverigBetrokkene(), "ZAKBTROVR");
                    rolGeconverteerd  = true;
                }
				if(!rolGeconverteerd) {
					throw new ConverterException("Rol: " + zgwRol.getOmschrijvingGeneriek() + " niet geconverteerd worden");
				}
            }                        
            ZgwZaakType zgwZaakType = this.getZaakTypeByUrl(zgwZaak.zaaktype);
            zaak.isVan = new ZdsRol();
            zaak.isVan.entiteittype = "ZAKZKT";
            zaak.isVan.gerelateerde = new ZdsGerelateerde();
            zaak.isVan.gerelateerde.entiteittype = "ZKT";
            zaak.isVan.gerelateerde.code = zgwZaakType.identificatie;
            zaak.isVan.gerelateerde.omschrijving = zgwZaakType.omschrijving;

            if (zgwZaak.getKenmerk() != null && !zgwZaak.getKenmerk().isEmpty()) {
                zaak.kenmerk = new ArrayList<>();
                for(ZgwKenmerk zgwKenmerk : zgwZaak.getKenmerk()) {
                	zaak.kenmerk.add(modelMapper.map(zgwKenmerk, ZdsZaak.Kenmerk.class));
                }
            }

            zaak.opschorting = zgwZaak.getOpschorting() != null ? modelMapper.map(zgwZaak.getOpschorting(), ZdsZaak.Opschorting.class) : null;
            zaak.verlenging = zgwZaak.getVerlenging() != null ? modelMapper.map(zgwZaak.getVerlenging(), ZdsZaak.Verlenging.class) : null;

            var zgwStatussen = zgwClient.getStatussenByZaakUrl(zgwZaak.url);
            
            // TODO: wat gebeurd hier?
            var zdsStatussen = zgwStatussen
            .stream()
            .map(zgwStatus -> {
                ZgwStatusType zgwStatusType = zgwClient.getResource(zgwStatus.statustype, ZgwStatusType.class);
                return modelMapper.map(zgwStatus, ZdsHeeft.class)
                        .setEntiteittype("ZAKSTT")
                        .setIndicatieLaatsteStatus(Boolean.valueOf(zgwStatusType.isEindstatus) ? "J" : "N");
            })
            .collect(Collectors.toList());
            
            zaak.heeft = zdsStatussen;
            return zaak;
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
        var fieldChanges = changeDetector.getAllChangesByDeclaringClassAndFilter(ZdsZaak.class, ZdsRol.class);
        if (fieldChanges.size() > 0) {
        	log.info("Update of zaakid:" + zdsWasZaak.identificatie + " has # " + fieldChanges.size() + " field changes");
            for(Change change: fieldChanges.keySet()) {
            	log.info("change:" + change.getField().getName());
            }            
            ZgwZaakPut zgwWordtZaak = this.modelMapper.map(zdsWordtZaak, ZgwZaakPut.class);
            ZgwZaakPut updatedZaak = ZgwZaakPut.merge(zgwZaak, zgwWordtZaak);
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
        zgwClient.deleteRol(rol.uuid);
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

