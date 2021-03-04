package nl.haarlem.translations.zdstozgw.translation.zds.services;

import java.lang.invoke.MethodHandles;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.Organisatie;
import nl.haarlem.translations.zdstozgw.config.model.ZgwRolOmschrijving;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.debug.Debugger;
import nl.haarlem.translations.zdstozgw.translation.BetrokkeneType;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGerelateerde;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsHeeft;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsHeeftRelevant;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsInhoud;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsIsRelevantVoor;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsKenmerk;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsOpschorting;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsRol;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsVerlenging;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakDocumentInhoud;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwAdres;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwBetrokkeneIdentificatie;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwEnkelvoudigInformatieObject;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwInformatieObjectType;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwKenmerk;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwLock;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwResultaat;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwRol;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwStatus;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwStatusType;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaak;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakInformatieObject;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakPut;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakType;
import nl.haarlem.translations.zdstozgw.utils.ChangeDetector;
import nl.haarlem.translations.zdstozgw.utils.ChangeDetector.Change;

@Service
public class ZaakService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final Debugger debug = Debugger.getDebugger(MethodHandles.lookup().lookupClass());

	@Value("${nl.haarlem.translations.zdstozgw.auto.laststatus}")
	public Boolean autoLastStatus;	
	
	public final ZGWClient zgwClient;

	private final ModelMapper modelMapper;
	public final ConfigService configService;

	@Autowired
	public ZaakService(ZGWClient zgwClient, ModelMapper modelMapper, ConfigService configService) {
		this.zgwClient = zgwClient;
		this.modelMapper = modelMapper;
		this.configService = configService;
	}

	public String getRSIN(String gemeenteCode) {
		List<Organisatie> organisaties = this.configService.getConfiguration().getOrganisaties();
		for (Organisatie organisatie : organisaties) {
			if (organisatie.getGemeenteCode().equals(gemeenteCode)) {
				return organisatie.getRSIN();
			}
		}
		return "";
	}

	public ZgwZaak creeerZaak(String rsin, ZdsZaak zdsZaak) {
		log.debug("creeerZaak:" + zdsZaak.identificatie);
		ZgwZaak zgwZaak = this.modelMapper.map(zdsZaak, ZgwZaak.class);

		var zaaktypecode = zdsZaak.isVan.gerelateerde.code;
		var zaaktype = this.zgwClient.getZgwZaakTypeByIdentificatie(zaaktypecode);
		if (zaaktype == null) {
			throw new ConverterException("Zaaktype met code:" + zaaktypecode + " could not be found");
		}
		zgwZaak.zaaktype = zaaktype.url;
		zgwZaak.bronorganisatie = rsin;
		zgwZaak.verantwoordelijkeOrganisatie = rsin;

		if (zdsZaak.getKenmerk() != null && !zdsZaak.getKenmerk().isEmpty()) {
			zgwZaak.kenmerk = new ArrayList<>();
			// TODO: controleren of werkt
			for (ZdsKenmerk kenmerk : zdsZaak.getKenmerk()) {
				zgwZaak.kenmerk.add(this.modelMapper.map(kenmerk, ZgwKenmerk.class));
			}
		}
		
		// alleen een verlenging meenemen als er echt waarden in staan
		if(zgwZaak.verlenging != null && (zgwZaak.verlenging.reden == null || zgwZaak.verlenging.reden.length() == 0)) {
			zgwZaak.verlenging = null;
		}

		zgwZaak = this.zgwClient.addZaak(zgwZaak);
		log.debug("Created a ZGW Zaak with UUID: " + zgwZaak.getUuid());

		// rollen
		ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguration().getZgwRolOmschrijving();
		addRolToZgw(zdsZaak.heeftBetrekkingOp, zgwRolOmschrijving.getHeeftBetrekkingOp(), zgwZaak);
		addRolToZgw(zdsZaak.heeftAlsBelanghebbende, zgwRolOmschrijving.getHeeftAlsBelanghebbende(), zgwZaak);
		addRolToZgw(zdsZaak.heeftAlsInitiator, zgwRolOmschrijving.getHeeftAlsInitiator(), zgwZaak);
		addRolToZgw(zdsZaak.heeftAlsUitvoerende, zgwRolOmschrijving.getHeeftAlsUitvoerende(), zgwZaak);
		addRolToZgw(zdsZaak.heeftAlsVerantwoordelijke, zgwRolOmschrijving.getHeeftAlsVerantwoordelijke(), zgwZaak);
		addRolToZgw(zdsZaak.heeftAlsGemachtigde, zgwRolOmschrijving.getHeeftAlsGemachtigde(), zgwZaak);
		addRolToZgw(zdsZaak.heeftAlsOverigBetrokkene, zgwRolOmschrijving.getHeeftAlsOverigBetrokkene(), zgwZaak);

		setResultaatAndStatus(zdsZaak, zgwZaak);
		
		return zgwZaak;
	}
	
	public void updateZaak(ZdsZaak zdsWasZaak, ZdsZaak zdsWordtZaak) {
		log.debug("updateZaak:" + zdsWasZaak.identificatie);
		ZgwZaak zgwZaak = this.zgwClient.getZaakByIdentificatie(zdsWasZaak.identificatie);
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
			for (Change change : fieldChanges.keySet()) {
				log.debug("\tchange:" + change.getField().getName());
			}
			ZgwZaakPut zgwWordtZaak = this.modelMapper.map(zdsWordtZaak, ZgwZaakPut.class);
			ZgwZaakPut updatedZaak = ZgwZaakPut.merge(zgwZaak, zgwWordtZaak);
			this.zgwClient.updateZaak(zgwZaak.uuid, updatedZaak);

			changed = true;
		}

		// rollen
		Map<ChangeDetector.Change, ChangeDetector.ChangeType> rolChanges = changeDetector.getAllChangesByFieldType(ZdsRol.class);
		if (rolChanges.size() > 0) {
			log.debug("Update of zaakid:" + zdsWasZaak.identificatie + " has # " + rolChanges.size() + " rol changes:");

			changeDetector.filterChangesByType(rolChanges, ChangeDetector.ChangeType.NEW)
					.forEach((change, changeType) -> {
						var rolnaam = getRolOmschrijvingGeneriekByRolName(change.getField().getName());
						log.debug("[CHANGE ROL] New Rol:" + rolnaam);
						addRolToZgw((ZdsRol) change.getValue(), rolnaam, zgwZaak);
					});

			changeDetector.filterChangesByType(rolChanges, ChangeDetector.ChangeType.DELETED)
					.forEach((change, changeType) -> {
						var rolnaam = getRolOmschrijvingGeneriekByRolName(change.getField().getName());
						if(rolnaam != null) {						
							log.debug("[CHANGE ROL] Deleted Rol:" + rolnaam);
							deleteRolFromZgw(rolnaam, zgwZaak);
						}
					});

			changeDetector.filterChangesByType(rolChanges, ChangeDetector.ChangeType.CHANGED)
					.forEach((change, changeType) -> {
						var rolnaam = getRolOmschrijvingGeneriekByRolName(change.getField().getName());
						log.debug("[CHANGE ROL] Update Rol:" + rolnaam);
						updateRolInZgw(rolnaam, zgwZaak, (ZdsRol) change.getValue());
					});
			changed = true;
		}

		boolean rasChanged = setResultaatAndStatus(zdsWordtZaak, zgwZaak);
				
		if (!changed && ! rasChanged) {
			debugWarning("Update of zaakid:" + zdsWasZaak.identificatie + " without any changes");
		}
	}
	
	private boolean setResultaatAndStatus(ZdsZaak zdsZaak, ZgwZaak zgwZaak) {
		var changed = false;		
		
		if (zdsZaak.resultaat != null && zdsZaak.resultaat.omschrijving != null) {
			// wanneer eindezaak

			// Difference between ZDS --> ZGW the behaviour of ending a zaak has changed.
			// (more info at: https://vng-realisatie.github.io/gemma-zaken/standaard/zaken/index#zrc-007 ) 
			//
			// in ZDS:
			//	- object/einddatum contained the einddatum
			//	- object/resultaat/omgeschrijving contained the resultaat-omschrijving
			// 
			// in ZGW:
			//	- resultaat an reference and status has to be set to the one with the highest volgnummer
			var zaakid = zdsZaak.identificatie;
			var resultaatomschrijving = zdsZaak.resultaat.omschrijving;
			var einddatum = zdsZaak.einddatum;			
			var today = new SimpleDateFormat("yyyyMMdd").format(new Date()); 
			
			if(einddatum == null) {
				debugWarning("Update of zaakid:" + zaakid + " has resultaat but no einddatum, using today");
				einddatum = today;
			}
			//else if(!einddatum.equals(today)) {
			//	log.warn("Update of zaakid:" + zaakid + " has resultaat and einddatum, einddatum:" + zdsZaak.einddatum + " is not today (" + today + ")");				
			//}
			log.debug("Update of zaakid:" + zaakid + " with resultaatomschrijving:" + resultaatomschrijving );
			var zgwResultaatType = this.zgwClient.getResultaatTypeByZaakTypeAndOmschrijving(zgwZaak.zaaktype, resultaatomschrijving);			
			log.debug("Gevonden restulaattype:" + zgwResultaatType.omschrijving);						
			var resultaten = this.zgwClient.getResultatenByZaakUrl(zgwZaak.url);			
			
			for (ZgwResultaat resultaat : resultaten) {
				debugWarning("Zaak met identitifatie:" + zaakid+ " already has resultaat #" + resultaten.indexOf(resultaat) + " met toelichting:" +  resultaat.toelichting);
				if(resultaat.toelichting.equals(zdsZaak.resultaat.omschrijving)) {
					debugWarning("Gevonden resultaat:" + resultaat.toelichting + " is hetzelfde als waar het resultaat opgezet moet worden, kan zo niet goed gaan");
				}
			}			
			ZgwResultaat zgwResultaat = new ZgwResultaat();
			zgwResultaat.zaak = zgwZaak.url;
			zgwResultaat.resultaattype = zgwResultaatType.url;
			zgwResultaat.toelichting = zdsZaak.resultaat.omschrijving;
			this.zgwClient.actualiseerZaakResultaat(zgwResultaat);

					
			// Bekijken wat de laatste status is, deze moet gezet worden bij het afsltuiden van de zaak
			var statustypes = this.zgwClient.getStatusTypesByZaakType(zgwZaak.zaaktype);
			ZgwStatusType laststatustype = null;
			for (ZgwStatusType statustype : statustypes) {
				if(laststatustype == null || laststatustype.volgnummer < statustype.volgnummer) {
					laststatustype = statustype;
				}
			}					
			if(laststatustype == null) {
				throw new ConverterException("no statuses found for zaaktype:" + zgwZaak.zaaktype);
			}
			
			// nu kijken of we een status hebben die gelijk is aan wat de laatste status zou moeten zijn,...
			ZgwStatusType foundstatustype = null;
			ZdsHeeft zdsHeeft = null;
			if (zdsZaak.heeft != null && zdsZaak.heeft.size() > 0 && zdsZaak.heeft.get(0).gerelateerde != null) {
				zdsHeeft = zdsZaak.heeft.get(0);
				var zdsStatus = zdsHeeft.gerelateerde;
				foundstatustype = this.zgwClient.getStatusTypeByZaakTypeAndOmschrijving(zgwZaak.zaaktype, zdsStatus.omschrijving, zdsStatus.volgnummer);				
			}
			
			if(foundstatustype == null) {
				debugWarning("einddatum and resultaat without a status");
				if(this.autoLastStatus) {
					debugWarning("autoLastStatus = enabled: setting status to:" + laststatustype.omschrijving);
					foundstatustype = laststatustype;
					// de status heeft straks info nodig
					zdsHeeft = new ZdsHeeft();
					zdsHeeft.datumStatusGezet = einddatum;
				}
			}
			else if(!laststatustype.url.equals(foundstatustype.url)) {
				debugWarning("einddatum and resultaat but found status:" + foundstatustype.omschrijving + " is not the last status:" + laststatustype.omschrijving);
				if(this.autoLastStatus) {
					debugWarning("autoLastStatus = enabled: overriding status to:" + laststatustype.omschrijving);
					foundstatustype = laststatustype;
				}
			}
			if(foundstatustype != null) {			
				ZgwStatus zgwStatus = this.modelMapper.map(zdsHeeft, ZgwStatus.class);
				zgwStatus.zaak = zgwZaak.url;
				zgwStatus.statustype = foundstatustype.url;			
				this.zgwClient.actualiseerZaakStatus(zgwStatus);
			}
			else {
				debugWarning("No status, while einddatum and resultaat were supplied");	
			}
			changed = true;
		}
		else if (zdsZaak.heeft != null && zdsZaak.heeft.size() > 0 && zdsZaak.heeft.get(0).gerelateerde != null) {
				var zdsHeeft = zdsZaak.heeft.get(0);
				var zdsStatus = zdsHeeft.gerelateerde;
				log.debug("Update of zaakid:" + zdsZaak.identificatie + " wants status to be changed to:" + zdsStatus.omschrijving);				
				var zgwStatusType = this.zgwClient.getStatusTypeByZaakTypeAndOmschrijving(zgwZaak.zaaktype, zdsStatus.omschrijving, zdsStatus.volgnummer);			
				ZgwStatus zgwStatus = this.modelMapper.map(zdsHeeft, ZgwStatus.class);
				zgwStatus.zaak = zgwZaak.url;
				zgwStatus.statustype = zgwStatusType.url;
				this.zgwClient.actualiseerZaakStatus(zgwStatus);	
				changed = true;
		}
		return changed;
	}

	private void addRolToZgw(ZdsRol zdsRol, String typeRolOmschrijving, ZgwZaak createdZaak) {
		log.debug("addRolToZgw Rol:" + typeRolOmschrijving);
		if (zdsRol == null) {
			return;
		}
		if (zdsRol.gerelateerde == null) {
			// throw new ConverterException("Rol:" + typeRolOmschrijving + " zonder gerelateerde informatie");
			debugWarning("Rol:" + typeRolOmschrijving + " zonder gerelateerde informatie");
			return;
		}
		ZgwRol zgwRol = new ZgwRol();
		if (zdsRol.gerelateerde.medewerker != null) {
			zgwRol.betrokkeneIdentificatie = this.modelMapper.map(zdsRol.gerelateerde.medewerker,
					ZgwBetrokkeneIdentificatie.class);
			zgwRol.betrokkeneType = BetrokkeneType.MEDEWERKER.getDescription();
		}
		if (zdsRol.gerelateerde.natuurlijkPersoon != null) {
			if (zgwRol.betrokkeneIdentificatie == null) {
				if (zgwRol.betrokkeneIdentificatie != null) {
					throw new ConverterException("Rol: " + typeRolOmschrijving + " wordt al gebruikt voor medewerker");
				}
			}
			zgwRol.betrokkeneIdentificatie = this.modelMapper.map(zdsRol.gerelateerde.natuurlijkPersoon, ZgwBetrokkeneIdentificatie.class);
			if(zdsRol.gerelateerde.natuurlijkPersoon.verblijfsadres != null) {
				if(zdsRol.gerelateerde.natuurlijkPersoon.verblijfsadres != null) {
					if(zdsRol.gerelateerde.natuurlijkPersoon.verblijfsadres.identificatie == null || zdsRol.gerelateerde.natuurlijkPersoon.verblijfsadres.identificatie.length() == 0) {
						// https://github.com/Sudwest-Fryslan/OpenZaakBrug/issues/55
						debugWarning("No aoaIdentificatie found for zaak with id: " + createdZaak.identificatie + " in rol: " + typeRolOmschrijving + " for natuurlijkPersoon");
					}
					else {
						zgwRol.betrokkeneIdentificatie.verblijfsadres = this.modelMapper.map(zdsRol.gerelateerde.natuurlijkPersoon.verblijfsadres, ZgwAdres.class);
						// https://github.com/Sudwest-Fryslan/OpenZaakBrug/issues/54
						// 		Move code to the ModelMapperConfig.java						
						zgwRol.betrokkeneIdentificatie.verblijfsadres = new ZgwAdres();
						zgwRol.betrokkeneIdentificatie.verblijfsadres.aoaIdentificatie = zdsRol.gerelateerde.natuurlijkPersoon.verblijfsadres.identificatie;
						zgwRol.betrokkeneIdentificatie.verblijfsadres.wplWoonplaatsNaam = zdsRol.gerelateerde.natuurlijkPersoon.verblijfsadres.woonplaatsnaam;
						zgwRol.betrokkeneIdentificatie.verblijfsadres.gorOpenbareRuimteNaam = zdsRol.gerelateerde.natuurlijkPersoon.verblijfsadres.straatnaam;
						zgwRol.betrokkeneIdentificatie.verblijfsadres.aoaPostcode = zdsRol.gerelateerde.natuurlijkPersoon.verblijfsadres.postcode;
						zgwRol.betrokkeneIdentificatie.verblijfsadres.aoaHuisnummer = zdsRol.gerelateerde.natuurlijkPersoon.verblijfsadres.huisnummer;
						zgwRol.betrokkeneIdentificatie.verblijfsadres.aoaHuisletter = zdsRol.gerelateerde.natuurlijkPersoon.verblijfsadres.huisletter;
						zgwRol.betrokkeneIdentificatie.verblijfsadres.aoaHuisnummertoevoeging = zdsRol.gerelateerde.natuurlijkPersoon.verblijfsadres.huisnummertoevoeging;
						zgwRol.betrokkeneIdentificatie.verblijfsadres.inpLocatiebeschrijving  = zdsRol.gerelateerde.natuurlijkPersoon.verblijfsadres.locatiebeschrijving;
					}
				}
			}			
			zgwRol.betrokkeneType = BetrokkeneType.NATUURLIJK_PERSOON.getDescription();
		}
		if (zgwRol.betrokkeneIdentificatie == null) {
			throw new ConverterException("Rol: " + typeRolOmschrijving + " zonder Natuurlijkpersoon or Medewerker");
		}
		zgwRol.roltoelichting = typeRolOmschrijving;
		var roltype = this.zgwClient.getRolTypeByZaaktypeUrlAndOmschrijving(createdZaak.zaaktype, typeRolOmschrijving);
		if (roltype == null) {
			var zaaktype = this.zgwClient.getZaakTypeByUrl(createdZaak.zaaktype);
			throw new ConverterException(
					"Rol: " + typeRolOmschrijving + " niet gevonden bij Zaaktype: " + zaaktype.identificatie);
		}
		zgwRol.roltype = roltype.url;
		zgwRol.zaak = createdZaak.getUrl();
		this.zgwClient.addZgwRol(zgwRol);
	}

	public List<ZdsHeeftRelevant> geefLijstZaakdocumenten(String zaakidentificatie) {
		log.debug("geefLijstZaakdocumenten:" + zaakidentificatie);
		ZgwZaak zgwZaak = this.zgwClient.getZaakByIdentificatie(zaakidentificatie);

		var relevanteDocumenten = new ArrayList<ZdsHeeftRelevant>();
		for (ZgwZaakInformatieObject zgwZaakInformatieObject : this.zgwClient
				.getZaakInformatieObjectenByZaak(zgwZaak.url)) {
			ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = this.zgwClient
					.getZaakDocumentByUrl(zgwZaakInformatieObject.informatieobject);
			if (zgwEnkelvoudigInformatieObject == null) {
				throw new ConverterException("could not get the zaakdocument: "
						+ zgwZaakInformatieObject.informatieobject + " for zaak:" + zaakidentificatie);
			}
			ZgwInformatieObjectType documenttype = this.zgwClient
					.getZgwInformatieObjectTypeByUrl(zgwEnkelvoudigInformatieObject.informatieobjecttype);
			if (documenttype == null) {
				throw new ConverterException("getZgwInformatieObjectType #"
						+ zgwEnkelvoudigInformatieObject.informatieobjecttype + " could not be found");
			}
			/*
			 * if(zgwEnkelvoudigInformatieObject == null) { throw new
			 * ConverterException("ZgwEnkelvoudigInformatieObject #" + documentIdentificatie
			 * + " could not be found"); } ZgwInformatieObjectType documenttype =
			 * zgwClient.getZgwInformatieObjectTypeByÙrl(zgwEnkelvoudigInformatieObject.
			 * informatieobjecttype); if(documenttype == null) { throw new
			 * ConverterException("getZgwInformatieObjectType #" +
			 * zgwEnkelvoudigInformatieObject.informatieobjecttype + " could not be found");
			 * } var zgwZaakInformatieObject =
			 * zgwClient.getZgwZaakInformatieObjectByEnkelvoudigInformatieObjectUrl(
			 * zgwEnkelvoudigInformatieObject.getUrl()); if(zgwZaakInformatieObject == null)
			 * { throw new ConverterException("getZgwZaakInformatieObjectByUrl #" +
			 * zgwEnkelvoudigInformatieObject.getUrl() + " could not be found"); } var
			 * zgwZaak = zgwClient.getZaakByUrl(zgwZaakInformatieObject.getZaak());
			 * if(zgwZaak == null) { throw new ConverterException("getZaakByUrl #" +
			 * zgwZaakInformatieObject.getZaak() + " could not be found"); } String inhoud =
			 * zgwClient.getBas64Inhoud(zgwEnkelvoudigInformatieObject.getInhoud());
			 * if(inhoud == null) { throw new ConverterException("getBas64Inhoud #" +
			 * zgwEnkelvoudigInformatieObject.getInhoud() + " could not be found"); }
			 *
			 *
			 * ZdsZaakDocumentInhoud result =
			 * modelMapper.map(zgwEnkelvoudigInformatieObject, ZdsZaakDocumentInhoud.class);
			 * result.inhoud = new ZdsInhoud(); var mimeType =
			 * URLConnection.guessContentTypeFromName(zgwEnkelvoudigInformatieObject.
			 * bestandsnaam); // documenttype result.omschrijving =
			 * documenttype.omschrijving;
			 *
			 */
			ZdsZaakDocument zdsZaakDocument = this.modelMapper.map(zgwEnkelvoudigInformatieObject,
					ZdsZaakDocument.class);
			zdsZaakDocument.omschrijving = documenttype.omschrijving;
			ZdsHeeftRelevant heeftRelevant = this.modelMapper.map(zgwZaakInformatieObject, ZdsHeeftRelevant.class);
			heeftRelevant.gerelateerde = zdsZaakDocument;
			relevanteDocumenten.add(heeftRelevant);
		}
		return relevanteDocumenten;
	}

	public ZgwEnkelvoudigInformatieObject voegZaakDocumentToe(String rsin, ZdsZaakDocumentInhoud zdsInformatieObject) {
		log.debug("voegZaakDocumentToe:" + zdsInformatieObject.identificatie);

		var zgwInformatieObjectType = this.zgwClient.getZgwInformatieObjectTypeByOmschrijving(zdsInformatieObject.omschrijving);
		if (zgwInformatieObjectType == null) {
			throw new RuntimeException("Documenttype not found for omschrijving: " + zdsInformatieObject.omschrijving);
		}

		ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = this.modelMapper.map(zdsInformatieObject, ZgwEnkelvoudigInformatieObject.class);
		zgwEnkelvoudigInformatieObject.informatieobjecttype = zgwInformatieObjectType.url;		
		zgwEnkelvoudigInformatieObject.bronorganisatie = rsin;
		// https://github.com/Sudwest-Fryslan/OpenZaakBrug/issues/54
		// 		Move code to the ModelMapperConfig.java
		if(zgwEnkelvoudigInformatieObject.verzenddatum != null && zgwEnkelvoudigInformatieObject.verzenddatum.length() == 0) {
			zgwEnkelvoudigInformatieObject.verzenddatum = null;
		}
		zgwEnkelvoudigInformatieObject.indicatieGebruiksrecht = "false";
		
		zgwEnkelvoudigInformatieObject = this.zgwClient.addZaakDocument(zgwEnkelvoudigInformatieObject);
		ZgwZaak zgwZaak = this.zgwClient
				.getZaakByIdentificatie(zdsInformatieObject.isRelevantVoor.gerelateerde.identificatie);
		ZgwZaakInformatieObject zgwZaakInformatieObject = addZaakInformatieObject(zgwEnkelvoudigInformatieObject, zgwZaak.url);

		// status
		if (zdsInformatieObject.isRelevantVoor.volgnummer != null
				&& zdsInformatieObject.isRelevantVoor.omschrijving != null
				&& zdsInformatieObject.isRelevantVoor.datumStatusGezet != null) {
			log.debug("Update of zaakid:" + zgwZaak.identificatie + " has  status changes");
			var zgwStatusType = this.zgwClient.getStatusTypeByZaakTypeAndOmschrijving(zgwZaak.zaaktype,
					zdsInformatieObject.isRelevantVoor.omschrijving, zdsInformatieObject.isRelevantVoor.volgnummer);
			// ZgwStatus zgwStatus = modelMapper.map(zdsHeeft, ZgwStatus.class);
			ZgwStatus zgwStatus = new ZgwStatus();
			zgwStatus.zaak = zgwZaak.url;
			zgwStatus.statustype = zgwStatusType.url;
			this.zgwClient.actualiseerZaakStatus(zgwStatus);
		}

		return zgwEnkelvoudigInformatieObject;
	}

	private ZgwZaakInformatieObject addZaakInformatieObject(ZgwEnkelvoudigInformatieObject doc, String zaakUrl) {
		var zgwZaakInformatieObject = new ZgwZaakInformatieObject();
		zgwZaakInformatieObject.setZaak(zaakUrl);
		zgwZaakInformatieObject.setInformatieobject(doc.getUrl());
		zgwZaakInformatieObject.setTitel(doc.getTitel());
		return this.zgwClient.addDocumentToZaak(zgwZaakInformatieObject);
	}

	public ZdsZaakDocumentInhoud getZaakDocumentLezen(String documentIdentificatie) {
		log.debug("getZaakDocumentLezen:" + documentIdentificatie);
		ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = this.zgwClient
				.getZgwEnkelvoudigInformatieObjectByIdentiticatie(documentIdentificatie);
		if (zgwEnkelvoudigInformatieObject == null) {
			throw new ConverterException(
					"ZgwEnkelvoudigInformatieObject #" + documentIdentificatie + " could not be found");
		}
		ZgwInformatieObjectType documenttype = this.zgwClient
				.getZgwInformatieObjectTypeByUrl(zgwEnkelvoudigInformatieObject.informatieobjecttype);
		if (documenttype == null) {
			throw new ConverterException("getZgwInformatieObjectType #"
					+ zgwEnkelvoudigInformatieObject.informatieobjecttype + " could not be found");
		}
		var zgwZaakInformatieObject = this.zgwClient
				.getZgwZaakInformatieObjectByEnkelvoudigInformatieObjectUrl(zgwEnkelvoudigInformatieObject.getUrl());
		if (zgwZaakInformatieObject == null) {
			throw new ConverterException("getZgwZaakInformatieObjectByUrl #" + zgwEnkelvoudigInformatieObject.getUrl()
					+ " could not be found");
		}
		var zgwZaak = this.zgwClient.getZaakByUrl(zgwZaakInformatieObject.getZaak());
		if (zgwZaak == null) {
			throw new ConverterException("getZaakByUrl #" + zgwZaakInformatieObject.getZaak() + " could not be found");
		}
		String inhoud = this.zgwClient.getBas64Inhoud(zgwEnkelvoudigInformatieObject.getInhoud());
		if (inhoud == null) {
			throw new ConverterException(
					"getBas64Inhoud #" + zgwEnkelvoudigInformatieObject.getInhoud() + " could not be found");
		}

		ZdsZaakDocumentInhoud result = this.modelMapper.map(zgwEnkelvoudigInformatieObject,
				ZdsZaakDocumentInhoud.class);
		result.inhoud = new ZdsInhoud();
		var mimeType = URLConnection.guessContentTypeFromName(zgwEnkelvoudigInformatieObject.bestandsnaam);

		// documenttype
		result.omschrijving = documenttype.omschrijving;
		if (result.ontvangstdatum == null) {
			result.ontvangstdatum = "00010101";
		}
		result.titel = zgwEnkelvoudigInformatieObject.titel;
		result.beschrijving = zgwEnkelvoudigInformatieObject.beschrijving;
		if (result.beschrijving.length() == 0) {
			result.beschrijving = null;
		}
		if (result.versie.length() == 0) {
			result.versie = null;
		}
		if (result.taal.length() == 0) {
			result.taal = null;
		}
		if (result.status.length() == 0) {
			result.status = null;
		}

		result.formaat = zgwEnkelvoudigInformatieObject.bestandsnaam
				.substring(zgwEnkelvoudigInformatieObject.bestandsnaam.lastIndexOf(".") + 1);
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

	public ZgwZaak actualiseerZaakstatus(ZdsZaak wasZaak, ZdsZaak wordtZaak) {
		log.debug("actualiseerZaakstatus:" + wasZaak.identificatie);
		ZgwZaak zgwZaak = this.zgwClient.getZaakByIdentificatie(wasZaak.identificatie);
		var zdsHeeft = wordtZaak.heeft.get(0);
		var zdsStatus = zdsHeeft.gerelateerde;
		// var zgwStatusType =
		// zgwClient.getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype,
		// zdsStatus.volgnummer, zdsStatus.omschrijving);
		var zgwStatusType = this.zgwClient.getStatusTypeByZaakTypeAndOmschrijving(zgwZaak.zaaktype,
				zdsStatus.omschrijving, zdsStatus.volgnummer);

		ZgwStatus zgwStatus = this.modelMapper.map(zdsHeeft, ZgwStatus.class);
		zgwStatus.zaak = zgwZaak.url;
		zgwStatus.statustype = zgwStatusType.url;

		this.zgwClient.actualiseerZaakStatus(zgwStatus);
		return zgwZaak;
	}

	public List<ZdsZaak> getZaakDetailsByBsn(String bsn) {
		log.debug("getZaakDetailsByBsn:" + bsn);
		var zgwRollen = this.zgwClient.getRollenByBsn(bsn);
		var zdsZaken = new ArrayList<ZdsZaak>();
		var result = new ArrayList<ZdsZaak>();
		for (ZgwRol rol : zgwRollen) {
			var zgwRolType = this.zgwClient.getRolTypeByUrl(rol.roltype);
			ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguration().getZgwRolOmschrijving();
			if (zgwRolType.omschrijving.equals(zgwRolOmschrijving.getHeeftAlsInitiator())) {
				// TODO: hier minder overhead: hier wordt nu 2 keer achterelkaar een getzaak op openzaak gedaan!
				var zgwZaak = this.zgwClient.getZaakByUrl(rol.zaak);
				result.add(getZaakDetailsByIdentificatie(zgwZaak.identificatie));
			}
			if(result.size() >= 20) {
				// Max 20 results, it seems we get get unpredicted results after that
				debugWarning("Limit activated, no more than 20 results! (total amound found: " + zgwRollen.size() + " relations)");
				break;
			}
		}
		return result;
	}

	public ZdsZaak getZaakDetailsByIdentificatie(String zaakidentificatie) {
		log.debug("getZaakDetailsByIdentificatie:" + zaakidentificatie);
		var zgwZaak = this.zgwClient.getZaakByIdentificatie(zaakidentificatie);
		if (zgwZaak == null) {
			throw new ConverterException("Zaak not found for identification: '" + zaakidentificatie + "'");
		}
		ZdsZaak zaak = new ZdsZaak();
		zaak = this.modelMapper.map(zgwZaak, ZdsZaak.class);

		ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguration().getZgwRolOmschrijving();

		for (ZgwRol zgwRol : this.zgwClient.getRollenByZaakUrl(zgwZaak.url)) {
			var rolGeconverteerd = false;

			if (zgwRolOmschrijving.getHeeftAlsBelanghebbende() != null
					&& zgwRolOmschrijving.getHeeftAlsBelanghebbende().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
				zaak.heeftAlsBelanghebbende = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsBelanghebbende(), "ZAKBTRBLH");
				rolGeconverteerd = true;
			}
			if (zgwRolOmschrijving.getHeeftAlsInitiator() != null
					&& zgwRolOmschrijving.getHeeftAlsInitiator().equalsIgnoreCase(zgwRol.getRoltoelichting())) {
				zaak.heeftAlsInitiator = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsInitiator(), "ZAKBTRINI");
				rolGeconverteerd = true;
			}
			if (zgwRolOmschrijving.getHeeftAlsBelanghebbende() != null
					&& zgwRolOmschrijving.getHeeftAlsBelanghebbende().equalsIgnoreCase(zgwRol.getRoltoelichting())) {
				zaak.heeftAlsBelanghebbende = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsBelanghebbende(), "ZAKBTRBLH");
				rolGeconverteerd = true;
			}
			if (zgwRolOmschrijving.getHeeftAlsUitvoerende() != null
					&& zgwRolOmschrijving.getHeeftAlsUitvoerende().equalsIgnoreCase(zgwRol.getRoltoelichting())) {
				zaak.heeftAlsUitvoerende = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsUitvoerende(), "ZAKBTRUTV");
				rolGeconverteerd = true;
			}
			if (zgwRolOmschrijving.getHeeftAlsVerantwoordelijke() != null
					&& zgwRolOmschrijving.getHeeftAlsVerantwoordelijke().equalsIgnoreCase(zgwRol.getRoltoelichting())) {
				zaak.heeftAlsVerantwoordelijke = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsVerantwoordelijke(), "ZAKBTRVRA");
				rolGeconverteerd = true;
			}
			if (zgwRolOmschrijving.getHeeftAlsGemachtigde() != null
					&& zgwRolOmschrijving.getHeeftAlsGemachtigde().equalsIgnoreCase(zgwRol.getRoltoelichting())) {
				zaak.heeftAlsGemachtigde = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsGemachtigde(), "ZAKBTRGMC");
				rolGeconverteerd = true;
			}
			if (zgwRolOmschrijving.getHeeftAlsOverigBetrokkene() != null && zgwRolOmschrijving
					.getHeeftAlsOverigBetrokkene().equalsIgnoreCase(zgwRol.getOmschrijvingGeneriek())) {
				zaak.heeftAlsOverigBetrokkene = getZdsRol(zgwZaak, zgwRolOmschrijving.getHeeftAlsOverigBetrokkene(), "ZAKBTROVR");
				rolGeconverteerd = true;
			}
			if (!rolGeconverteerd) {
				throw new ConverterException("Rol: " + zgwRol.getOmschrijvingGeneriek() + " niet geconverteerd worden ("+ zgwRol.uuid + ")");
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
			for (ZgwKenmerk zgwKenmerk : zgwZaak.getKenmerk()) {
				var zdsKenmerkKenmerk = this.modelMapper.map(zgwKenmerk, ZdsKenmerk.class);
				zaak.kenmerk.add(zdsKenmerkKenmerk);
			}
		}

		zaak.opschorting = zgwZaak.getOpschorting() != null
				? this.modelMapper.map(zgwZaak.getOpschorting(), ZdsOpschorting.class)
				: null;
		zaak.verlenging = zgwZaak.getVerlenging() != null
				? this.modelMapper.map(zgwZaak.getVerlenging(), ZdsVerlenging.class)
				: null;

		var zdsStatussen = new ArrayList<ZdsHeeft>();
		for (ZgwStatus zgwStatus : this.zgwClient.getStatussenByZaakUrl(zgwZaak.url)) {
			ZgwStatusType zgwStatusType = this.zgwClient.getResource(zgwStatus.statustype, ZgwStatusType.class);
			// ZdsHeeft zdsHeeft = modelMapper.map(zgwStatus, ZdsHeeft.class);
			ZdsHeeft zdsHeeft = new ZdsHeeft();
			zdsHeeft.setEntiteittype("ZAKSTT");
			zdsHeeft.setIndicatieLaatsteStatus(Boolean.valueOf(zgwStatusType.isEindstatus) ? "J" : "N");

			zdsHeeft.gerelateerde = this.modelMapper.map(zgwStatus, ZdsGerelateerde.class);
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
		return this.zgwClient.getZaakTypes(null).stream().filter(zgwZaakType -> zgwZaakType.url.equalsIgnoreCase(url))
				.findFirst().orElse(null);
	}

	private ZdsRol getZdsRol(ZgwZaak zgwZaak, String rolOmschrijving, String entiteittype) {
		var zgwRolType = this.zgwClient.getRolTypeByZaaktypeUrlAndOmschrijving(zgwZaak.zaaktype, rolOmschrijving);
		ZgwRol zgwRol = this.zgwClient.getRolByZaakUrlAndRolTypeUrl(zgwZaak.url, zgwRolType.url);
		if (zgwRol == null) {
			// geen rol voor deze
			return null;
		}
		ZdsRol zdsRol = this.modelMapper.map(zgwRol, ZdsRol.class);
		zdsRol.setEntiteittype(entiteittype);
		return zdsRol;
	}

	private void updateRolInZgw(String typeRolOmschrijving, ZgwZaak zgwZaak, ZdsRol newValue) {
		log.debug("updateRolInZgw Rol:" + typeRolOmschrijving);

		// no put action for rollen, so first delete then add
		log.debug("Attempting to update rol by deleting and adding as new");
		deleteRolFromZgw(typeRolOmschrijving, zgwZaak);


		if(newValue.gerelateerde == null) {
			log.debug("Not adding the rol:"  + typeRolOmschrijving + ", gerelateerde == null ");
			return;
		}

		if(typeRolOmschrijving == null) {
			debugWarning("Not adding the rol, typeRolOmschrijving == null ");
			return;
		}				
		
		addRolToZgw(newValue, typeRolOmschrijving, zgwZaak);
	}

	private void deleteRolFromZgw(String typeRolOmschrijving, ZgwZaak zgwZaak) {
		log.debug("deleteRolFromZgw Rol:" + typeRolOmschrijving);

		var roltype = this.zgwClient.getRolTypeByZaaktypeUrlAndOmschrijving(zgwZaak.zaaktype, typeRolOmschrijving);
		if (roltype == null) {
			// throw new ConverterException("Roltype: " + typeRolOmschrijving + " niet gevonden bij zaaktype voor zaak: " + zgwZaak.identificatie);
			debugWarning("Roltype: " + typeRolOmschrijving + " niet gevonden bij zaaktype voor zaak: " + zgwZaak.identificatie);
			return;
		}
		var rol = this.zgwClient.getRolByZaakUrlAndRolTypeUrl(zgwZaak.url, roltype.url);
		if (rol == null) {
			//throw new ConverterException("Rol: " + typeRolOmschrijving + " niet gevonden bij zaak: " + zgwZaak.identificatie);
			debugWarning("Rol: " + typeRolOmschrijving + " niet gevonden bij zaaktype voor zaak: " + zgwZaak.identificatie);
			return;			
			
		}
		this.zgwClient.deleteRol(rol.uuid);
	}

	public String getRolOmschrijvingGeneriekByRolName(String rolName) {
		ZgwRolOmschrijving zgwRolOmschrijving = this.configService.getConfiguration().getZgwRolOmschrijving();

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

	public String checkOutZaakDocument(String documentIdentificatie) {
		log.debug("checkOutZaakDocument:" + documentIdentificatie);
		ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = this.zgwClient.getZgwEnkelvoudigInformatieObjectByIdentiticatie(documentIdentificatie);
		if (zgwEnkelvoudigInformatieObject == null) {
			throw new ConverterException(
					"ZgwEnkelvoudigInformatieObject #" + documentIdentificatie + " could not be found");
		}
		if (zgwEnkelvoudigInformatieObject == null) {
			throw new ConverterException("ZgwEnkelvoudigInformatieObjectByIdentiticatie not found for identificatie: " + zgwEnkelvoudigInformatieObject.identificatie);
		}
		if(zgwEnkelvoudigInformatieObject.locked) {
			throw new ConverterException("ZgwEnkelvoudigInformatieObjectByIdentiticatie with identificatie: " + zgwEnkelvoudigInformatieObject.identificatie + " cannot be locked and then changed");
		}		
		
		ZgwLock lock = this.zgwClient.getZgwInformatieObjectLock(zgwEnkelvoudigInformatieObject);
		log.debug("received lock:" + lock.lock);
		return lock.lock;
	}

	public Object cancelCheckOutZaakDocument(String documentIdentificatie, String lock) {
		log.debug("checkOutZaakDocument:" + documentIdentificatie);
		ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = this.zgwClient
				.getZgwEnkelvoudigInformatieObjectByIdentiticatie(documentIdentificatie);
		if (zgwEnkelvoudigInformatieObject == null) {
			throw new ConverterException(
					"ZgwEnkelvoudigInformatieObject #" + documentIdentificatie + " could not be found");
		}
		ZgwLock zgwLock = new ZgwLock();
		zgwLock.lock = lock;
		this.zgwClient.getZgwInformatieObjectUnLock(zgwEnkelvoudigInformatieObject, zgwLock);
		return null;
	}

	public ZgwEnkelvoudigInformatieObject updateZaakDocument(String lock, ZdsZaakDocumentInhoud zdsWasInformatieObject, ZdsZaakDocumentInhoud zdsWordtInformatieObject) {
		log.debug("updateZaakDocument lock:" + lock + " informatieobject:" + zdsWasInformatieObject.identificatie);

		var zgwWasEnkelvoudigInformatieObject = this.zgwClient.getZgwEnkelvoudigInformatieObjectByIdentiticatie(zdsWasInformatieObject.identificatie);
		if("definitief".equals(zgwWasEnkelvoudigInformatieObject.status)) {
			throw new RuntimeException("ZgwEnkelvoudigInformatieObjectByIdentiticatie with identificatie: " + zdsWasInformatieObject.identificatie + " cannot be locked and then changed");
		}
			

		// https://github.com/Sudwest-Fryslan/OpenZaakBrug/issues/54
		// 		Move code to the ModelMapperConfig.java
		//		Also merge, we shouldnt overwrite the old values this hard
		var zgwWordtEnkelvoudigInformatieObject = this.modelMapper.map(zdsWordtInformatieObject, ZgwEnkelvoudigInformatieObject.class);
		if(zgwWordtEnkelvoudigInformatieObject.verzenddatum != null && zgwWordtEnkelvoudigInformatieObject.verzenddatum.length() == 0) {
			zgwWordtEnkelvoudigInformatieObject.verzenddatum = null;
		}
		//zgwEnkelvoudigInformatieObject.indicatieGebruiksrecht = "false";
		zgwWordtEnkelvoudigInformatieObject.bronorganisatie = zgwWasEnkelvoudigInformatieObject.bronorganisatie;
		zgwWordtEnkelvoudigInformatieObject.informatieobjecttype = zgwWasEnkelvoudigInformatieObject.informatieobjecttype;
		
		//	"in_bewerking" "ter_vaststelling" "definitief" "gearchiveerd"
		zgwWordtEnkelvoudigInformatieObject.status = zgwWordtEnkelvoudigInformatieObject.status.toLowerCase();
		zgwWordtEnkelvoudigInformatieObject.lock = lock;
		zgwWordtEnkelvoudigInformatieObject.url = zgwWasEnkelvoudigInformatieObject.url;
		zgwWasEnkelvoudigInformatieObject = this.zgwClient.putZaakDocument(zgwWordtEnkelvoudigInformatieObject);
		//ZgwZaak zgwZaak = this.zgwClient.getZaakByIdentificatie(zdsInformatieObject.isRelevantVoor.gerelateerde.identificatie);
		//ZgwZaakInformatieObject zgwZaakInformatieObject = addZaakInformatieObject(zgwEnkelvoudigInformatieObject, zgwZaak.url);
		ZgwLock zgwLock = new ZgwLock();
		zgwLock.lock = lock;
		this.zgwClient.getZgwInformatieObjectUnLock(zgwWordtEnkelvoudigInformatieObject, zgwLock);
		
		// status
		//if (zdsInformatieObject.isRelevantVoor.volgnummer != null
		//		&& zdsInformatieObject.isRelevantVoor.omschrijving != null
		//		&& zdsInformatieObject.isRelevantVoor.datumStatusGezet != null) {
		//	log.debug("Update of zaakid:" + zgwZaak.identificatie + " has  status changes");
		//	var zgwStatusType = this.zgwClient.getStatusTypeByZaakTypeAndOmschrijving(zgwZaak.zaaktype,
		//			zdsInformatieObject.isRelevantVoor.omschrijving, zdsInformatieObject.isRelevantVoor.volgnummer);
		//	// ZgwStatus zgwStatus = modelMapper.map(zdsHeeft, ZgwStatus.class);
		//	ZgwStatus zgwStatus = new ZgwStatus();
		//	zgwStatus.zaak = zgwZaak.url;
		//	zgwStatus.statustype = zgwStatusType.url;
		//	this.zgwClient.actualiseerZaakStatus(zgwStatus);
		//}
		return zgwWasEnkelvoudigInformatieObject;
	}
	
	private void debugWarning(String message) {
		log.info("[processing warning] " + message);
		debug.infopoint("WARN:" + message.substring(0, 25) + "...", message);
	}	
}