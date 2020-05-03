package nl.haarlem.translations.zdstozgw.translation.zds.services;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import nl.haarlem.translations.zdstozgw.translation.ZaakTranslator;
import nl.haarlem.translations.zdstozgw.translation.ZaakTranslator.ZaakTranslatorException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLa01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01_v2;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient.ZGWClientException;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwEnkelvoudigInformatieObject;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwStatus;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwStatusType;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaak;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakInformatieObject;

@Service
public class ZaakService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private ZGWClient zgwClient;

	@Autowired
	private ZaakTranslator zaakTranslator;

	public ZgwZaak creeerZaak(ZakLk01_v2 zakLk01) throws ZGWClientException, ZaakTranslatorException {
		// zaakTranslator.setDocument((Document) zakLk01).zdsZaakToZgwZaak();
		this.zaakTranslator.setZakLk01(zakLk01).zdsZaakToZgwZaak();

		ZgwZaak zaak = this.zaakTranslator.getZgwZaak();

		var createdZaak = this.zgwClient.addZaak(zaak);
		// hij heeft toch altijd een object, anders exception
		if (createdZaak.getUrl() != null) {			
		    //<ZKN:heeftBetrekkingOp StUF:entiteittype="ZAKOBJ" StUF:verwerkingssoort="T">
		    //<ZKN:heeftAlsBelanghebbende StUF:entiteittype="ZAKBTRBLH" StUF:verwerkingssoort="T">
			//<ZKN:heeftAlsInitiator StUF:entiteittype="ZAKBTRINI" StUF:verwerkingssoort="T">        
			//<ZKN:heeftAlsUitvoerende StUF:entiteittype="ZAKBTRUTV" StUF:verwerkingssoort="T">
		    //<ZKN:heeftAlsVerantwoordelijke StUF:entiteittype="ZAKBTRVRA" StUF:verwerkingssoort="T">	
					
			log.info("Created a ZGW Zaak with UUID: " + createdZaak.getUuid());
			var rol = this.zaakTranslator.getRolInitiator();
			if (rol != null) {
				rol.setZaak(createdZaak.getUrl());
				this.zgwClient.addRolNPS("heeftAlsInitiator", rol);
			}
			rol = this.zaakTranslator.getRolUitvoerende();
			if (rol != null) {
				rol.setZaak(createdZaak.getUrl());
				this.zgwClient.addRolNPS("heeftAlsUitvoerende", rol);
			}
			
			return createdZaak;
		}
		return null;
	}

	public Document getZaakDetails(ZakLv01 zakLv01) throws Exception {
		ZgwZaak zgwZaak = getZaak(zakLv01.getIdentificatie());

		this.zaakTranslator.setZgwZaak(zgwZaak);
		this.zaakTranslator.zgwZaakToZakLa01();

		return this.zaakTranslator.getDocument();

	}

	public Document getLijstZaakdocumenten(ZakLv01 zakLv01) throws Exception {
		ZgwZaak zgwZaak = getZaak(zakLv01.getIdentificatie());

		Map<String, String> parameters = new HashMap();
		parameters.put("zaak", zgwZaak.getUrl());

		var zaakInformatieObjecten = this.zgwClient.getLijstZaakDocumenten(parameters);

		this.zaakTranslator.setZgwEnkelvoudigInformatieObjectList(zaakInformatieObjecten);
		this.zaakTranslator.zgwEnkelvoudingInformatieObjectenToZSDLijstZaakDocumenten();

		return this.zaakTranslator.getDocument();

	}

	public ZgwZaakInformatieObject voegZaakDocumentToe(EdcLk01 edcLk01) throws Exception {
		ZgwZaakInformatieObject result = null;

		this.zaakTranslator.setEdcLk01(edcLk01).zdsDocumentToZgwDocument();

		ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = this.zgwClient
				.addDocument(this.zaakTranslator.getZgwEnkelvoudigInformatieObject());

		if (zgwEnkelvoudigInformatieObject.getUrl() != null) {
			String zaakUrl = getZaak(edcLk01.objects.get(0).isRelevantVoor.gerelateerde.identificatie).url;
			result = addZaakInformatieObject(zgwEnkelvoudigInformatieObject, zaakUrl);
		} else {
			throw new Exception("Document not added");
		}
		return result;
	}

	private ZgwZaakInformatieObject addZaakInformatieObject(ZgwEnkelvoudigInformatieObject doc, String zaakUrl)
			throws Exception {
		ZgwZaakInformatieObject result = null;
		try {
			var zgwZaakInformatieObject = new ZgwZaakInformatieObject();
			zgwZaakInformatieObject.setZaak(zaakUrl);
			zgwZaakInformatieObject.setInformatieobject(doc.getUrl());
			zgwZaakInformatieObject.setTitel(doc.getTitel());
			result = this.zgwClient.addDocumentToZaak(zgwZaakInformatieObject);

		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	private ZgwZaak getZaak(String zaakIdentificatie) throws ZGWClientException {
		Map<String, String> parameters = new HashMap();
		parameters.put("identificatie", zaakIdentificatie);

		return this.zgwClient.getZaakDetails(parameters);
	}

	public EdcLa01 getZaakDoumentLezen(EdcLv01 edcLv01) throws ZGWClientException {
		EdcLa01 edcLa01 = new EdcLa01();

		ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject = this.zgwClient
				.getZgwEnkelvoudigInformatieObject(edcLv01.gelijk.identificatie);

		edcLa01 = this.zaakTranslator.getEdcLa01FromZgwEnkelvoudigInformatieObject(zgwEnkelvoudigInformatieObject);

		return edcLa01;
	}

	private ZgwStatusType getStatusTypeByZaakTypeAndVolgnummer(String zaakTypeUrl, int volgnummer)
			throws ZGWClientException {
		Map<String, String> parameters = new HashMap();
		parameters.put("zaaktype", zaakTypeUrl);

		return this.zgwClient.getStatusTypes(parameters).stream()
				.filter(zgwStatusType -> zgwStatusType.volgnummer == volgnummer).findFirst().orElse(null);
	}

	public ZgwZaak actualiseerZaakstatus(ZakLk01_v2 zakLk01) throws ZGWClientException {
		ZakLk01_v2.Object object = zakLk01.objects.get(1);
		ZgwZaak zgwZaak = getZaak(object.identificatie);

		this.zaakTranslator.setZakLk01(zakLk01);
		ZgwStatus zgwStatus = this.zaakTranslator.getZgwStatus();
		zgwStatus.zaak = zgwZaak.url;
		zgwStatus.statustype = getStatusTypeByZaakTypeAndVolgnummer(zgwZaak.zaaktype,
				Integer.valueOf(object.heeft.gerelateerde.volgnummer)).url;

		this.zgwClient.actualiseerZaakStatus(zgwStatus);
		return zgwZaak;
	}
}
