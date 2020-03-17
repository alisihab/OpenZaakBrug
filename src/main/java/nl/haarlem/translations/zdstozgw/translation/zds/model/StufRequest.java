package nl.haarlem.translations.zdstozgw.translation.zds.model;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import static nl.haarlem.translations.zdstozgw.utils.XmlUtils.xmlNodesToDocument;

public class StufRequest {

    public Document body;

    public StufRequest(Document _soapDocument) {
        body = _soapDocument;
    }

    public boolean isVoegZaakdocumentToe() {
        NodeList actualiseerZaakstatusZakLk01Nodes = body.getElementsByTagNameNS("http://www.stufstandaarden.nl/koppelvlak/zds0120", "voegZaakdocumentToe_EdcLk01");
        return actualiseerZaakstatusZakLk01Nodes.getLength() > 0;
    }

    public boolean isActualiseerZaakstatus() {
        NodeList actualiseerZaakstatusZakLk01Nodes = body.getElementsByTagNameNS("http://www.stufstandaarden.nl/koppelvlak/zds0120", "actualiseerZaakstatus_ZakLk01");
        return actualiseerZaakstatusZakLk01Nodes.getLength() > 0;
    }

    public boolean isgeefZaakDetails() {
        NodeList geefZaakdetails_zakLv01Nodes = body.getElementsByTagNameNS("http://www.stufstandaarden.nl/koppelvlak/zds0120", "geefZaakdetails_ZakLv01");
        return geefZaakdetails_zakLv01Nodes.getLength() > 0;
    }

    public boolean isgeefLijstZaakdocumenten() {
        NodeList geefZaakdetails_zakLv01Nodes = body.getElementsByTagNameNS("http://www.stufstandaarden.nl/koppelvlak/zds0120", "geefLijstZaakdocumenten_ZakLv01");
        return geefZaakdetails_zakLv01Nodes.getLength() > 0;
    }

    public boolean isCreeerZaak() {
        NodeList creeerZaakZakLk01Nodes = body.getElementsByTagNameNS("http://www.stufstandaarden.nl/koppelvlak/zds0120", "creeerZaak_ZakLk01");
        return creeerZaakZakLk01Nodes.getLength() > 0;
    }

    public ZakLv01 getZakLv01ZaakDetails() {
        NodeList nodes = body.getElementsByTagNameNS("http://www.stufstandaarden.nl/koppelvlak/zds0120", "geefZaakdetails_ZakLv01").item(0).getChildNodes();

        return new ZakLv01(xmlNodesToDocument(nodes, "geefZaakdetails_ZakLa01"));
    }

    public ZakLv01 getZakLv01LijstZaakdocumenten() {
        NodeList nodes = body.getElementsByTagNameNS("http://www.stufstandaarden.nl/koppelvlak/zds0120", "geefLijstZaakdocumenten_ZakLv01").item(0).getChildNodes();

        return new ZakLv01(xmlNodesToDocument(nodes, "geefLijstZaakdocumenten_ZakLa01"));
    }

    public ZakLk01 getZakLk01() {
        var nodes = body.getElementsByTagNameNS("http://www.egem.nl/StUF/sector/zkn/0310", "zakLk01").item(0).getChildNodes();

        return new ZakLk01(xmlNodesToDocument(nodes, "creeerZaak_ZakLk01"));
    }

    public EdcLk01 getEdcLk01() {
        var nodes = body.getElementsByTagNameNS("http://www.stufstandaarden.nl/koppelvlak/zds0120", "voegZaakdocumentToe_EdcLk01").item(0).getChildNodes();

        return new EdcLk01(xmlNodesToDocument(nodes, "voegZaakdocumentToe_EdcLk01"));
    }

}
