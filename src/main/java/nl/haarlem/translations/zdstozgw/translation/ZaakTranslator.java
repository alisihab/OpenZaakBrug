package nl.haarlem.translations.zdstozgw.translation;


import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.BG;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import java.lang.invoke.MethodHandles;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.aspectj.weaver.Dump.INode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.DocumentType;
import nl.haarlem.translations.zdstozgw.config.Organisatie;
import nl.haarlem.translations.zdstozgw.config.ZaakType;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.jpa.model.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.translation.ZaakTranslator.ZaakTranslatorException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLa01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLa01.Antwoord;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLk01.ZdsDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.GerelateerdeRol;
import nl.haarlem.translations.zdstozgw.translation.zds.model.GerelateerdeWrapper;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Heeft;
//import nl.haarlem.translations.zdstozgw.translation.zds.model.HeeftAlsAanspreekpunt;
//import nl.haarlem.translations.zdstozgw.translation.zds.model.HeeftAlsBelanghebbende;
import nl.haarlem.translations.zdstozgw.translation.zds.model.HeeftAlsInitiator;
import nl.haarlem.translations.zdstozgw.translation.zds.model.HeeftAlsUitvoerende;
import nl.haarlem.translations.zdstozgw.translation.zds.model.HeeftRelevantEDC;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLa01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsRol;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLa01LijstZaakdocumenten;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakDocumentInhoud;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLa01Zaakdetails;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsMedewerker;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsNatuurlijkPersoon;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsRelatieZaakDocument;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient.ZGWClientException;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwAdres;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwBasicZaak;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwBetrokkeneIdentificatie;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwEnkelvoudigInformatieObject;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwInformatieObject;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwInformatieObjectType;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwNatuurlijkPersoon;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwRol;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwStatus;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwCompleteZaak;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakInformatieObject;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakType;

@Service
@Data
public class ZaakTranslator {
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@SuppressWarnings("serial")
	public class ZaakTranslatorException extends Exception {
		public ZaakTranslatorException(String message) {
			super(message);
		}
	}

	
	class ChangeDetector {
		private Boolean dirty = false;

		public Boolean compare(String eersteWaarde, String tweedeWaarde) {
			if(eersteWaarde == null && tweedeWaarde == null) return false;			
			if(eersteWaarde == null) return true;			
			return !eersteWaarde.equals(tweedeWaarde);
		}			
		
		public void compare(String eersteWaarde, String tweedeWaarde, Runnable bijverschil) {
			Boolean different = compare(eersteWaarde, tweedeWaarde);
			if(different) {
				bijverschil.run();
				dirty = true;
			}
		}
		
		public Boolean isDirty() {
			return dirty;
		}
	}	
	
	private ZGWClient zgwClient;
	private ConfigService configService;
	//@Autowired
	//private ConfigService configService;

	//private Document document;
	//private ZgwZaak zgwZaak;
	//private ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject;
	//private List<ZgwEnkelvoudigInformatieObject> zgwEnkelvoudigInformatieObjectList;
	//private ZakLk01_v2 zakLk01;
	//private EdcLk01 edcLk01;

	public ZaakTranslator(ZGWClient zgwClient, ConfigService configService) {
		this.zgwClient =  zgwClient;
		this.configService = configService;
	}

	public ZgwCompleteZaak creeerZaak(ZakLk01 zakLk01) throws ZGWClientException, ZaakTranslatorException {
		// zaakTranslator.setDocument((Document) zakLk01).zdsZaakToZgwZaak();
		//this.zaakTranslator.setZakLk01(zakLk01).zdsZaakToZgwZaak();

		var zdsZaak = zakLk01.object.get(0);
		ZgwCompleteZaak zgwZaak = zdsZaakToZgwZaak(zdsZaak);		

		// zaaktype
		var zgwZaakType = zgwClient.getZaakTypeByIdentiticatie(zdsZaak.isVan.gerelateerde.code);
		if(zgwZaakType == null) throw new ZaakTranslatorException("Geen zaaktype niet gevonden voor identificatie: '" + zdsZaak.isVan.gerelateerde.code + "'");
		zgwZaak.setZaaktype(zgwZaakType.getUrl());
				
		// verplichte velden
		if (zakLk01.stuurgegevens.zender.organisatie.length() == 0) throw new ZaakTranslatorException("zender.organisatie is verplicht");
		zgwZaak.setVerantwoordelijkeOrganisatie(getRSIN(zakLk01.stuurgegevens.zender.organisatie));
		if (getRSIN(zakLk01.stuurgegevens.ontvanger.organisatie).length() == 0) throw new ZaakTranslatorException("zaak identificatie is verplicht");
		zgwZaak.setBronorganisatie(getRSIN(zakLk01.stuurgegevens.ontvanger.organisatie));

		// en de zaak bestaat
		zgwZaak = this.zgwClient.postZaak(zgwZaak);					
		log.info("Created a ZGW Zaak with UUID: " + zgwZaak.getUuid() + " (" + zdsZaak.identificatie + ")");
		
		// rollen toevoegen
		var rollen = new java.util.ArrayList<ZgwRol>();		
		if (zdsZaak.heeftBetrekkingOp != null) rollen.addAll(getRollen(zgwZaak, zdsZaak.heeftBetrekkingOp, "Betrekking"));
		if (zdsZaak.heeftAlsBelanghebbende != null) rollen.addAll(getRollen(zgwZaak, zdsZaak.heeftAlsBelanghebbende, "Belanghebbende"));
		if (zdsZaak.heeftAlsInitiator != null) rollen.addAll(getRollen(zgwZaak, zdsZaak.heeftAlsInitiator, "Initiator"));
		if (zdsZaak.heeftAlsUitvoerende != null) rollen.addAll(getRollen(zgwZaak, zdsZaak.heeftAlsUitvoerende, "Uitvoerende"));
		if (zdsZaak.heeftAlsVerantwoordelijke != null) rollen.addAll(getRollen(zgwZaak, zdsZaak.heeftAlsVerantwoordelijke, "Verantwoordelijke"));		
		
		// if (zdsZaak.heeftAlsVerantwoordelijke != null) rollen.addAll(getRollen(zgwZaak, zdsZaak.heeftAlsVerantwoordelijke, "Verantwoordelijke"));		

		
		for(ZgwRol rol : rollen) {
			rol.setZaak(zgwZaak.getUrl());
			zgwClient.postRol(rol);
		}	
		
		// zet de status
		if(zdsZaak.heeft.gerelateerde.volgnummer != null) {
			var zgwZaakStatusNummer = zgwClient.getZaakTypeByIdentiticatie(zdsZaak.heeft.gerelateerde.volgnummer);
			var zgwZaakStatusCode = zgwClient.getZaakTypeByIdentiticatie(zdsZaak.heeft.gerelateerde.code);
			var zgwZaakStatusOmschrijving = zgwClient.getZaakTypeByIdentiticatie(zdsZaak.heeft.gerelateerde.omschrijving);
			
			ZgwStatus zgwStatus = new ZgwStatus();
			zgwStatus.statustoelichting = zdsZaak.heeft.gerelateerde.omschrijving;
			zgwStatus.datumStatusGezet = getDateTimeStringFromStufDate(zdsZaak.heeft.datumStatusGezet, this.configService.getConfiguratie().getTimeOffsetHour());
			zgwStatus.zaak = zgwZaak.url;
			zgwStatus.statustype = zgwClient.getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype, zdsZaak.heeft.gerelateerde.volgnummer, zdsZaak.heeft.gerelateerde.omschrijving).url;
			this.zgwClient.actualiseerZaakStatus(zgwStatus);			
		}
		if(zgwZaakType == null) throw new ZaakTranslatorException("Geen zaaktype niet gevonden ZTC voor identificatie: '" + zdsZaak.isVan.gerelateerde.code + "'");
		zgwZaak.setZaaktype(zgwZaakType.getUrl());

		
		return zgwZaak;
	}

	
	private void updateZaak(ZgwBasicZaak zgwZaak, List<ZgwRol> zgwRollen, String rolnaam, ZdsRol zdsWasRol, ZdsRol zdsWordtRol) throws ZaakTranslatorException, ZGWClientException {
		var zgwRoltype = zgwClient.getRolTypeByOmschrijving(zgwZaak.zaaktype, rolnaam);		
		if(zgwRoltype == null) throw new ZaakTranslatorException("Geen roltype niet gevonden ZTC voor identificatie: '" + rolnaam);
		// filter waar het ons roltype betreft
		zgwRollen = zgwRollen.stream().filter(r -> r.roltype.equals(zgwRoltype.url)).collect(Collectors.toList());
		
		// nu gaan we het vergelijken starten
		var wasRollen = getRollen(zgwZaak, zdsWasRol, rolnaam);
		var wordtRollen = getRollen(zgwZaak, zdsWordtRol, rolnaam);
		
		// acties die moeten gebeuren
		var postRollen = new java.util.ArrayList<ZgwRol>();
		var putRollen = new java.util.ArrayList<ZgwRol>();
		var deleteRollen = new java.util.ArrayList<ZgwRol>();
		var nothingRollen = new java.util.ArrayList<ZgwRol>();
		
		// alles wat we gehad hebben verwijderen we uit de lijst
		while(wasRollen.size() > 0 || wordtRollen.size() > 0 ) {
			if(wasRollen.size() == 0) {
				// er moet een rol bijkomen
				var wordtRol = wordtRollen.get(0);
				wordtRol.setZaak(zgwZaak.getUrl());
				postRollen.add(wordtRol);
				// deze hebben we gehad
				wordtRollen.remove(0);
			}
			else if(wordtRollen.size() == 0) {
				// deze rol moet verwijderd worden
				var wasRol = wasRollen.get(0);
				Boolean gevonden = false;
				for(ZgwRol zgwRol : zgwRollen) {
					if(zgwRol.betrokkeneType.equals(wasRol.betrokkeneType)
							&& 
							(		
									(zgwRol.betrokkeneIdentificatie.inpBsn == null && wasRol.betrokkeneIdentificatie.inpBsn == null)
									||
									zgwRol.betrokkeneIdentificatie.inpBsn.equals(wasRol.betrokkeneIdentificatie.inpBsn)
							)
							&& 
							(
									(zgwRol.betrokkeneIdentificatie.identificatie != null && wasRol.betrokkeneIdentificatie.identificatie != null)
									||
									zgwRol.betrokkeneIdentificatie.identificatie.equals(wasRol.betrokkeneIdentificatie.identificatie)
							) 
						)
					{
						// er is een rol teveel
						gevonden = true;
						deleteRollen.add(zgwRol);
						break;
					}
				}
				if(!gevonden) log.warn("rol niet terug gevonden in openzaak!");
				// deze hebben we gehad
				wasRollen.remove(0);				
			}
			else {
				var wordtRol = wordtRollen.get(0);
				Boolean gevonden = false;
				for(ZgwRol wasRol : wasRollen) {
					if(
							wasRol.betrokkeneType.equals(wordtRol.betrokkeneType)
							&& 
							(		
									(wasRol.betrokkeneIdentificatie.inpBsn == null && wordtRol.betrokkeneIdentificatie.inpBsn == null)
									||
									wasRol.betrokkeneIdentificatie.inpBsn.equals(wordtRol.betrokkeneIdentificatie.inpBsn)
							)
							&& 
							(
									(wasRol.betrokkeneIdentificatie.identificatie != null && wordtRol.betrokkeneIdentificatie.identificatie != null)
									||
									wasRol.betrokkeneIdentificatie.identificatie.equals(wordtRol.betrokkeneIdentificatie.identificatie)
							) 
						)
						{
							ZgwRol zgwRol = null;			
							for(ZgwRol rol : zgwRollen) {
								if(rol.betrokkeneType.equals(wordtRol.betrokkeneType)
									&& 
									(		
											(rol.betrokkeneIdentificatie.inpBsn == null && wordtRol.betrokkeneIdentificatie.inpBsn == null)
											||
											rol.betrokkeneIdentificatie.inpBsn.equals(wordtRol.betrokkeneIdentificatie.inpBsn)
									)
									&& 
									(
											(rol.betrokkeneIdentificatie.identificatie != null && wordtRol.betrokkeneIdentificatie.identificatie != null)
											||
											rol.betrokkeneIdentificatie.identificatie.equals(wordtRol.betrokkeneIdentificatie.identificatie)
									) 
								)
								{
									// er is een rol teveel
									zgwRol = rol;
									break;
								}
							}
							if(zgwRol == null) log.warn("rol niet terug gevonden in openzaak!");
							{
								// for runnable has to be from final
								final ZgwRol rol = zgwRol;
								
								var changed = new ChangeDetector();
								changed.compare(wasRol.betrokkeneIdentificatie.anpIdentificatie, wordtRol.betrokkeneIdentificatie.anpIdentificatie,  new Runnable() { public void run() { rol.betrokkeneIdentificatie.anpIdentificatie = wordtRol.betrokkeneIdentificatie.anpIdentificatie; } } );
								changed.compare(wasRol.betrokkeneIdentificatie.inpA_nummer, wordtRol.betrokkeneIdentificatie.inpA_nummer,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.inpA_nummer = wordtRol.betrokkeneIdentificatie.inpA_nummer; } } );
								changed.compare(wasRol.betrokkeneIdentificatie.inpgeslachtsnaam, wordtRol.betrokkeneIdentificatie.inpgeslachtsnaam,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.inpgeslachtsnaam = wordtRol.betrokkeneIdentificatie.inpgeslachtsnaam; } } );
								changed.compare(wasRol.betrokkeneIdentificatie.geslachtsnaam, wordtRol.betrokkeneIdentificatie.geslachtsnaam,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.geslachtsnaam = wordtRol.betrokkeneIdentificatie.geslachtsnaam; } } );
								changed.compare(wasRol.betrokkeneIdentificatie.voorvoegselGeslachtsnaam, wordtRol.betrokkeneIdentificatie.voorvoegselGeslachtsnaam,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.voorvoegselGeslachtsnaam = wordtRol.betrokkeneIdentificatie.voorvoegselGeslachtsnaam; } } );
								changed.compare(wasRol.betrokkeneIdentificatie.voornamen, wordtRol.betrokkeneIdentificatie.voornamen,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.voornamen = wordtRol.betrokkeneIdentificatie.voornamen; } } );							
								changed.compare(wasRol.betrokkeneIdentificatie.geslachtsaanduiding, wordtRol.betrokkeneIdentificatie.geslachtsaanduiding,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.geslachtsaanduiding = wordtRol.betrokkeneIdentificatie.geslachtsaanduiding; } } );							
								changed.compare(wasRol.betrokkeneIdentificatie.geboortedatum, wordtRol.betrokkeneIdentificatie.geboortedatum,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.geboortedatum = wordtRol.betrokkeneIdentificatie.geboortedatum; } } );
// TODO: verhuizingen!
//								changed.compare(wasRol.betrokkeneIdentificatie.verblijfsadres, wordtRol.betrokkeneIdentificatie.verblijfsadres,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.verblijfsadres = wordtRol.betrokkeneIdentificatie.verblijfsadres; } } );							
//								changed.compare(wasRol.betrokkeneIdentificatie.subVerblijfBuitenland, wordtRol.betrokkeneIdentificatie.subVerblijfBuitenland,  new Runnable() { public void run() {rol.betrokkeneIdentificatie.subVerblijfBuitenland = wordtRol.betrokkeneIdentificatie.subVerblijfBuitenland; } } );
								if(changed.isDirty()) {
									putRollen.add(rol);
								} else {
									nothingRollen.add(rol);
								}

							}
							// deze hebben we gehad
							if(!wasRollen.remove(wasRol)) throw new ZaakTranslatorException("fout bij het verwijderen van rol uit de rollenlijst");
							break;
						}
				}
				// deze hebben we gehad
				wordtRollen.remove(0);				
			}			
		}
		log.info("nothing rollen:" + nothingRollen.size());
		log.info("post rollen:" + postRollen.size());
		log.info("put rollen:" + putRollen.size());
		log.info("delete rollen:" + deleteRollen.size());
		
		for(ZgwRol rol : postRollen) { 
			zgwClient.postRol(rol);
		}					
		for(ZgwRol rol : putRollen) { 
			zgwClient.put(rol);
		}
		for(ZgwRol rol : deleteRollen) { 
			zgwClient.delete(rol);
		}				
	}	

	private void updateZaak(ZgwBasicZaak zgwZaak, ZdsZaak zdsWasZaak, ZdsZaak zdsWordtZaak) throws ZGWClientException, ZaakTranslatorException {
		var changed = new ChangeDetector();
		changed.compare(zdsWasZaak.identificatie, zdsWordtZaak.identificatie,  new Runnable() { public void run() {zgwZaak.identificatie = zdsWordtZaak.identificatie; } } );
		changed.compare(zdsWasZaak.omschrijving, zdsWordtZaak.omschrijving,  new Runnable() { public void run() {zgwZaak.omschrijving = zdsWordtZaak.omschrijving; } } );		
		changed.compare(zdsWasZaak.toelichting, zdsWordtZaak.toelichting,  new Runnable() { public void run() {zgwZaak.toelichting = zdsWordtZaak.toelichting; } } );
		changed.compare(zdsWasZaak.registratiedatum, zdsWordtZaak.registratiedatum,  new Runnable() { public void run() {zgwZaak.registratiedatum = getDateStringFromZdsDate(zdsWordtZaak.registratiedatum); } } );
		changed.compare(zdsWasZaak.startdatum, zdsWordtZaak.startdatum,  new Runnable() { public void run() {zgwZaak.startdatum = getDateStringFromZdsDate(zdsWordtZaak.startdatum); } } );
		changed.compare(zdsWasZaak.einddatum, zdsWordtZaak.einddatum,  new Runnable() { public void run() {zgwZaak.einddatum = getDateStringFromZdsDate(zdsWordtZaak.einddatum); } } );
		changed.compare(zdsWasZaak.einddatumGepland, zdsWordtZaak.einddatumGepland,  new Runnable() { public void run() {zgwZaak.einddatumGepland = getDateStringFromZdsDate(zdsWordtZaak.einddatumGepland); } } );
		if(changed.isDirty()) {
			zgwClient.put(zgwZaak);
		}
		
		var rollen = zgwClient.getRollenByZaak(zgwZaak.url);	
		updateZaak(zgwZaak, rollen, "Betrekking", zdsWasZaak.heeftBetrekkingOp, zdsWordtZaak.heeftBetrekkingOp);
		updateZaak(zgwZaak, rollen, "Belanghebbende", zdsWasZaak.heeftAlsBelanghebbende, zdsWordtZaak.heeftAlsBelanghebbende);
		updateZaak(zgwZaak, rollen, "Initiator", zdsWasZaak.heeftAlsInitiator, zdsWordtZaak.heeftAlsInitiator);
		updateZaak(zgwZaak, rollen, "Uitvoerende", zdsWasZaak.heeftAlsUitvoerende, zdsWordtZaak.heeftAlsUitvoerende);
		updateZaak(zgwZaak, rollen, "Verantwoordelijke", zdsWasZaak.heeftAlsVerantwoordelijke, zdsWordtZaak.heeftAlsVerantwoordelijke);		
	}	

	public ZgwBasicZaak updateZaak(ZakLk01 zakLk01) throws ZGWClientException, ZaakTranslatorException {
		var zdsWasZaak = zakLk01.object.get(0);
		var zdsWordtZaak = zakLk01.object.get(1);
		
		if (zdsWasZaak.identificatie.length() == 0) throw new ZaakTranslatorException("zaak identificatie is verplicht");		
		var zgwZaak = zgwClient.getZaakBasicByIdentificatie(zdsWasZaak.identificatie);		
		if(zgwZaak == null) throw new ZaakTranslatorException("zaak met identificatie: " + zdsWasZaak.identificatie + " niet gevonden");
						
		updateZaak(zgwZaak, zdsWasZaak, zdsWordtZaak);
	
		return zgwZaak;
	}	
	
	public ZgwCompleteZaak zdsZaakToZgwZaak(ZdsZaak zdsZaak) throws ZaakTranslatorException {

		var zgwZaak = new ZgwCompleteZaak();
		
		if (zdsZaak.identificatie.length() == 0) throw new ZaakTranslatorException("zaak identificatie is verplicht");
		zgwZaak.setIdentificatie(zdsZaak.identificatie);

		zgwZaak.setOmschrijving(zdsZaak.omschrijving);
		zgwZaak.setToelichting(zdsZaak.toelichting);

		zgwZaak.setRegistratiedatum(getDateStringFromZdsDate(zdsZaak.registratiedatum));
		zgwZaak.setStartdatum(getDateStringFromZdsDate(zdsZaak.startdatum));
		zgwZaak.setEinddatumGepland(getDateStringFromZdsDate(zdsZaak.einddatumGepland));
		zgwZaak.setArchiefnominatie(getZGWArchiefNominatie(zdsZaak.archiefnominatie));

		return zgwZaak;
	}

	public List<ZgwRol> getRollen(ZgwBasicZaak zgwzaak, ZdsRol zdsRol, String rolname) throws ZaakTranslatorException,  ZGWClientException {
		var zgwRoltype = zgwClient.getRolTypeByOmschrijving(zgwzaak.zaaktype, rolname);
		if(zgwRoltype == null) throw new ZaakTranslatorException("Geen roltype niet gevonden ZTC voor identificatie: '" + rolname);
				
		var rollen = new java.util.ArrayList<ZgwRol>();		
		if(zdsRol == null) return rollen;
		if(zdsRol.gerelateerde == null) return rollen;
		
		if(zdsRol.gerelateerde.natuurlijkPersoon != null) {
			var rol = new ZgwRol();
			rol.setRoltype(zgwRoltype.getUrl());
			rol.setRoltoelichting(rolname);
			var nps = getBetrokkeneIdentificatieNPS(zdsRol.gerelateerde.natuurlijkPersoon);
			rol.setBetrokkeneIdentificatie(nps);
			rol.setBetrokkeneType("natuurlijk_persoon");
			rollen.add(rol);
		}
		if(zdsRol.gerelateerde.medewerker != null) {
			var rol = new ZgwRol();
			rol.setRoltype(zgwRoltype.getUrl());
			rol.setRoltoelichting(rolname);
			var zdsMedewerker = getBetrokkeneIdentificatieMedewerker(zdsRol.gerelateerde.medewerker);
			rol.setBetrokkeneIdentificatie(zdsMedewerker);
			rol.setBetrokkeneType("medewerker");
			rollen.add(rol);			
		}
		if(zdsRol.gerelateerde.entiteittype == "CTP") {
			throw new ZaakTranslatorException("not yet implemented");
		}
		return rollen;
	}

	private String getRSIN(String gemeenteCode) throws ZaakTranslatorException {
		List<Organisatie> organisaties = this.configService.getConfiguratie().getOrganisaties();
		for (Organisatie organisatie : organisaties) {
			if (organisatie.getGemeenteCode().equals(gemeenteCode)) {
				return organisatie.getRSIN();
			}
		}
		throw new ZaakTranslatorException("Geen RSIN voor gemeentecode: '" + gemeenteCode + "' in config.json");
	}
	
	private String getDateStringFromZdsDate(String zdsDate) {
		if(zdsDate == null) return null;
		var year = zdsDate.substring(0, 4);
		var month = zdsDate.substring(4, 6);
		var day = zdsDate.substring(6, 8);
		return year + "-" + month + "-" + day;
	}	

	private String getDateStringFromZgwDate(String zgwDate) {
		if(zgwDate ==null) return null;
		return zgwDate.replace("-", "");
	}	
	
	
	private String getZGWArchiefNominatie(String archiefNominatie) {
		if(archiefNominatie == null) return null;
		if (archiefNominatie.toUpperCase().equals("J")) {
			return "vernietigen";
		} else {
			return "blijvend_bewaren";
		}
	}

	private String getZdsArchiefNominatie(String archiefNominatie) {
		if (archiefNominatie == null) return null;
		if(archiefNominatie.equals("vernietigen")) {
			return "J";
		} else {
			return "N";
		}
	}
	
	
	private ZgwBetrokkeneIdentificatie getBetrokkeneIdentificatieNPS(ZdsNatuurlijkPersoon natuurlijkPersoon)  {
		ZgwBetrokkeneIdentificatie nps = new ZgwBetrokkeneIdentificatie();
		nps.setInpBsn(natuurlijkPersoon.bsn);
		nps.setGeslachtsnaam(natuurlijkPersoon.geslachtsnaam);
		nps.setVoorvoegselGeslachtsnaam(natuurlijkPersoon.voorvoegselGeslachtsnaam);
		nps.setVoornamen(natuurlijkPersoon.voornamen);
		nps.setGeboortedatum(getDateStringFromZdsDate(natuurlijkPersoon.geboortedatum));
		nps.setGeslachtsaanduiding(natuurlijkPersoon.geslachtsaanduiding.toLowerCase());
		if(natuurlijkPersoon.verblijfsadres != null) {
			nps.verblijfsadres = new ZgwAdres();
			nps.verblijfsadres.aoaIdentificatie = natuurlijkPersoon.verblijfsadres.identificatie;			
			if(nps.verblijfsadres.aoaIdentificatie.length() == 0) {
				// ongewenst, maar we moeten toch wat
				nps.verblijfsadres.aoaIdentificatie = natuurlijkPersoon.verblijfsadres.postcode + natuurlijkPersoon.verblijfsadres.huisnummer + natuurlijkPersoon.verblijfsadres.huisletter +  natuurlijkPersoon.verblijfsadres.huisnummertoevoeging;
			}
			nps.verblijfsadres.wplWoonplaatsNaam = natuurlijkPersoon.verblijfsadres.woonplaatsnaam;
			nps.verblijfsadres.gorOpenbareRuimteNaam = natuurlijkPersoon.verblijfsadres.straatnaam;
			nps.verblijfsadres.aoaPostcode = natuurlijkPersoon.verblijfsadres.postcode;
			nps.verblijfsadres.aoaHuisnummer = natuurlijkPersoon.verblijfsadres.huisnummer;
			nps.verblijfsadres.aoaHuisletter = natuurlijkPersoon.verblijfsadres.huisletter;
			nps.verblijfsadres.aoaHuisnummertoevoeging = natuurlijkPersoon.verblijfsadres.huisnummertoevoeging;
			nps.verblijfsadres.inpLocatiebeschrijving = natuurlijkPersoon.verblijfsadres.locatiebeschrijving;
		}		
		return nps;
	}

	private ZgwBetrokkeneIdentificatie getBetrokkeneIdentificatieMedewerker(ZdsMedewerker zdsMedewerker)  {
		ZgwBetrokkeneIdentificatie zgwMedewerker = new ZgwBetrokkeneIdentificatie();
		zgwMedewerker.identificatie = zdsMedewerker.identificatie;
		zgwMedewerker.voorletters = zdsMedewerker.voorletters;
		zgwMedewerker.voorvoegselAchternaam = "";
		zgwMedewerker.achternaam = zdsMedewerker.achternaam;
		
		return zgwMedewerker;
	}
		
	/*
	public Document getZaakDetails(ZakLv01 zakLv01) throws Exception {
		ZgwZaak zgwZaak = getZaak(zakLv01.getIdentificatie());

		this.zaakTranslator.setZgwZaak(zgwZaak);
		this.zaakTranslator.zgwZaakToZakLa01();

		return this.zaakTranslator.getDocument();

	}
	*/
	public ZgwZaakInformatieObject voegZaakDocumentToe(EdcLk01 edcLk01) throws ZaakTranslatorException, ZGWClientException  {
		var zgwEnkelvoudigInformatieObject = zdsDocumentToZgwDocument(edcLk01);
		zgwEnkelvoudigInformatieObject  = this.zgwClient.addDocument(zgwEnkelvoudigInformatieObject);
		var zdsDocument = edcLk01.objects.get(0);
		var zaakIdentificatie = zdsDocument.isRelevantVoor.gerelateerde.identificatie;
		var zgwZaak = this.zgwClient.getZaakCompleteByIdentificatie(zaakIdentificatie);
		String zaakUrl = zgwZaak.url;
		ZgwZaakInformatieObject result = addZaakInformatieObject(zgwEnkelvoudigInformatieObject, zaakUrl);
		
		if(zdsDocument.isRelevantVoor.sttOmschrijving.length() > 0) {
			ZgwStatus zgwStatus = new ZgwStatus();
			zgwStatus.statustoelichting = zdsDocument.isRelevantVoor.sttOmschrijving;
			zgwStatus.datumStatusGezet = getDateTimeStringFromStufDate(zdsDocument.isRelevantVoor.staDatumStatusGezet, this.configService.getConfiguratie().getTimeOffsetHour());
			zgwStatus.zaak = zgwZaak.url;
			zgwStatus.statustype = zgwClient.getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype, zdsDocument.isRelevantVoor.sttVolgnummer, zgwStatus.statustoelichting).url;
			this.zgwClient.actualiseerZaakStatus(zgwStatus);
		}
		
		return result;
	}
	
	private ZgwZaakInformatieObject addZaakInformatieObject(ZgwEnkelvoudigInformatieObject doc, String zaakUrl) throws ZGWClientException {
			var zgwZaakInformatieObject = new ZgwZaakInformatieObject();
			zgwZaakInformatieObject.setZaak(zaakUrl);
			zgwZaakInformatieObject.setInformatieobject(doc.getUrl());
			zgwZaakInformatieObject.setTitel(doc.getTitel());
			ZgwZaakInformatieObject result = this.zgwClient.addDocumentToZaak(zgwZaakInformatieObject);
			return result;
	}
	
	public EdcLa01 getZaakDoumentLezen(EdcLv01 edcLv01) throws ZGWClientException, ZaakTranslatorException {
		ZgwEnkelvoudigInformatieObject document = this.zgwClient.getZgwEnkelvoudigInformatieObject(edcLv01.gelijk.identificatie);
	
		if(document == null) throw new ZaakTranslatorException("Geen zaakdocument niet gevonden voor identificatie: '" + edcLv01.gelijk.identificatie + "'");
		
		EdcLa01 edcLa01 = new EdcLa01();
		edcLa01.antwoord = new EdcLa01.Antwoord();
		edcLa01.antwoord.object = new ZdsZaakDocumentInhoud();
        edcLa01.antwoord.object.auteur = (document.auteur == null || document.auteur.equals("") ? null: document.auteur);
		edcLa01.antwoord.object.creatiedatum = document.creatiedatum;
		//edcLa01.antwoord.object.dctCategorie = document.beschrijving;
		edcLa01.antwoord.object.dctOmschrijving = document.beschrijving;
		edcLa01.antwoord.object.identificatie = document.identificatie;
		
		edcLa01.antwoord.object.bestandsnaam = document.bestandsnaam;
		edcLa01.antwoord.object.inhoud = zgwClient.getBas64Inhoud(document.inhoud);
		
		edcLa01.antwoord.object.link = document.url;
		edcLa01.antwoord.object.ontvangstdatum = document.ontvangstdatum;
        edcLa01.antwoord.object.status = (document.status.equals("")) ? null : document.status;


		edcLa01.antwoord.object.taal = document.taal;
		edcLa01.antwoord.object.titel = document.titel;
		edcLa01.antwoord.object.versie = document.versie;
		return edcLa01;
	}

	public ZgwCompleteZaak actualiseerZaakstatus(ZakLk01 zakLk01) throws ZGWClientException, ZaakTranslatorException {
		ZdsZaak zdsZaak = zakLk01.object.get(1);
		ZgwCompleteZaak zgwZaak = zgwClient.getZaakCompleteByIdentificatie(zdsZaak.identificatie);

		//this.zaakTranslator.setZakLk01(zakLk01);
		ZgwStatus zgwStatus = new ZgwStatus();
		/*
		zgwStatus.statustoelichting = zdsZaak.heeft.statustoelichting;
		zgwStatus.datumStatusGezet = getDateTimeStringFromStufDate(zdsZaak.heeft.datumStatusGezet, this.configService.getConfiguratie().getTimeOffsetHour());
		zgwStatus.zaak = zgwZaak.url;
		zgwStatus.statustype = zgwClient.getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype, zdsZaak.heeft.gerelateerde.volgnummer).url;
		 */
		zgwStatus.statustoelichting = zdsZaak.heeft.gerelateerde.omschrijving;
		zgwStatus.datumStatusGezet = getDateTimeStringFromStufDate(zdsZaak.heeft.datumStatusGezet, this.configService.getConfiguratie().getTimeOffsetHour());
		zgwStatus.zaak = zgwZaak.url;
		zgwStatus.statustype = zgwClient.getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype, zdsZaak.heeft.gerelateerde.volgnummer, zgwStatus.statustoelichting).url;
	
		this.zgwClient.actualiseerZaakStatus(zgwStatus);
		return zgwZaak;
	}
	
	
	/*
	public void zgwZaakToZakLa01() throws Exception {
		if (this.zgwZaak == null) {
			throw new Exception("ZGW zaak is null");
		}

		var zakLa01 = new ZakLa01Zaakdetails();
		zakLa01.setIdentificatie(this.zgwZaak.getIdentificatie());
		zakLa01.setOmschrijving(this.zgwZaak.getOmschrijving());
		zakLa01.setToelichting(this.zgwZaak.getToelichting());
		if (this.zgwZaak.getResultaat() != null) {
			// TODO Fetch resultaat for this zaak from ZGW API
			zakLa01.setResultaat("TODO", "Fetch resultaat for this zaak from ZGW API");
		} else {
			zakLa01.setEmptyResultaat();
		}
		zakLa01.setStartDatum(getStufDateFromDateString(this.zgwZaak.getStartdatum()));
		zakLa01.setRegistratieDatum(getStufDateFromDateString(this.zgwZaak.getRegistratiedatum()));
		zakLa01.setPublicatieDatum(getStufDateFromDateString(this.zgwZaak.getPublicatiedatum()));
		zakLa01.setEinddatumGepland(getStufDateFromDateString(this.zgwZaak.getEinddatumGepland()));
		zakLa01.setUiterlijkeEinddatum(getStufDateFromDateString(this.zgwZaak.getUiterlijkeEinddatumAfdoening()));
		zakLa01.setEinddatum(getStufDateFromDateString(this.zgwZaak.getEinddatum()));
		zakLa01.setArchiefNominatie(getZDSArchiefNominatie(this.zgwZaak.getArchiefnominatie()));
		zakLa01.setDatumVernietigingDossier(getStufDateFromDateString(this.zgwZaak.getArchiefactiedatum()));
		var zaakType = getZaakTypeByZGWZaakType(this.zgwZaak.getZaaktype());
		zakLa01.setZaakTypeOmschrijving(zaakType.getZaakTypeOmschrijving());
		zakLa01.setZaakTypeCode(zaakType.getCode());
		zakLa01.setZaakTypeIngangsDatumObject(zaakType.getIngangsdatumObject());

		this.document = zakLa01.getDocument();
	}
	*/
	
	/*
	public EdcLa01 getEdcLa01FromZgwEnkelvoudigInformatieObject(ZgwEnkelvoudigInformatieObject document) {
		EdcLa01 edcLa01 = new EdcLa01();
		edcLa01.antwoord = new EdcLa01.Antwoord();
		edcLa01.antwoord.object = new EdcLa01.Object();
        edcLa01.antwoord.object.auteur = (document.auteur.equals("") ? null: document.auteur);
		edcLa01.antwoord.object.creatiedatum = document.creatiedatum;
		edcLa01.antwoord.object.dctCategorie = document.beschrijving;
		edcLa01.antwoord.object.dctOmschrijving = document.beschrijving;
		edcLa01.antwoord.object.identificatie = document.identificatie;
		edcLa01.antwoord.object.inhoud = document.inhoud;
		edcLa01.antwoord.object.link = document.url;
		edcLa01.antwoord.object.ontvangstdatum = document.ontvangstdatum;
        edcLa01.antwoord.object.status = (document.status.equals("")) ? null : document.status;


		edcLa01.antwoord.object.taal = document.taal;
		edcLa01.antwoord.object.titel = document.titel;
		edcLa01.antwoord.object.versie = document.versie;

		return edcLa01;
	}
	*/
	
	
	public ZgwEnkelvoudigInformatieObject zdsDocumentToZgwDocument(EdcLk01 edcLk01) throws ZaakTranslatorException, ZGWClientException {
		/*
		"documentTypes": [
		          		{
		          			"documentType": "https://openzaak.local/catalogi/api/v1/informatieobjecttypen/b380e35f-3b10-4d76-81b5-58f8013dca4a",
		          			"omschrijving": "Overig stuk inkomend"
		          		}
		          	]		            		
		*/
		//var informatieObjectType = this.configService.getConfiguratie().getDocumentTypes().get(0).getDocumentType();
		ZdsDocument document =  edcLk01.objects.get(0);
		var informatieObjectType = this.zgwClient.getZgwInformatieObjectTypeByOmschrijving(document.omschrijving);
		if(informatieObjectType == null) throw new ZaakTranslatorException("Geen informatieobjectype gevonden in  ZTC voor omschrijving: '" + document.omschrijving + "");
	
		var o = edcLk01.objects.get(0);
		var eio = new ZgwEnkelvoudigInformatieObject();
		eio.setIdentificatie(o.identificatie);
		eio.setBronorganisatie(getRSIN(edcLk01.stuurgegevens.zender.organisatie));
		eio.setCreatiedatum(getDateStringFromZdsDate(o.creatiedatum));
		eio.setTitel(o.titel);
		eio.setVertrouwelijkheidaanduiding(o.vertrouwelijkAanduiding.toLowerCase());
		eio.setAuteur(o.auteur);
		eio.setTaal(o.taal);
		eio.setFormaat(o.formaat);
		eio.setInhoud(o.inhoud.value);
		eio.setInformatieobjecttype(informatieObjectType.url);
		eio.setBestandsnaam(o.inhoud.bestandsnaam);

		return eio;
	}
	
	/*
	*/
	
	/*
	public RolNPS getRolUitvoerende() throws ZaakTranslatorException {
		var z = this.zakLk01.objects.get(0);
		if (z.heeftAlsUitvoerende != null) {			
			var nps =  getBetrokkeneIdentificatieNPS(z.heeftAlsUitvoerende.gerelateerde.natuurlijkPersoon);
			var rol = new RolNPS();
			rol.setBetrokkeneIdentificatieNPS(nps);
			rol.setBetrokkeneType("natuurlijk_persoon");
			rol.setRoltoelichting("Inititator");
			if(true) throw new ZaakTranslatorException("wat is de gedacht hier achter?");
			rol.setRoltype(getZaakTypeByZDSCode(z.isVan.gerelateerde.code).initiatorRolTypeUrl);
			return rol;
		} 
		else 
		{
			return null;
		}
	}	
	*/
	
	/*
	private BetrokkeneIdentificatieNPS getBetrokkeneIdentificatieNPS(NatuurlijkPersoon natuurlijkPersoon)  {
			BetrokkeneIdentificatieNPS nps = new BetrokkeneIdentificatieNPS();
			nps.setInpBsn(natuurlijkPersoon.bsn);
			nps.setGeslachtsnaam(natuurlijkPersoon.geslachtsnaam);
			nps.setVoorvoegselGeslachtsnaam(natuurlijkPersoon.voorvoegselGeslachtsnaam);
			nps.setVoornamen(natuurlijkPersoon.voornamen);
			nps.setGeboortedatum(getDateStringFromStufDate(natuurlijkPersoon.geboortedatum));
			nps.setGeslachtsaanduiding(natuurlijkPersoon.geslachtsaanduiding.toLowerCase());
			return nps;
	}
	*/
	
	/*
	private String getStufDateFromDateString(String dateString) {
		if (dateString == null) {
			return null;
		}
		var year = dateString.substring(0, 4);
		var month = dateString.substring(5, 7);
		var day = dateString.substring(8, 10);
		return year + month + day;
	}
	*/
	
	/*
	private String getDateStringFromStufDate(String stufDate) {

		var year = stufDate.substring(0, 4);
		var month = stufDate.substring(4, 6);
		var day = stufDate.substring(6, 8);
		return year + "-" + month + "-" + day;
	}
	*/
	
	private String getDateTimeStringFromStufDate(String stufDate) throws ZaakTranslatorException {
		return getDateTimeStringFromStufDate(stufDate, 0);
	}
	
	private String getDateTimeStringFromStufDate(String stufDate, int offsetHour) throws ZaakTranslatorException {
		/*
		var year = stufDate.substring(0, 4);
		var month = stufDate.substring(4, 6);
		var day = stufDate.substring(6, 8);
		var hours = stufDate.substring(8, 10);
		var minutes = stufDate.substring(10, 12);
		var seconds = stufDate.substring(12, 14);
		var milliseconds = stufDate.substring(14);
		return year + "-" + month + "-" + day + "T" + hours + ":" + minutes + ":" + seconds + "." + milliseconds + "Z";
		*/
		
		// 20200311 
		if(stufDate.length() == 8) {
			stufDate += "000000000";
		}
		
		var zdsdate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		var zgwdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			var date = zdsdate.parse(stufDate);
			date.setTime(date.getTime() + (offsetHour * 60 * 60 * 1000));
			return zgwdate.format(date);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			  throw new ZaakTranslatorException("ongeldige stuf-datum: '" + stufDate + "'");
		}
	}

	public ZakLa01 getZaakDetails(ZakLv01 zakLv01) throws ZaakTranslatorException, ZGWClientException {
		if (zakLv01.gelijk != null && zakLv01.gelijk.identificatie != null) {
			var zgwZaak = zgwClient.getZaakCompleteByIdentificatie(zakLv01.gelijk.identificatie);
			if(zgwZaak == null) throw new ZaakTranslatorException("Geen zaak niet gevonden voor identificatie: '" + zakLv01.gelijk.identificatie + "'");
			var zdsZaak = new ZdsZaak();
			zdsZaak.identificatie = zgwZaak.identificatie;
			zdsZaak.omschrijving = zgwZaak.omschrijving;
			zdsZaak.toelichting = zgwZaak.toelichting;
			zdsZaak.toelichting = zgwZaak.toelichting;
			
			var zgwZaakType = zgwClient.getZaakTypeByUrl(zgwZaak.zaaktype);
			if(zgwZaakType == null) throw new ZaakTranslatorException("Geen zaaktype niet gevonden voor identificatie: '" + zgwZaak.zaaktype + "'");
			
			zdsZaak.isVan = new GerelateerdeWrapper();
			zdsZaak.isVan.gerelateerde = new GerelateerdeRol(); 
			zdsZaak.isVan.gerelateerde.code = zgwZaakType.identificatie;			
			zdsZaak.isVan.gerelateerde.omschrijving = zgwZaakType.omschrijving;			

			zdsZaak.registratiedatum = getDateStringFromZgwDate(zgwZaak.registratiedatum);
			zdsZaak.startdatum = getDateStringFromZgwDate(zgwZaak.startdatum);
			zdsZaak.einddatumGepland = getDateStringFromZgwDate(zgwZaak.einddatumGepland);
			zdsZaak.einddatum = getDateStringFromZgwDate(zgwZaak.einddatum);
			zdsZaak.archiefnominatie = getZdsArchiefNominatie(zgwZaak.einddatum);
						
			log.warn("rollen enzo moeten hier nog!");
			var zgwRollen = zgwClient.getRollenByZaak(zgwZaak.url);
			for(ZgwRol zgwRol: zgwRollen) {
				var zdsRol = new ZdsRol();
				zdsRol.gerelateerde = new GerelateerdeRol();				
				// relatie van de rol
				switch(zgwRol.betrokkeneType) {
				  case "natuurlijk_persoon":
					  ZgwNatuurlijkPersoon zgwNatuurlijkPersoon = null;
					  if(zgwRol.betrokkene.length() > 0) {
						  zgwNatuurlijkPersoon = zgwClient.getNatuurlijkPersoonByUrl(zgwRol.betrokkene);
					  }
					  else {
						  Gson gson = new Gson();
						  String json = gson.toJson(zgwRol.betrokkeneIdentificatie);
						  zgwNatuurlijkPersoon = gson.fromJson(json, ZgwNatuurlijkPersoon.class);						  
					  }						  
					  zdsRol.gerelateerde.natuurlijkPersoon = new ZdsNatuurlijkPersoon();
					  zdsRol.gerelateerde.natuurlijkPersoon.bsn = zgwNatuurlijkPersoon.inpBsn;
					  zdsRol.gerelateerde.natuurlijkPersoon.geslachtsnaam = zgwNatuurlijkPersoon.geslachtsnaam;
					  zdsRol.gerelateerde.natuurlijkPersoon.voorvoegselGeslachtsnaam = zgwNatuurlijkPersoon.voorvoegselGeslachtsnaam;
					  zdsRol.gerelateerde.natuurlijkPersoon.voorletters = zgwNatuurlijkPersoon.voorletters;
					  zdsRol.gerelateerde.natuurlijkPersoon.voornamen =  zgwNatuurlijkPersoon.voornamen;
					  zdsRol.gerelateerde.natuurlijkPersoon.geslachtsaanduiding = zgwNatuurlijkPersoon.geslachtsaanduiding;					  					  
					  zdsRol.gerelateerde.natuurlijkPersoon.geboortedatum = getDateStringFromZgwDate(zgwNatuurlijkPersoon.geboortedatum);
					  
					  break;
				  case "niet_natuurlijk_persoon":
					  log.warn("todo: niet_natuurlijk_persoon not yet implemented!");
					  break;
				  case "vestiging":
					  log.warn("todo: vestiging not yet implemented!");
					  break;
				  case "organisatorische_eenheid":
					  log.warn("todo: organisatorische_eenheidnot yet implemented!");
					  break;
				  case "medewerker":
					  ZgwBetrokkeneIdentificatie zgwMedewerker = null;
					  if(zgwRol.betrokkene.length() > 0) {
						  zgwMedewerker = zgwClient.getMedewerkerByUrl(zgwRol.betrokkene);
					  }
					  else {
						  Gson gson = new Gson();
						  String json = gson.toJson(zgwRol.betrokkeneIdentificatie);
						  zgwMedewerker = gson.fromJson(json, ZgwBetrokkeneIdentificatie.class);						  
					  }						  
					  zdsRol.gerelateerde.medewerker = new ZdsMedewerker();
					  zdsRol.gerelateerde.medewerker.identificatie = zgwMedewerker.identificatie;
					  zdsRol.gerelateerde.medewerker.achternaam = zgwMedewerker.achternaam;
					  zdsRol.gerelateerde.medewerker.voorletters = zgwMedewerker.voorletters;
					  break;
				  default:
					  throw new ZaakTranslatorException("onbekende betrokkeneType: '" + zgwRol.betrokkeneType+ "'");
				}
				// type rol
				switch(zgwRol.roltoelichting) {
				  case "Betrekking":
					  zdsZaak.heeftBetrekkingOp = zdsRol;
					  break;
				  case "Belanghebbende":
					  zdsZaak.heeftAlsBelanghebbende = zdsRol;
					  break;
				  case "Initiator":
					  zdsZaak.heeftAlsInitiator= zdsRol;
					  break;
				  case "Uitvoerende":
					  zdsZaak.heeftAlsUitvoerende= zdsRol;
					  break;
				  case "Verantwoordelijke":
					  zdsZaak.heeftAlsVerantwoordelijke= zdsRol;
					    break;
				  default:
					  throw new ZaakTranslatorException("onbekende rol met omschrijving: '" + zgwRol.roltoelichting + "'");				
				}
			}
			var result = new ZakLa01();
			result.antwoord = new ZakLa01.Antwoord();
			result.antwoord.object = zdsZaak;
			return result;
		}
		throw new ZaakTranslatorException("niet ondersteunde vraag in geefZaakDetails");
	}

	/*
	public Document getLijstZaakdocumenten(ZakLv01 zakLv01) throws Exception {
		ZgwZaak zgwZaak = getZaak(zakLv01.getIdentificatie());

		Map<String, String> parameters = new HashMap();
		parameters.put("zaak", zgwZaak.getUrl());

		var zaakInformatieObjecten = this.zgwClient.getLijstZaakDocumenten(parameters);

		this.zaakTranslator.setZgwEnkelvoudigInformatieObjectList(zaakInformatieObjecten);
		this.zaakTranslator.zgwEnkelvoudingInformatieObjectenToZSDLijstZaakDocumenten();

		return this.zaakTranslator.getDocument();

	}
	*/	
	/*
	public void zgwEnkelvoudingInformatieObjectenToZSDLijstZaakDocumenten() {
		var zakLa01 = new ZakLa01LijstZaakdocumenten();

		this.zgwEnkelvoudigInformatieObjectList.forEach(document -> {
			zgwDocumentToZgwDocument(zakLa01, document);
		});

		this.document = zakLa01.getDocument();
	}
	*/	
	/*
	private void zgwDocumentToZgwDocument(ZakLa01LijstZaakdocumenten zakLa01, ZgwEnkelvoudigInformatieObject document) {
		HeeftRelevantEDC heeftRelevantEDC = new HeeftRelevantEDC();
		heeftRelevantEDC.setIdentificatie(document.getIdentificatie());
		heeftRelevantEDC.setDctOmschrijving(getDocumentTypeOmschrijving(document.getInformatieobjecttype()));
		heeftRelevantEDC.setCreatieDatum(getStufDateFromDateString(document.getCreatiedatum()));
		heeftRelevantEDC.setOntvangstDatum(getStufDateFromDateString(document.getOntvangstdatum()));
		heeftRelevantEDC.setTitel(document.getTitel());
		heeftRelevantEDC.setBeschrijving(document.getBeschrijving());
		heeftRelevantEDC.setFormaat(document.getFormaat());
		heeftRelevantEDC.setTaal(document.getTaal());
		heeftRelevantEDC.setVersie(document.getVersie());
		heeftRelevantEDC.setStatus(document.getStatus());
		heeftRelevantEDC.setVerzendDatum(getStufDateFromDateString(document.getVerzenddatum()));
		heeftRelevantEDC.setVertrouwelijkAanduiding(document.getVertrouwelijkheidaanduiding().toUpperCase());
		heeftRelevantEDC.setAuteur(document.getAuteur());
		heeftRelevantEDC.setLink(document.getUrl());
		zakLa01.addHeeftRelevant(heeftRelevantEDC);
	}
	*/	
	public ZakLa01LijstZaakdocumenten geefLijstZaakdocumenten(ZakLv01 zakLv01) throws ZaakTranslatorException, ZGWClientException {
		if (zakLv01.gelijk != null && zakLv01.gelijk.identificatie != null) {
			var zgwZaak = zgwClient.getZaakCompleteByIdentificatie(zakLv01.gelijk.identificatie);
			if(zgwZaak == null) throw new ZaakTranslatorException("Geen zaak niet gevonden voor identificatie: '" + zakLv01.gelijk.identificatie + "'");
			
			log.info("zaak gevonden:" + zgwZaak.url);			
			List<ZgwZaakInformatieObject> zgwZaakInformatieObjecten = zgwClient.getZaakInformatieObjectenByZaakUrl(zgwZaak.url);
			List<ZdsRelatieZaakDocument> zdsRelatieDocumenten = new ArrayList<>();

			
			for(ZgwZaakInformatieObject zwgZaakInformatieObject : zgwZaakInformatieObjecten) {
				ZgwInformatieObject zgwInformatieObject = zgwClient.getInformatieObjectByUrl(zwgZaakInformatieObject.informatieobject);				
				
				var zdsZaakDocument = new ZdsZaakDocument();
				zdsZaakDocument.entiteittype = "EDC";
				zdsZaakDocument.identificatie = zgwInformatieObject.identificatie;
				
				if (zgwInformatieObject.beschrijving != null  && zgwInformatieObject.beschrijving != "") zdsZaakDocument.dctOmschrijving = zgwInformatieObject.beschrijving;
				
				//zdsZaakDocument.dctCategorie= zgwInformatieObject.identificatie;
				zdsZaakDocument.creatiedatum = zgwInformatieObject.creatiedatum.replace("-","");
				zdsZaakDocument.ontvangstdatum  = zgwInformatieObject.ontvangstdatum;
				zdsZaakDocument.titel  = zgwInformatieObject.titel;
				zdsZaakDocument.taal  = zgwInformatieObject.taal;
				zdsZaakDocument.versie  = zgwInformatieObject.versie;
				zdsZaakDocument.status  = zgwInformatieObject.status;
				zdsZaakDocument.vertrouwelijkAanduiding = zgwInformatieObject.vertrouwelijkheidaanduiding.toUpperCase();
				zdsZaakDocument.auteur  = zgwInformatieObject.auteur;
				zdsZaakDocument.link  = zgwInformatieObject.url;

				var zdsRelatieZaakDocument = new ZdsRelatieZaakDocument();
				zdsRelatieZaakDocument.entiteittype = "ZAKEDC";
				zdsRelatieZaakDocument.gerelateerde =  zdsZaakDocument;
				zdsRelatieDocumenten.add(zdsRelatieZaakDocument);
			}
			
			var result = new ZakLa01LijstZaakdocumenten();			
			result.antwoord = new nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLa01LijstZaakdocumenten.Antwoord();
			result.antwoord.object = new ZdsZaak();
			result.antwoord.object.entiteittype = "ZAK";
			result.antwoord.object.heeftRelevant = zdsRelatieDocumenten;
			
			return result;
			
			/*
			var zdsZaak = new ZdsZaak();
			zdsZaak.identificatie = zgwZaak.identificatie;
			zdsZaak.omschrijving = zgwZaak.omschrijving;
			zdsZaak.toelichting = zgwZaak.toelichting;
			zdsZaak.toelichting = zgwZaak.toelichting;
			
			var zgwZaakType = zgwClient.getZaakTypeByUrl(zgwZaak.zaaktype);
			if(zgwZaakType == null) throw new ZaakTranslatorException("Geen zaaktype niet gevonden voor identificatie: '" + zgwZaak.zaaktype + "'");
			
			zdsZaak.isVan = new GerelateerdeWrapper();
			zdsZaak.isVan.gerelateerde = new Gerelateerde(); 
			zdsZaak.isVan.gerelateerde.code = zgwZaakType.identificatie;			
			zdsZaak.isVan.gerelateerde.omschrijving = zgwZaakType.omschrijving;			

			zdsZaak.registratiedatum = getDateStringFromZgwDate(zgwZaak.registratiedatum);
			zdsZaak.startdatum = getDateStringFromZgwDate(zgwZaak.startdatum);
			zdsZaak.einddatumGepland = getDateStringFromZgwDate(zgwZaak.einddatumGepland);
			zdsZaak.einddatum = getDateStringFromZgwDate(zgwZaak.einddatum);
			zdsZaak.archiefnominatie = getZdsArchiefNominatie(zgwZaak.einddatum);
						
			log.warn("rollen enzo moeten hier nog!");
			var zgwRollen = zgwClient.getRollenByZaak(zgwZaak.url);
			for(Rol zgwRol: zgwRollen) {
				var zdsRol = new ZdsRol();
				zdsRol.gerelateerde = new Gerelateerde();				
				// relatie van de rol
				switch(zgwRol.betrokkeneType) {
				  case "natuurlijk_persoon":
					  ZgwNatuurlijkPersoon zgwNatuurlijkPersoon = null;
					  if(zgwRol.betrokkene.length() > 0) {
						  zgwNatuurlijkPersoon = zgwClient.getNatuurlijkPersoonByUrl(zgwRol.betrokkene);
					  }
					  else {
						  Gson gson = new Gson();
						  String json = gson.toJson(zgwRol.betrokkeneIdentificatie);
						  zgwNatuurlijkPersoon = gson.fromJson(json, ZgwNatuurlijkPersoon.class);						  
					  }						  
					  zdsRol.gerelateerde.natuurlijkPersoon = new ZdsNatuurlijkPersoon();
					  zdsRol.gerelateerde.natuurlijkPersoon.bsn = zgwNatuurlijkPersoon.inpBsn;
					  zdsRol.gerelateerde.natuurlijkPersoon.geslachtsnaam = zgwNatuurlijkPersoon.geslachtsnaam;
					  zdsRol.gerelateerde.natuurlijkPersoon.voorvoegselGeslachtsnaam = zgwNatuurlijkPersoon.voorvoegselGeslachtsnaam;
					  zdsRol.gerelateerde.natuurlijkPersoon.voorletters = zgwNatuurlijkPersoon.voorletters;
					  zdsRol.gerelateerde.natuurlijkPersoon.voornamen =  zgwNatuurlijkPersoon.voornamen;
					  zdsRol.gerelateerde.natuurlijkPersoon.geslachtsaanduiding = zgwNatuurlijkPersoon.geslachtsaanduiding;					  					  
					  zdsRol.gerelateerde.natuurlijkPersoon.geboortedatum = getDateStringFromZgwDate(zgwNatuurlijkPersoon.geboortedatum);
					  break;
				  case "niet_natuurlijk_persoon":
					  log.warn("todo: niet_natuurlijk_persoon not yet implemented!");
					  break;
				  case "vestiging":
					  log.warn("todo: vestiging not yet implemented!");
					  break;
				  case "organisatorische_eenheid":
					  log.warn("todo: organisatorische_eenheidnot yet implemented!");
					  break;
				  case "medewerker":
					  ZgwMedewerker zgwMedewerker = null;
					  if(zgwRol.betrokkene.length() > 0) {
						  zgwMedewerker = zgwClient.getMedewerkerByUrl(zgwRol.betrokkene);
					  }
					  else {
						  Gson gson = new Gson();
						  String json = gson.toJson(zgwRol.betrokkeneIdentificatie);
						  zgwMedewerker = gson.fromJson(json, ZgwMedewerker.class);						  
					  }						  
					  zdsRol.gerelateerde.medewerker = new ZdsMedewerker();
					  zdsRol.gerelateerde.medewerker.identificatie = zgwMedewerker.identificatie;
					  zdsRol.gerelateerde.medewerker.achternaam = zgwMedewerker.achternaam;
					  zdsRol.gerelateerde.medewerker.voorletters = zgwMedewerker.voorletters;
					  break;
				  default:
					  throw new ZaakTranslatorException("onbekende betrokkeneType: '" + zgwRol.betrokkeneType+ "'");
				}
				// type rol
				switch(zgwRol.roltoelichting) {
				  case "Betrekking":
					  zdsZaak.heeftBetrekkingOp = zdsRol;
					  break;
				  case "Belanghebbende":
					  zdsZaak.heeftAlsBelanghebbende = zdsRol;
					  break;
				  case "Initiator":
					  zdsZaak.heeftAlsInitiator= zdsRol;
					  break;
				  case "Uitvoerende":
					  zdsZaak.heeftAlsUitvoerende= zdsRol;
					  break;
				  case "Verantwoordelijke":
					  zdsZaak.heeftAlsVerantwoordelijke= zdsRol;
					    break;
				  default:
					  throw new ZaakTranslatorException("onbekende rol met omschrijving: '" + zgwRol.roltoelichting + "'");				
				}
			}
			var result = new EdcLa01();
			result.antwoord = new Antwoord();
			result.antwoord.object = zdsZaak;
			return result;
			*/
		}
		throw new ZaakTranslatorException("niet ondersteunde vraag in geefLijstZaakdocumenten");
	}

	public void replicateZds2ZgwZaak(RequestResponseCycle session, String zaakidentificatie) throws ZGWClientException, ZaakTranslatorException {
		var zgwZaak = zgwClient.getZaakBasicByIdentificatie(zaakidentificatie);
		if(zgwZaak != null)  {
			log.info("replication: no need to copy, zaak with id #" + zaakidentificatie + " already in zgw");
			// nothing to do here
			return;
		}
		throw new ZaakTranslatorException("replicatie voor zaakdocmenten nog niet ondersteund!\n\tzaakidentificatie:" + zaakidentificatie);	}

	public void replicateZds2ZgwDocument(RequestResponseCycle session, String zaakdocmentidentificatie) throws ZGWClientException, ZaakTranslatorException {
		var zgwZaakDocument= zgwClient.getZgwEnkelvoudigInformatieObject(zaakdocmentidentificatie);
		if(zgwZaakDocument != null)  {
			log.info("replication: no need to copy zaakdocument, zaakdocument with id #" + zaakdocmentidentificatie + " already in zgw");
			// nothing to do here
			return;
		}
		throw new ZaakTranslatorException("replicatie voor zaakdocmenten nog niet ondersteund!\n\tzaakdocumentidentificatie:" + zaakdocmentidentificatie);
	}
	
	/*
	private String getZGWArchiefNominatie(String archiefNominatie) {
		if (archiefNominatie.toUpperCase().equals("J")) {
			return "vernietigen";
		} else {
			return "blijvend_bewaren";
		}
	}
	*/
	
	/*
	private String getZDSArchiefNominatie(String archiefNominatie) {
		if (archiefNominatie.toUpperCase().equals("vernietigen")) {
			return "J";
		} else {
			return "N";
		}
	}
	*/
	
	/*
	private ZaakType getZaakTypeByZGWZaakType(String zgwZaakType) {
		List<ZaakType> zaakTypes = this.configService.getConfiguratie().getZaakTypes();
		for (ZaakType zaakType : zaakTypes) {
			if (zaakType.getZaakType().equals(zgwZaakType)) {
				return zaakType;
			}
		}
		return null;
	}
	*/
	
	/*
	private String getDocumentTypeOmschrijving(String documentType) {
		List<DocumentType> documentTypes = this.configService.getConfiguratie().getDocumentTypes();
		for (DocumentType type : documentTypes) {
			if (type.getDocumentType().equals(documentType)) {
				return type.getOmschrijving();
			}
		}
		return null;
	}
	*/
	
	/*
//    public ZaakType getZaakTypeByZDSCode(String catalogus, String zaakTypeCode) throws ZaakTranslatorException {
	public ZaakType getZaakTypeByZDSCode(String zaakTypeCode) throws ZaakTranslatorException {
		// TODO: request from OpenZaak!
		log.warn("Retrieving the zaaktype NOT FROM ZTC but from config.json for zaaktypecode:" + zaakTypeCode);
		List<ZaakType> zaakTypes = this.configService.getConfiguratie().getZaakTypes();
		for (ZaakType zaakType : zaakTypes) {
			if (zaakType.getCode().equals(zaakTypeCode)) {
				return zaakType;
			}
		}
		// throw new ZaakTranslatorException("Geen zaaktypeurl voor zaaktype: '" +
		// zaakTypeCode + "' in catalogus:" + catalogus);
		throw new ZaakTranslatorException("Geen zaaktypeurl voor zaaktype: '" + zaakTypeCode);
	}
	*/

	/*
	private String getRSIN(String gemeenteCode) throws ZaakTranslatorException {
		List<Organisatie> organisaties = this.configService.getConfiguratie().getOrganisaties();
		for (Organisatie organisatie : organisaties) {
			if (organisatie.getGemeenteCode().equals(gemeenteCode)) {
				return organisatie.getRSIN();
			}
		}
		throw new ZaakTranslatorException("Geen RSIN voor gemeentecode: '" + gemeenteCode + "' in config.json");
	}
	*/
	/*
	public ZgwStatus getZgwStatus(ZakLk01_v2.ZdsZaak object) {
		ZgwStatus zgwStatus = new ZgwStatus();
		zgwStatus.statustoelichting = object.heeft.statustoelichting;
		zgwStatus.datumStatusGezet = getDateTimeStringFromStufDate(object.heeft.datumStatusGezet);
		return zgwStatus;
	}
	*/
}
