package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@Data
public class HeeftRelevantEDC {

    private Node heeftRelevant;
    private Document document;

    public HeeftRelevantEDC() {
        getBaseDocument();
        getBaseNode();
    }

    private void getBaseDocument() {
        document = XmlUtils.convertStringToDocument("<root></root>");
    }


    private void getBaseNode() {
        Element heeftRelevant = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310", "heeftRelevant");
        heeftRelevant.setAttributeNS("http://www.egem.nl/StUF/StUF0301", "entiteittype", "ZAKEDC");
        Element gerelateerde = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310", "gerelateerde");
        gerelateerde.setAttributeNS("http://www.egem.nl/StUF/StUF0301", "entiteittype", "EDC");

        heeftRelevant.appendChild(gerelateerde);

        this.heeftRelevant = (Node) heeftRelevant;
    }

    public void setTitel(String titel) {
        Element el = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "titel");
        el.setTextContent(titel);
        Element el2 = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "titel");
        el2.setTextContent(titel);
        heeftRelevant.appendChild(el);
        heeftRelevant.getChildNodes().item(0).appendChild(el2);
    }

    public void setDctOmschrijving(String dctOmschrijving) {
        Element el = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "dct.omschrijving");
        el.setTextContent(dctOmschrijving);
        heeftRelevant.getChildNodes().item(0).appendChild(el);
    }


    public void setIdentificatie(String identificatie) {
        Element el = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "identificatie");
        el.setTextContent(identificatie);
        heeftRelevant.getChildNodes().item(0).appendChild(el);
    }

    public void setOntvangstDatum(String ontvangstDatum) {
        Element el = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "ontvangstdatum");
        el.setTextContent(ontvangstDatum);
        heeftRelevant.getChildNodes().item(0).appendChild(el);
    }

    public void setBeschrijving(String beschrijving) {
        Element el = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "beschrijving");
        el.setTextContent(beschrijving);
        heeftRelevant.getChildNodes().item(0).appendChild(el);
    }

    public void setFormaat(String formaat) {
        Element el = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "foraat");
        el.setTextContent(formaat);
        heeftRelevant.getChildNodes().item(0).appendChild(el);
    }

    public void setTaal(String taal) {
        Element el = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "taal");
        el.setTextContent(taal);
        heeftRelevant.getChildNodes().item(0).appendChild(el);
    }

    public void setVersie(String versieum) {
        Element el = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "versie");
        el.setTextContent(versieum);
        heeftRelevant.getChildNodes().item(0).appendChild(el);
    }

    public void setCreatieDatum(String creatieDatum) {
        Element el = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "creatiedatum");
        el.setTextContent(creatieDatum);
        heeftRelevant.getChildNodes().item(0).appendChild(el);
    }

    public void setStatus(String status) {
        Element el = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "status");
        el.setTextContent(status);
        heeftRelevant.getChildNodes().item(0).appendChild(el);
    }

    public void setVerzendDatum(String verzendDatum) {
        Element el = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "verzenddatum");
        el.setTextContent(verzendDatum);
        heeftRelevant.getChildNodes().item(0).appendChild(el);
    }

    public void setVertrouwelijkAanduiding(String vertrouwelijkAanduiding) {
        Element el = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "vertrouwelijkAanduiding");
        el.setTextContent(vertrouwelijkAanduiding);
        heeftRelevant.getChildNodes().item(0).appendChild(el);
    }

    public void setAuteur(String auteur) {
        Element el = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "auteur");
        el.setTextContent(auteur);
        heeftRelevant.getChildNodes().item(0).appendChild(el);
    }

    public void setLink(String link) {
        Element el = document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "link");
        el.setTextContent(link);
        heeftRelevant.getChildNodes().item(0).appendChild(el);
    }
}
