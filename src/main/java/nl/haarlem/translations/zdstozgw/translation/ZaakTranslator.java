package nl.haarlem.translations.zdstozgw.translation;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.DocumentType;
import nl.haarlem.translations.zdstozgw.config.model.Organisatie;
import nl.haarlem.translations.zdstozgw.config.model.ZaakType;
import nl.haarlem.translations.zdstozgw.translation.zds.model.*;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.*;
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
    private ZakLk01 zakLk01;
    private EdcLk01 edcLk01;

    public ZaakTranslator() {

    }

    public EdcLa01  getEdcLa01FromZgwEnkelvoudigInformatieObject(ZgwEnkelvoudigInformatieObject document){
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

        return  edcLa01;
    }

    public void zdsDocumentToZgwDocument(){
        var informatieObjectType = configService.getConfiguratie().getDocumentTypes().get(0).getDocumentType();

        var o = edcLk01.objects.get(0);
        var eio = new ZgwEnkelvoudigInformatieObject();
        eio.setIdentificatie(o.identificatie);
        eio.setBronorganisatie(getRSIN(edcLk01.stuurgegevens.zender.organisatie));
        eio.setCreatiedatum(getDateStringFromStufDate(o.creatiedatum));
        eio.setTitel(o.titel);
        eio.setVertrouwelijkheidaanduiding(o.vertrouwelijkAanduiding.toLowerCase());
        eio.setAuteur(o.auteur);
        eio.setTaal(o.taal);
        eio.setFormaat(o.formaat);
        eio.setInhoud(o.inhoud.value);
        eio.setInformatieobjecttype(informatieObjectType);
        eio.setBestandsnaam(o.inhoud.bestandsnaam);

        zgwEnkelvoudigInformatieObject = eio;
    }

    public void zdsZaakToZgwZaak() {

        var zaak = new ZgwZaak();
        var z = zakLk01.objects.get(0);
        zaak.setIdentificatie(z.identificatie)
                .setOmschrijving(z.omschrijving)
                .setToelichting(z.toelichting)
                .setZaaktype(getZaakTypeByZDSCode(z.isVan.gerelateerde.code).zaakType)
                .setRegistratiedatum(getDateStringFromStufDate(z.registratiedatum))
                .setVerantwoordelijkeOrganisatie(getRSIN(zakLk01.stuurgegevens.zender.organisatie))
                .setBronorganisatie(getRSIN(zakLk01.stuurgegevens.ontvanger.organisatie))
                .setStartdatum(getDateStringFromStufDate(z.startdatum))
                .setEinddatumGepland(getDateStringFromStufDate(z.einddatumGepland))
                .setArchiefnominatie(getZGWArchiefNominatie(z.archiefnominatie));

        this.zgwZaak = zaak;
    }

    public RolNPS getRolInitiator() {
        var z = zakLk01.objects.get(0);

        if (z.heeftAlsInitiator != null) {
            var natuurlijkPersoon = z.heeftAlsInitiator.gerelateerde.natuurlijkPersoon;
            BetrokkeneIdentificatieNPS nps = new BetrokkeneIdentificatieNPS();
            nps.setInpBsn(natuurlijkPersoon.bsn);
            nps.setGeslachtsnaam(natuurlijkPersoon.geslachtsnaam);
            nps.setVoorvoegselGeslachtsnaam(natuurlijkPersoon.voorvoegselGeslachtsnaam);
            nps.setVoornamen(natuurlijkPersoon.voornamen);
            nps.setGeboortedatum(getDateStringFromStufDate(natuurlijkPersoon.geboortedatum));
            nps.setGeslachtsaanduiding(natuurlijkPersoon.geslachtsaanduiding.toLowerCase());

            var rol = new RolNPS();
            rol.setBetrokkeneIdentificatieNPS(nps);
            rol.setBetrokkeneType("natuurlijk_persoon");
            rol.setRoltoelichting("Inititator");
            rol.setRoltype(getZaakTypeByZDSCode(z.isVan.gerelateerde.code).initiatorRolTypeUrl);

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

    private String getDateTimeStringFromStufDate(String stufDate) {

        var year = stufDate.substring(0, 4);
        var month = stufDate.substring(4, 6);
        var day = stufDate.substring(6, 8);
        var hours = stufDate.substring(8, 10);
        var minutes = stufDate.substring(10, 12);
        var seconds =  stufDate.substring(12, 14);
        var milliseconds = stufDate.substring(14);
        return year + "-" + month + "-" + day + "T" + hours + ":" + minutes + ":" + seconds + "." + milliseconds + "Z";
    }

    private String getZGWArchiefNominatie(String archiefNominatie) {
        if (archiefNominatie.toUpperCase().equals("J")) {
            return "vernietigen";
        } else {
            return "blijvend_bewaren";
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

    public ZgwStatus getZgwStatus() {
        ZakLk01.Object object = zakLk01.getObjects().get(1);
        ZgwStatus zgwStatus = new ZgwStatus();
        zgwStatus.statustoelichting = object.heeft.statustoelichting;
        zgwStatus.datumStatusGezet = getDateTimeStringFromStufDate(object.heeft.datumStatusGezet);
        return zgwStatus;
    }
}
