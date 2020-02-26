package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.StufUtils;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@Data
public class ZakLa01LijstZaakdocumenten {

    private final XpathDocument xpathDocument;
    private Document document;

    public ZakLa01LijstZaakdocumenten() {
        getBaseDocument();
        xpathDocument = new XpathDocument(document);
        xpathDocument.setNodeValue("//stuf:tijdstipBericht", StufUtils.getTijdstipBericht());
    }

    private void getBaseDocument() {
        document = XmlUtils.convertStringToDocument("<soap11env:Envelope xmlns:soap11env=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tns=\"http://www.stufstandaarden.nl/koppelvlak/zds0120\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "    <soap11env:Body>\n" +
                "        <tns:geefLijstZaakdocumenten_ZakLa01>\n" +
                "            <ns2:stuurgegevens xmlns:ns2=\"http://www.egem.nl/StUF/sector/zkn/0310\">\n" +
                "                <ns0:berichtcode xmlns:ns0=\"http://www.egem.nl/StUF/StUF0301\">La01</ns0:berichtcode>\n" +
                "                <ns3:zender xmlns:ns3=\"http://www.egem.nl/StUF/StUF0301\">\n" +
                "                    <ns3:organisatie>0392</ns3:organisatie>\n" +
                "                    <ns3:applicatie>ZSH</ns3:applicatie>\n" +
                "                </ns3:zender>\n" +
                "                <ns4:ontvanger xmlns:ns4=\"http://www.egem.nl/StUF/StUF0301\">\n" +
                "                    <ns4:organisatie>0392</ns4:organisatie>\n" +
                "                    <ns4:applicatie>ZSH</ns4:applicatie>\n" +
                "                </ns4:ontvanger>\n" +
                "                <ns0:referentienummer xmlns:ns0=\"http://www.egem.nl/StUF/StUF0301\">0392cb21c065-063a-4c06-a007-e99367a5f51e</ns0:referentienummer>\n" +
                "                <ns0:tijdstipBericht xmlns:ns0=\"http://www.egem.nl/StUF/StUF0301\">20200214172056</ns0:tijdstipBericht>\n" +
                "                <ns0:crossRefnummer xmlns:ns0=\"http://www.egem.nl/StUF/StUF0301\">1</ns0:crossRefnummer>\n" +
                "                <ns0:entiteittype xmlns:ns0=\"http://www.egem.nl/StUF/StUF0301\">ZAK</ns0:entiteittype>\n" +
                "            </ns2:stuurgegevens>\n" +
                "            <ns5:parameters xmlns:ns5=\"http://www.egem.nl/StUF/sector/zkn/0310\">\n" +
                "                <ns0:indicatorVervolgvraag xmlns:ns0=\"http://www.egem.nl/StUF/StUF0301\">false</ns0:indicatorVervolgvraag>\n" +
                "                <ns0:indicatorAfnemerIndicatie xmlns:ns0=\"http://www.egem.nl/StUF/StUF0301\">false</ns0:indicatorAfnemerIndicatie>\n" +
                "                <ns0:aantalVoorkomens xmlns:ns0=\"http://www.egem.nl/StUF/StUF0301\">0</ns0:aantalVoorkomens>\n" +
                "            </ns5:parameters>\n" +
                "            <ns6:antwoord xmlns:ns6=\"http://www.egem.nl/StUF/sector/zkn/0310\">\n" +
                "                <ns6:object ns11:entiteittype=\"ZAK\" xmlns:ns11=\"http://www.egem.nl/StUF/StUF0301\">\n" +
                "                    <ns6:identificatie xsi:nil=\"true\" ns11:noValue=\"geenWaarde\"/>\n" +
                "                </ns6:object>\n" +
                "            </ns6:antwoord>\n" +
                "        </tns:geefLijstZaakdocumenten_ZakLa01>\n" +
                "    </soap11env:Body>\n" +
                "</soap11env:Envelope>\n");
    }

    public void addHeeftRelevant(HeeftRelevantEDC heeftRelevantEDC) {
        Node gerelateerde = document.importNode(heeftRelevantEDC.getHeeftRelevant(), true);
        xpathDocument.insertNode("//zkn:antwoord/zkn:object",gerelateerde);
    }

}
