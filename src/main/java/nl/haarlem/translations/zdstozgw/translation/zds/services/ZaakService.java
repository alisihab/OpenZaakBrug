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
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import java.lang.invoke.MethodHandles;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;


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
    
    public ZgwZaak creeerZaak(String rsin, ZdsZaak zdsZaak)   {    
    	log.info("creeerZaak:" + zdsZaak.identificatie);        
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
            //TODO: controleren of werkt
            for(ZdsKenmerk kenmerk : zdsZaak.getKenmerk() )
            {
            	zgwZaak.kenmerk.add(modelMapper.map(kenmerk, ZgwKenmerk.class));
            }
        }
        zgwZaak = zgwClient.addZaak(zgwZaak);
        log.debug("Created a ZGW Zaak with UUID: " + zgwZaak.getUuid());
        
        // rollen
        ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguratie().getZgwRolOmschrijving();
        addRolToZgw(zdsZaak.heeftAlsInitiator, zgwRolOmschrijving.getHeeftAlsInitiator(), zgwZaak);
        addRolToZgw(zdsZaak.heeftAlsBelanghebbende, zgwRolOmschrijving.getHeeftAlsBelanghebbende(), zgwZaak);
        addRolToZgw(zdsZaak.heeftAlsGemachtigde, zgwRolOmschrijving.getHeeftAlsGemachtigde(), zgwZaak);
        addRolToZgw(zdsZaak.heeftAlsOverigBetrokkene, zgwRolOmschrijving.getHeeftAlsOverigBetrokkene(), zgwZaak);
        addRolToZgw(zdsZaak.heeftAlsUitvoerende, zgwRolOmschrijving.getHeeftAlsUitvoerende(), zgwZaak);
        addRolToZgw(zdsZaak.heeftAlsVerantwoordelijke, zgwRolOmschrijving.getHeeftAlsVerantwoordelijke(), zgwZaak);
        

        
        // status
        if(zdsZaak.heeft != null && zdsZaak.heeft.size() > 0 && zdsZaak.heeft.get(0).gerelateerde != null) {
        	log.debug("Update of zaakid:" + zdsZaak.identificatie + " has status changes");

        	var zdsHeeft = zdsZaak.heeft.get(0);
	    	var zdsStatus = zdsHeeft.gerelateerde;
	        var zgwStatusType = zgwClient.getStatusTypeByZaakTypeAndOmschrijving(zgwZaak.zaaktype, zdsStatus.omschrijving, zdsStatus.volgnummer);
	        ZgwStatus zgwStatus = modelMapper.map(zdsHeeft, ZgwStatus.class);
	        zgwStatus.zaak = zgwZaak.url;
	        zgwStatus.statustype = zgwStatusType.url;
	        zgwClient.actualiseerZaakStatus(zgwStatus);
        }
        
        return zgwZaak;
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
    	log.info("geefLijstZaakdocumenten:" + zaakidentificatie);
	  	ZgwZaak zgwZaak = zgwClient.getZaakByIdentificatie(zaakidentificatie);
	
	  	var relevanteDocumenten = new ArrayList<ZdsHeeftRelevant>();
	  	for(ZgwZaakInformatieObject zgwZaakInformatieObject : zgwClient.getZaakInformatieObjectenByZaak(zgwZaak.url)) {
	        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = zgwClient.getZaakDocument(zgwZaakInformatieObject.informatieobject);
	        if(zgwEnkelvoudigInformatieObject == null) {
	        	throw new ConverterException("could not get the zaakdocument: " + zgwZaakInformatieObject.informatieobject + " for zaak:" + zaakidentificatie );
	        }
	        ZgwInformatieObjectType documenttype = zgwClient.getZgwInformatieObjectTypeByUrl(zgwEnkelvoudigInformatieObject.informatieobjecttype);
	        if(documenttype == null) {
	        	throw new ConverterException("getZgwInformatieObjectType #" + zgwEnkelvoudigInformatieObject.informatieobjecttype + " could not be found");
	        }        	        
/*
        if(zgwEnkelvoudigInformatieObject == null) {
        	throw new ConverterException("ZgwEnkelvoudigInformatieObject #" + documentIdentificatie + " could not be found");
        }        
        ZgwInformatieObjectType documenttype = zgwClient.getZgwInformatieObjectTypeByÙrl(zgwEnkelvoudigInformatieObject.informatieobjecttype);
        if(documenttype == null) {
        	throw new ConverterException("getZgwInformatieObjectType #" + zgwEnkelvoudigInformatieObject.informatieobjecttype + " could not be found");
        }        
        var zgwZaakInformatieObject = zgwClient.getZgwZaakInformatieObjectByEnkelvoudigInformatieObjectUrl(zgwEnkelvoudigInformatieObject.getUrl());
        if(zgwZaakInformatieObject == null) {
        	throw new ConverterException("getZgwZaakInformatieObjectByUrl #" + zgwEnkelvoudigInformatieObject.getUrl() + " could not be found");
        }        
        var zgwZaak = zgwClient.getZaakByUrl(zgwZaakInformatieObject.getZaak());
        if(zgwZaak == null) {
        	throw new ConverterException("getZaakByUrl #" + zgwZaakInformatieObject.getZaak() + " could not be found");
        }        
        String inhoud = zgwClient.getBas64Inhoud(zgwEnkelvoudigInformatieObject.getInhoud());
        if(inhoud == null) {
        	throw new ConverterException("getBas64Inhoud #" + zgwEnkelvoudigInformatieObject.getInhoud() + " could not be found");
        }        
        
        
        ZdsZaakDocumentInhoud result = modelMapper.map(zgwEnkelvoudigInformatieObject, ZdsZaakDocumentInhoud.class);
        result.inhoud = new ZdsInhoud();
        var mimeType = URLConnection.guessContentTypeFromName(zgwEnkelvoudigInformatieObject.bestandsnaam);
        // documenttype
        result.omschrijving = documenttype.omschrijving;
	        
 */
	        ZdsZaakDocument zdsZaakDocument = modelMapper.map(zgwEnkelvoudigInformatieObject, ZdsZaakDocument.class);
	        zdsZaakDocument.omschrijving = documenttype.omschrijving;
	        ZdsHeeftRelevant heeftRelevant = modelMapper.map(zgwZaakInformatieObject, ZdsHeeftRelevant.class);
	        heeftRelevant.gerelateerde = zdsZaakDocument;
	        relevanteDocumenten.add(heeftRelevant );
	  	}
	    return relevanteDocumenten;
    }

    public ZgwEnkelvoudigInformatieObject voegZaakDocumentToe(String rsin, ZdsZaakDocumentInhoud zdsInformatieObject)   {
    	log.info("voegZaakDocumentToe:" + zdsInformatieObject.identificatie);
    	
        var zgwInformatieObjectType = zgwClient.getZgwInformatieObjectTypeByOmschrijving(zdsInformatieObject.omschrijving);
        if(zgwInformatieObjectType == null) {
        	throw new RuntimeException("Documenttype not found for omschrijving: "+ zdsInformatieObject.omschrijving);
        }
        
        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = modelMapper.map(zdsInformatieObject, ZgwEnkelvoudigInformatieObject.class);
        zgwEnkelvoudigInformatieObject.informatieobjecttype = zgwInformatieObjectType.url;
        zgwEnkelvoudigInformatieObject.bronorganisatie = rsin;
        zgwEnkelvoudigInformatieObject = zgwClient.addZaakDocument(zgwEnkelvoudigInformatieObject);
        ZgwZaak zgwZaak = zgwClient.getZaakByIdentificatie(zdsInformatieObject.isRelevantVoor.gerelateerde.identificatie);        
        ZgwZaakInformatieObject zgwZaakInformatieObject = addZaakInformatieObject(zgwEnkelvoudigInformatieObject, zgwZaak.url);
        
        // status
        if(zdsInformatieObject.isRelevantVoor.volgnummer != null && zdsInformatieObject.isRelevantVoor.omschrijving  != null && zdsInformatieObject.isRelevantVoor.datumStatusGezet != null) {
        	log.debug("Update of zaakid:" + zgwZaak.identificatie + " has  status changes");
	        var zgwStatusType = zgwClient.getStatusTypeByZaakTypeAndOmschrijving(zgwZaak.zaaktype, zdsInformatieObject.isRelevantVoor.omschrijving, zdsInformatieObject.isRelevantVoor.volgnummer);
	        // ZgwStatus zgwStatus = modelMapper.map(zdsHeeft, ZgwStatus.class);
	        ZgwStatus zgwStatus = new ZgwStatus();
	        zgwStatus.zaak = zgwZaak.url;
	        zgwStatus.statustype = zgwStatusType.url;
	        zgwClient.actualiseerZaakStatus(zgwStatus);
        }
        
        return zgwEnkelvoudigInformatieObject;
    }

    private ZgwZaakInformatieObject addZaakInformatieObject(ZgwEnkelvoudigInformatieObject doc, String zaakUrl)  {
        var zgwZaakInformatieObject = new ZgwZaakInformatieObject();
        zgwZaakInformatieObject.setZaak(zaakUrl);
        zgwZaakInformatieObject.setInformatieobject(doc.getUrl());
        zgwZaakInformatieObject.setTitel(doc.getTitel());
        return zgwClient.addDocumentToZaak(zgwZaakInformatieObject);
    }

    public ZdsZaakDocumentInhoud getZaakDocumentLezen(String documentIdentificatie)  {
    	log.info("getZaakDocumentLezen:" + documentIdentificatie);
        ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = zgwClient.getZgwEnkelvoudigInformatieObjectByIdentiticatie(documentIdentificatie);
        if(zgwEnkelvoudigInformatieObject == null) {
        	throw new ConverterException("ZgwEnkelvoudigInformatieObject #" + documentIdentificatie + " could not be found");
        }        
        ZgwInformatieObjectType documenttype = zgwClient.getZgwInformatieObjectTypeByUrl(zgwEnkelvoudigInformatieObject.informatieobjecttype);
        if(documenttype == null) {
        	throw new ConverterException("getZgwInformatieObjectType #" + zgwEnkelvoudigInformatieObject.informatieobjecttype + " could not be found");
        }        
        var zgwZaakInformatieObject = zgwClient.getZgwZaakInformatieObjectByEnkelvoudigInformatieObjectUrl(zgwEnkelvoudigInformatieObject.getUrl());
        if(zgwZaakInformatieObject == null) {
        	throw new ConverterException("getZgwZaakInformatieObjectByUrl #" + zgwEnkelvoudigInformatieObject.getUrl() + " could not be found");
        }        
        var zgwZaak = zgwClient.getZaakByUrl(zgwZaakInformatieObject.getZaak());
        if(zgwZaak == null) {
        	throw new ConverterException("getZaakByUrl #" + zgwZaakInformatieObject.getZaak() + " could not be found");
        }        
        String inhoud = zgwClient.getBas64Inhoud(zgwEnkelvoudigInformatieObject.getInhoud());
        if(inhoud == null) {
        	throw new ConverterException("getBas64Inhoud #" + zgwEnkelvoudigInformatieObject.getInhoud() + " could not be found");
        }        
                
        ZdsZaakDocumentInhoud result = modelMapper.map(zgwEnkelvoudigInformatieObject, ZdsZaakDocumentInhoud.class);
        result.inhoud = new ZdsInhoud();
        var mimeType = URLConnection.guessContentTypeFromName(zgwEnkelvoudigInformatieObject.bestandsnaam);
                      
        // documenttype
        result.omschrijving = documenttype.omschrijving; 
        if(result.ontvangstdatum == null) {
        	result.ontvangstdatum = "00010101";
        }
        result.titel = zgwEnkelvoudigInformatieObject.titel;
        result.beschrijving = zgwEnkelvoudigInformatieObject.beschrijving;
        if(result.beschrijving.length() == 0) result.beschrijving = null;
        if(result.versie.length() == 0) result.versie = null;
        if(result.taal.length() == 0) result.taal= null;        
        if(result.status.length() == 0) result.status = null;        

        result.formaat = zgwEnkelvoudigInformatieObject.bestandsnaam.substring(zgwEnkelvoudigInformatieObject.bestandsnaam.lastIndexOf(".") + 1);
        result.inhoud.contentType = mimeType;
        result.inhoud.bestandsnaam = zgwEnkelvoudigInformatieObject.bestandsnaam;
        result.inhoud.value = inhoud;
        result.isRelevantVoor = new ZdsIsRelevantVoor();
        result.isRelevantVoor.gerelateerde = new ZdsGerelateerde();
        result.isRelevantVoor.gerelateerde.entiteittype = "ZAK";
        result.isRelevantVoor.gerelateerde.identificatie = zgwZaak.identificatie;
        result.isRelevantVoor.gerelateerde.omschrijving = zgwZaak.omschrijving;
        
        return result;
    }

    public ZgwZaak actualiseerZaakstatus(ZdsZaak wasZaak, ZdsZaak wordtZaak)  {
    	log.info("actualiseerZaakstatus:" + wasZaak.identificatie);
    	ZgwZaak zgwZaak = zgwClient.getZaakByIdentificatie(wasZaak.identificatie);
    	var zdsHeeft = wordtZaak.heeft.get(0);
        var zdsStatus = zdsHeeft.gerelateerde;
    	//var zgwStatusType = zgwClient.getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype, zdsStatus.volgnummer, zdsStatus.omschrijving);
        var zgwStatusType = zgwClient.getStatusTypeByZaakTypeAndOmschrijving(zgwZaak.zaaktype, zdsStatus.omschrijving, zdsStatus.volgnummer);
        
        
        ZgwStatus zgwStatus = modelMapper.map(zdsHeeft, ZgwStatus.class);
        zgwStatus.zaak = zgwZaak.url;
        zgwStatus.statustype = zgwStatusType.url;

        zgwClient.actualiseerZaakStatus(zgwStatus);
        return zgwZaak;
    }

    
    public List<ZdsZaak> getZaakDetailsByBsn(String bsn) {
		log.info("getZaakDetailsByBsn:" + bsn);
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
    
	public ZdsZaak getZaakDetailsByIdentificatie(String zaakidentificatie) {     
		log.info("getZaakDetailsByIdentificatie:" + zaakidentificatie);
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
            		&& zgwRolOmschrijving.getHeeftAlsInitiator().equalsIgnoreCase(zgwRol.getRoltoelichting())) {
                zaak.heeftAlsInitiator = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsInitiator(), "ZAKBTRINI");
                rolGeconverteerd  = true;
            }            	
			if (zgwRolOmschrijving.getHeeftAlsBelanghebbende() != null 
            		&& zgwRolOmschrijving.getHeeftAlsBelanghebbende().equalsIgnoreCase(zgwRol.getRoltoelichting())) {
                zaak.heeftAlsBelanghebbende = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsBelanghebbende(), "ZAKBTRBLH");
                rolGeconverteerd  = true;
            }
            if (zgwRolOmschrijving.getHeeftAlsUitvoerende() != null 
            		&& zgwRolOmschrijving.getHeeftAlsUitvoerende().equalsIgnoreCase(zgwRol.getRoltoelichting())) {
                zaak.heeftAlsUitvoerende = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsUitvoerende(), "ZAKBTRUTV");
                rolGeconverteerd  = true;
            }
            if (zgwRolOmschrijving.getHeeftAlsVerantwoordelijke() != null 
            		&& zgwRolOmschrijving.getHeeftAlsVerantwoordelijke().equalsIgnoreCase(zgwRol.getRoltoelichting())) {
                zaak.heeftAlsVerantwoordelijke = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsVerantwoordelijke(), "ZAKBTRVRA");
                rolGeconverteerd  = true;
            }                
            if (zgwRolOmschrijving.getHeeftAlsGemachtigde() != null 
            		&& zgwRolOmschrijving.getHeeftAlsGemachtigde().equalsIgnoreCase(zgwRol.getRoltoelichting())) {
                zaak.heeftAlsGemachtigde = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsGemachtigde(), "ZAKBTRGMC");
                rolGeconverteerd  = true;
            }
            if (zgwRolOmschrijving.getHeeftAlsOverigBetrokkene() != null 
            		&& zgwRolOmschrijving.getHeeftAlsOverigBetrokkene().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
                zaak.heeftAlsOverigBetrokkene = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsOverigBetrokkene(), "ZAKBTROVR");
                rolGeconverteerd  = true;
            }
			if(!rolGeconverteerd) {
				throw new ConverterException("Rol: " + zgwRol.getOmschrijvingGeneriek() + " niet geconverteerd worden (" + zgwRol.uuid + ")");
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
            	var zdsKenmerkKenmerk =  modelMapper.map(zgwKenmerk, ZdsKenmerk.class);            	
            	zaak.kenmerk.add(zdsKenmerkKenmerk);
            }
        }

        zaak.opschorting = zgwZaak.getOpschorting() != null ? modelMapper.map(zgwZaak.getOpschorting(), ZdsOpschorting.class) : null;
        zaak.verlenging = zgwZaak.getVerlenging() != null ? modelMapper.map(zgwZaak.getVerlenging(), ZdsVerlenging.class) : null;


        var zdsStatussen = new ArrayList<ZdsHeeft>();
        for(ZgwStatus zgwStatus : zgwClient.getStatussenByZaakUrl(zgwZaak.url)) {
        	ZgwStatusType zgwStatusType = zgwClient.getResource(zgwStatus.statustype, ZgwStatusType.class);
        	//ZdsHeeft zdsHeeft = modelMapper.map(zgwStatus, ZdsHeeft.class);
        	ZdsHeeft zdsHeeft = new ZdsHeeft();
        	zdsHeeft.setEntiteittype("ZAKSTT");
        	zdsHeeft.setIndicatieLaatsteStatus(Boolean.valueOf(zgwStatusType.isEindstatus) ? "J" : "N");

        	zdsHeeft.gerelateerde = modelMapper.map(zgwStatus, ZdsGerelateerde.class);
        	zdsHeeft.gerelateerde.setEntiteittype("STT");
        	
        	zdsHeeft.gerelateerde.zktCode = zgwZaakType.identificatie;
        	zdsHeeft.gerelateerde.zktOmschrijving = zgwZaakType.omschrijving;
        	zdsHeeft.gerelateerde.omschrijving = zgwStatus.statustoelichting;

        	zdsStatussen.add(zdsHeeft);
        }
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
        ZdsRol zdsRol = this.modelMapper.map(zgwRol, ZdsRol.class);
        zdsRol.setEntiteittype(entiteittype);
        return zdsRol;
    }

    public void updateZaak(ZdsZaak zdsWasZaak, ZdsZaak zdsWordtZaak) {
    	log.info("updateZaak:" + zdsWasZaak.identificatie);
        ZgwZaak zgwZaak = zgwClient.getZaakByIdentificatie(zdsWasZaak.identificatie);
        if (zgwZaak == null) {
            throw new RuntimeException("Zaak with identification " + zdsWasZaak.identificatie + " not found in ZGW");
        }
        
        // attributen
        ChangeDetector changeDetector = new ChangeDetector();
        changeDetector.detect(zdsWasZaak, zdsWordtZaak);
        var changed = false;
        var fieldChanges = changeDetector.getAllChangesByDeclaringClassAndFilter(ZdsZaak.class, ZdsRol.class);
        if (fieldChanges.size() > 0) {
        	log.debug("Update of zaakid:" + zdsWasZaak.identificatie + " has # " + fieldChanges.size() + " field changes");
            for(Change change: fieldChanges.keySet()) {
            	log.debug("\tchange:" + change.getField().getName());
            }            
            ZgwZaakPut zgwWordtZaak = this.modelMapper.map(zdsWordtZaak, ZgwZaakPut.class);
            ZgwZaakPut updatedZaak = ZgwZaakPut.merge(zgwZaak, zgwWordtZaak);
            zgwClient.updateZaak(zgwZaak.uuid, updatedZaak);
            
            changed = true;
        }

        // rollen
        Map<ChangeDetector.Change, ChangeDetector.ChangeType> rolChanges = changeDetector.getAllChangesByFieldType(ZdsRol.class);
        if (rolChanges.size() > 0) {
        	log.debug("Update of zaakid:" + zdsWasZaak.identificatie + " has # " + rolChanges.size() + " rol changes:");

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

        // status
        if(zdsWordtZaak.heeft != null && zdsWordtZaak.heeft.size() > 0 && zdsWordtZaak.heeft.get(0).gerelateerde != null) {
        	log.debug("Update of zaakid:" + zdsWasZaak.identificatie + " has  status changes");

        	var zdsHeeft = zdsWordtZaak.heeft.get(0);
	    	var zdsStatus = zdsHeeft.gerelateerde;
	        var zgwStatusType = zgwClient.getStatusTypeByZaakTypeAndOmschrijving(zgwZaak.zaaktype, zdsStatus.omschrijving, zdsStatus.volgnummer);
	        ZgwStatus zgwStatus = modelMapper.map(zdsHeeft, ZgwStatus.class);
	        zgwStatus.zaak = zgwZaak.url;
	        zgwStatus.statustype = zgwStatusType.url;
	        zgwClient.actualiseerZaakStatus(zgwStatus);
	        
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

