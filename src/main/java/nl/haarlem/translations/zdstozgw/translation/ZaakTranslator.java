package nl.haarlem.translations.zdstozgw.translation;


import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.BG;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.aspectj.weaver.Dump.INode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.google.gson.Gson;

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
import nl.haarlem.translations.zdstozgw.translation.zgw.model.Rol;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwBetrokkeneIdentificatie;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.Rol;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwEnkelvoudigInformatieObject;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwInformatieObject;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwInformatieObjectType;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwNatuurlijkPersoon;
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
		var rollen = new java.util.ArrayList<Rol>();		
		if (zdsZaak.heeftBetrekkingOp != null) rollen.addAll(getRollen(zgwZaak, zdsZaak.heeftBetrekkingOp, "Betrekking"));
		if (zdsZaak.heeftAlsBelanghebbende != null) rollen.addAll(getRollen(zgwZaak, zdsZaak.heeftAlsBelanghebbende, "Belanghebbende"));
		if (zdsZaak.heeftAlsInitiator != null) rollen.addAll(getRollen(zgwZaak, zdsZaak.heeftAlsInitiator, "Initiator"));
		if (zdsZaak.heeftAlsUitvoerende != null) rollen.addAll(getRollen(zgwZaak, zdsZaak.heeftAlsUitvoerende, "Uitvoerende"));
		if (zdsZaak.heeftAlsVerantwoordelijke != null) rollen.addAll(getRollen(zgwZaak, zdsZaak.heeftAlsVerantwoordelijke, "Verantwoordelijke"));		
		for(Rol rol : rollen) {
			rol.setZaak(zgwZaak.getUrl());
			zgwClient.postRolNPS(rol);
		}	
		
		// zet de status
		if(zdsZaak.heeft.gerelateerde.volgnummer != null) {
			var zgwZaakStatusNummer = zgwClient.getZaakTypeByIdentiticatie(zdsZaak.heeft.gerelateerde.volgnummer);
			var zgwZaakStatusCode = zgwClient.getZaakTypeByIdentiticatie(zdsZaak.heeft.gerelateerde.code);
			var zgwZaakStatusOmschrijving = zgwClient.getZaakTypeByIdentiticatie(zdsZaak.heeft.gerelateerde.omschrijving);

			
			ZgwStatus zgwStatus = new ZgwStatus();
			zgwStatus.statustoelichting = zdsZaak.heeft.statustoelichting;
			zgwStatus.datumStatusGezet = getDateTimeStringFromStufDate(zdsZaak.heeft.datumStatusGezet);
			zgwStatus.zaak = zgwZaak.url;
			zgwStatus.statustype = zgwClient.getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype, Integer.valueOf(zdsZaak.heeft.gerelateerde.volgnummer)).url;

			this.zgwClient.actualiseerZaakStatus(zgwStatus);			
		}
		if(zgwZaakType == null) throw new ZaakTranslatorException("Geen zaaktype niet gevonden ZTC voor identificatie: '" + zdsZaak.isVan.gerelateerde.code + "'");
		zgwZaak.setZaaktype(zgwZaakType.getUrl());

		
		return zgwZaak;
	}

	public ZgwCompleteZaak updateZaak(ZakLk01 zakLk01) throws ZGWClientException, ZaakTranslatorException {
		var zdsWasZaak = zakLk01.object.get(0);
		var zdsWordZaak = zakLk01.object.get(1);
		
		if (zdsWordZaak.identificatie.length() == 0) throw new ZaakTranslatorException("zaak identificatie is verplicht");		
		var zgwZaak = zgwClient.getZaakByIdentificatie(zdsWasZaak.identificatie);		
		if(zgwZaak == null) throw new ZaakTranslatorException("zaak met identificatie: " + zdsWasZaak.identificatie + " niet gevonden");
		
		zgwZaak.setIdentificatie(zdsWordZaak.identificatie);
		zgwZaak.setOmschrijving(zdsWordZaak.omschrijving);
		zgwZaak.setToelichting(zdsWordZaak.toelichting);
		zgwZaak.setRegistratiedatum(getDateStringFromZdsDate(zdsWordZaak.registratiedatum));
		zgwZaak.setStartdatum(getDateStringFromZdsDate(zdsWordZaak.startdatum));
		zgwZaak.setEinddatumGepland(getDateStringFromZdsDate(zdsWordZaak.einddatumGepland));
		zgwZaak.setArchiefnominatie(getZGWArchiefNominatie(zdsWordZaak.archiefnominatie));
		zgwClient.putZaak(zgwZaak);
		
		
		if(zdsWasZaak.heeftAlsBelanghebbende != null && !zdsWasZaak.heeftAlsBelanghebbende.equals(zdsWordZaak.heeftAlsBelanghebbende)) throw new ZaakTranslatorException("Update voor heeftAlsBelanghebbende bij zaak met identificatie: '" + zdsWasZaak.identificatie + "' niet geimplementeerd");		if(zdsWasZaak.heeftAlsInitiator != zdsWordZaak.heeftAlsInitiator) log.warn("heeftAlsInitiator not implemented");
		if(zdsWasZaak.heeftAlsUitvoerende != null && !zdsWasZaak.heeftAlsUitvoerende.equals(zdsWordZaak.heeftAlsUitvoerende)) throw new ZaakTranslatorException("Update voor heeftAlsUitvoerende bij zaak met identificatie: '" + zdsWasZaak.identificatie + "' niet geimplementeerd");
		if(zdsWasZaak.heeftAlsAanspreekpunt != null && !zdsWasZaak.heeftAlsAanspreekpunt.equals(zdsWordZaak.heeftAlsAanspreekpunt)) throw new ZaakTranslatorException("Update voor heeftAlsAanspreekpunt bij zaak met identificatie: '" + zdsWasZaak.identificatie + "' niet geimplementeerd");
		if(zdsWasZaak.heeftBetrekkingOp != null && !zdsWasZaak.heeftBetrekkingOp.equals(zdsWordZaak.heeftBetrekkingOp)) throw new ZaakTranslatorException("Update voor heeftBetrekkingOp bij zaak met identificatie: '" + zdsWasZaak.identificatie + "' niet geimplementeerd");
		if(zdsWasZaak.heeftAlsVerantwoordelijke != null && !zdsWasZaak.heeftAlsVerantwoordelijke.equals(zdsWordZaak.heeftAlsVerantwoordelijke)) throw new ZaakTranslatorException("Update voor heeftAlsVerantwoordelijke bij zaak met identificatie: '" + zdsWasZaak.identificatie + "' niet geimplementeerd");

		// TODO: warning if nothing changes
		
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

	public List<Rol> getRollen(ZgwCompleteZaak zgwzaak, ZdsRol zdsRol, String rolname) throws ZaakTranslatorException,  ZGWClientException {
		var zgwRoltype = zgwClient.getRolTypeByOmschrijving(zgwzaak.zaaktype, rolname);
		if(zgwRoltype == null) throw new ZaakTranslatorException("Geen roltype niet gevonden ZTC voor identificatie: '" + rolname);
				
		var rollen = new java.util.ArrayList<Rol>();		
		if(zdsRol.gerelateerde.natuurlijkPersoon != null) {
			var rol = new Rol();
			rol.setRoltype(zgwRoltype.getUrl());
			rol.setRoltoelichting(rolname);
			var nps = getBetrokkeneIdentificatieNPS(zdsRol.gerelateerde.natuurlijkPersoon);
			rol.setBetrokkeneIdentificatie(nps);
			rol.setBetrokkeneType("natuurlijk_persoon");
			rollen.add(rol);
		}
		if(zdsRol.gerelateerde.medewerker != null) {
			var rol = new Rol();
			rol.setRoltype(zgwRoltype.getUrl());
			rol.setRoltoelichting(rolname);
			var zdsMedewerker = getBetrokkeneIdentificatieMedewerker(zdsRol.gerelateerde.medewerker);
			rol.setBetrokkeneIdentificatie(zdsMedewerker);
			rol.setBetrokkeneType("medewerker");
			rollen.add(rol);			
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
		return nps;
	}

	private ZgwBetrokkeneIdentificatie getBetrokkeneIdentificatieMedewerker(ZdsMedewerker zdsMedewerker)  {
		ZgwBetrokkeneIdentificatie zgwMedewerker = new ZgwBetrokkeneIdentificatie();
		zgwMedewerker.identificatie = zdsMedewerker.identificatie;
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
		
		var zgwZaak = this.zgwClient.getZaakByIdentificatie(edcLk01.objects.get(0).isRelevantVoor.gerelateerde.identificatie);
		String zaakUrl = zgwZaak.url;
		ZgwZaakInformatieObject result = addZaakInformatieObject(zgwEnkelvoudigInformatieObject, zaakUrl);
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
		ZgwCompleteZaak zgwZaak = zgwClient.getZaakByIdentificatie(zdsZaak.identificatie);

		//this.zaakTranslator.setZakLk01(zakLk01);
		ZgwStatus zgwStatus = new ZgwStatus();
		zgwStatus.statustoelichting = zdsZaak.heeft.statustoelichting;
		zgwStatus.datumStatusGezet = getDateTimeStringFromStufDate(zdsZaak.heeft.datumStatusGezet);
		zgwStatus.zaak = zgwZaak.url;
		zgwStatus.statustype = zgwClient.getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype, Integer.valueOf(zdsZaak.heeft.gerelateerde.volgnummer)).url;

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
	
	private String getDateTimeStringFromStufDate(String stufDate) {

		var year = stufDate.substring(0, 4);
		var month = stufDate.substring(4, 6);
		var day = stufDate.substring(6, 8);
		var hours = stufDate.substring(8, 10);
		var minutes = stufDate.substring(10, 12);
		var seconds = stufDate.substring(12, 14);
		var milliseconds = stufDate.substring(14);
		return year + "-" + month + "-" + day + "T" + hours + ":" + minutes + ":" + seconds + "." + milliseconds + "Z";
	}

	public ZakLa01 getZaakDetails(ZakLv01 zakLv01) throws ZaakTranslatorException, ZGWClientException {
		if (zakLv01.gelijk != null && zakLv01.gelijk.identificatie != null) {
			var zgwZaak = zgwClient.getZaakByIdentificatie(zakLv01.gelijk.identificatie);
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
			for(Rol zgwRol: zgwRollen) {
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
			var zgwZaak = zgwClient.getZaakByIdentificatie(zakLv01.gelijk.identificatie);
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

	public void synchronizeZdstoZgw(RequestResponseCycle session, ZGWClient zgwClient2, ConfigService config, ApplicationParameterRepository repository, String identificatie) {
			// check
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
