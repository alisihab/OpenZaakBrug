package nl.haarlem.translations.zdstozgw.translation;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.DocumentType;
import nl.haarlem.translations.zdstozgw.config.Organisatie;
import nl.haarlem.translations.zdstozgw.config.ZaakType;
import nl.haarlem.translations.zdstozgw.translation.zds.model.HeeftRelevantEDC;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLa01LijstZaakdocumenten;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwEnkelvoudigInformatieObject;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLa01Zaakdetails;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.BetrokkeneIdentificatieNPS;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.RolNPS;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
@Data
public class ZaakTranslator {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private ConfigService configService;

    private Document document;
    private ZgwZaak zgwZaak;
    private ZgwEnkelvoudigInformatieObject zgwEnkelvoudigInformatieObject;
    private List<ZgwEnkelvoudigInformatieObject> zgwEnkelvoudigInformatieObjectList;

    public ZaakTranslator() {

    }

    public void zgwZaakToZakLa01() throws Exception {
        if (zgwZaak == null) {
            throw new Exception("ZGW zaak is null");
        }

        var zakLa01 = new ZakLa01Zaakdetails();
        zakLa01.setIdentificatie(zgwZaak.getIdentificatie());
        zakLa01.setOmschrijving(zgwZaak.getOmschrijving());
        zakLa01.setToelichting(zgwZaak.getToelichting());
        if (zgwZaak.getResultaat() != null) {
            //TODO Fetch resultaat for this zaak from ZGW API
            zakLa01.setResultaat("TODO", "Fetch resultaat for this zaak from ZGW API");
        } else {
            zakLa01.setEmptyResultaat();
        }
        zakLa01.setStartDatum(getStufDateFromDateString(zgwZaak.getStartdatum()));
        zakLa01.setRegistratieDatum(getStufDateFromDateString(zgwZaak.getRegistratiedatum()));
        zakLa01.setPublicatieDatum(getStufDateFromDateString(zgwZaak.getPublicatiedatum()));
        zakLa01.setEinddatumGepland(getStufDateFromDateString(zgwZaak.getEinddatumGepland()));
        zakLa01.setUiterlijkeEinddatum(getStufDateFromDateString(zgwZaak.getUiterlijkeEinddatumAfdoening()));
        zakLa01.setEinddatum(getStufDateFromDateString(zgwZaak.getEinddatum()));
        zakLa01.setArchiefNominatie(getZDSArchiefNominatie(zgwZaak.getArchiefnominatie()));
        zakLa01.setDatumVernietigingDossier(getStufDateFromDateString(zgwZaak.getArchiefactiedatum()));
        var zaakType = getZaakTypeByZGWZaakType(zgwZaak.getZaaktype());
        zakLa01.setZaakTypeOmschrijving(zaakType.getZaakTypeOmschrijving());
        zakLa01.setZaakTypeCode(zaakType.getCode());
        zakLa01.setZaakTypeIngangsDatumObject(zaakType.getIngangsdatumObject());

        this.document = zakLa01.getDocument();
    }

    public void zgwEnkelvoudingInformatieObjectenToZSDLijstZaakDocumenten(){
        var zakLa01 = new ZakLa01LijstZaakdocumenten();

        zgwEnkelvoudigInformatieObjectList.forEach(document -> {
            zgwDocumentToZgwDocument(zakLa01, document);
        });

        document =  zakLa01.getDocument();
    }

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


    public void zdsDocumentToZgwDocument(){
        var xpath = new XpathDocument(document);
        var identificatie = xpath.getNodeValue("//zkn:object/zkn:identificatie");
        var bronOrganisatie = getRSIN(xpath.getNodeValue("//zkn:stuurgegevens/stuf:ontvanger/stuf:organisatie"));
        var creatieDatum = getDateStringFromStufDate(xpath.getNodeValue("//zkn:object/zkn:creatiedatum"));
        var titel = xpath.getNodeValue("//zkn:object/zkn:titel");
        var vertrouwlijkheidaanduiding = xpath.getNodeValue("//zkn:object/zkn:vertrouwelijkAanduiding").toLowerCase();
        var auteur = xpath.getNodeValue("//zkn:object/zkn:auteur");
        var formaat = xpath.getNodeValue("//zkn:object/zkn:formaat");
        var taal = xpath.getNodeValue("//zkn:object/zkn:taal");
        var bestandsnaam = xpath.getAttributeValue("//zkn:object/zkn:inhoud","http://www.egem.nl/StUF/StUF0301","bestandsnaam");
        var inhoud = xpath.getNodeValue("//zkn:object/zkn:inhoud");
        var informatieObjectType = configService.getConfiguratie().getDocumentTypes().get(0).getDocumentType();

        var eio = new ZgwEnkelvoudigInformatieObject();
        eio.setIdentificatie(identificatie);
        eio.setBronorganisatie(bronOrganisatie);
        eio.setCreatiedatum(creatieDatum);
        eio.setTitel(titel);
        eio.setVertrouwelijkheidaanduiding(vertrouwlijkheidaanduiding);
        eio.setAuteur(auteur);
        eio.setTaal(taal);
        eio.setFormaat(formaat);
        eio.setInhoud(inhoud);
        eio.setInformatieobjecttype(informatieObjectType);
        eio.setBestandsnaam(bestandsnaam);

        zgwEnkelvoudigInformatieObject = eio;
    }

    public void zdsZaakToZgwZaak() {

        var xpath = new XpathDocument(document);

        var identificatie = xpath.getNodeValue("//zkn:object/zkn:identificatie");
        var bronOrganisatie = getRSIN(xpath.getNodeValue("//zkn:stuurgegevens/stuf:ontvanger/stuf:organisatie"));
        var verantwoordelijkeOrganisatie = getRSIN(xpath.getNodeValue("//zkn:stuurgegevens/stuf:ontvanger/stuf:organisatie"));
        var zaaktype = getZaakTypeByZDSCode(xpath.getNodeValue("//zkn:object/zkn:isVan/zkn:gerelateerde/zkn:code")).getZaakType();
        var omschrijving = xpath.getNodeValue("//zkn:object/zkn:omschrijving");
        var toelichting = xpath.getNodeValue("//zkn:object/zkn:toelichting");
        var archiefNominatie = getZGWArchiefNominatie(getZGWArchiefNominatie(xpath.getNodeValue("//zkn:object/zkn:archiefnominatie")));
        var startDatum = getDateStringFromStufDate(xpath.getNodeValue("//zkn:object/zkn:startdatum"));
        var registratiedatum = getDateStringFromStufDate(xpath.getNodeValue("//zkn:object/zkn:registratiedatum"));
        var einddatumGepland = getDateStringFromStufDate(xpath.getNodeValue("//zkn:object/zkn:einddatumGepland"));

        var zaak = new ZgwZaak();
        zaak.setIdentificatie(identificatie)
                .setBronorganisatie(bronOrganisatie)
                .setOmschrijving(omschrijving)
                .setToelichting(toelichting)
                .setZaaktype(zaaktype)
                .setRegistratiedatum(registratiedatum)
                .setVerantwoordelijkeOrganisatie(verantwoordelijkeOrganisatie)
                .setStartdatum(startDatum)
                .setEinddatumGepland(einddatumGepland)
                .setArchiefnominatie(archiefNominatie);

        this.zgwZaak = zaak;
    }

    public RolNPS getRolInitiator() {
        var nodes = document.getElementsByTagNameNS("http://www.egem.nl/StUF/sector/zkn/0310", "heeftAlsInitiator");
        if (nodes.getLength() > 0) {
            var xpath = new XpathDocument(document);
            BetrokkeneIdentificatieNPS nps = new BetrokkeneIdentificatieNPS();
            nps.setInpBsn(xpath.getNodeValue("//zkn:object/zkn:heeftAlsInitiator/zkn:gerelateerde/zkn:natuurlijkPersoon/bg:inp.bsn"));
            nps.setGeslachtsnaam(xpath.getNodeValue("//zkn:object/zkn:heeftAlsInitiator/zkn:gerelateerde/zkn:natuurlijkPersoon/bg:geslachtsnaam"));
            nps.setVoorvoegselGeslachtsnaam(xpath.getNodeValue("//zkn:object/zkn:heeftAlsInitiator/zkn:gerelateerde/zkn:natuurlijkPersoon/bg:voorvoegselGeslachtsnaam"));
            nps.setVoornamen(xpath.getNodeValue("//zkn:object/zkn:heeftAlsInitiator/zkn:gerelateerde/zkn:natuurlijkPersoon/bg:voornamen"));
            nps.setGeboortedatum(getDateStringFromStufDate(xpath.getNodeValue("//zkn:object/zkn:heeftAlsInitiator/zkn:gerelateerde/zkn:natuurlijkPersoon/bg:geboortedatum")));
            nps.setGeslachtsaanduiding(xpath.getNodeValue("//zkn:object/zkn:heeftAlsInitiator/zkn:gerelateerde/zkn:natuurlijkPersoon/bg:geslachtsaanduiding").toLowerCase());

            var rol = new RolNPS();
            rol.setBetrokkeneIdentificatieNPS(nps);
            rol.setBetrokkeneType("natuurlijk_persoon");
            rol.setRoltoelichting("Inititator");
            rol.setRoltype(getZaakTypeByZDSCode(xpath.getNodeValue(xpath.getNodeValue("//zkn:object/zkn:isVan/zkn:gerelateerde/zkn:code"))).getInitiatorRolTypeUrl());

            return rol;
        } else {
            return null;
        }

    }


    private String getStufDateFromDateString(String dateString) {
        if (dateString == null) {
            return null;
        }
        var year = dateString.substring(0, 4);
        var month = dateString.substring(5, 7);
        var day = dateString.substring(8, 10);
        return year + month + day;
    }

    private String getDateStringFromStufDate(String stufDate) {

        var year = stufDate.substring(0, 4);
        var month = stufDate.substring(4, 6);
        var day = stufDate.substring(6, 8);
        return year + "-" + month + "-" + day;
    }

    private String getZGWArchiefNominatie(String archiefNominatie) {
        if (archiefNominatie.toUpperCase().equals("J")) {
            return "vernietigen";
        } else {
            return "blijvend_bewaren";
        }
    }

    private String getZDSArchiefNominatie(String archiefNominatie) {
        if (archiefNominatie.toUpperCase().equals("vernietigen")) {
            return "J";
        } else {
            return "N";
        }
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

    private ZaakType getZaakTypeByZDSCode(String zaakTypeCode) {
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
