package nl.haarlem.translations.zdstozgw.translation.zds.model;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import nl.haarlem.translations.zdstozgw.utils.StufUtils;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;

// Voor nu depricated, misschien later nog eens gebruiken, maar de createBaseDocument gebruikt een harde string
@Deprecated
public class F03 {
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private Document document;
	private XpathDocument xpathDocument;

	public F03() {
		createBaseDocument();
		this.xpathDocument = new XpathDocument(this.document);
		this.xpathDocument.setNodeValue("//stuf:tijdstipBericht", StufUtils.getTijdstipBericht());
	}

	public String getSoapMessageAsString() {
		return XmlUtils.getSoapMessageAsString(this.document);
	}

	public void setFaultString(String faultString) {
		this.xpathDocument.setNodeValue("//faultstring", faultString);
	}

	public void setCode(String code) {
		this.xpathDocument.setNodeValue("//stuf:code", code);
	}

	public void setOmschrijving(String omschrijving) {
		this.xpathDocument.setNodeValue("//stuf:omschrijving", omschrijving);
	}

	public void setDetails(String details) {
		this.xpathDocument.setNodeValue("//stuf:details", details);
	}

	private void createBaseDocument() {
		this.document = XmlUtils.convertStringToDocument(
				"<soap11env:Envelope xmlns:soap11env=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
						+ "   <soap11env:Body>\n" + "      <soap11env:Fault>\n"
						+ "         <faultcode>soap11env:server</faultcode>\n"
						+ "         <faultstring>Object niet gevonden</faultstring>\n" + "         <faultactor/>\n"
						+ "         <detail>\n"
						+ "            <stuf:Fo03Bericht xmlns:stuf=\"http://www.egem.nl/StUF/StUF0301\">\n"
						+ "               <stuf:stuurgegevens>\n"
						+ "                  <stuf:berichtcode>Fo03</stuf:berichtcode>\n"
						+ "                  <stuf:zender>\n"
						+ "                     <stuf:organisatie>0392</stuf:organisatie>\n"
						+ "                     <stuf:applicatie>ZDS-ZGW-Translator</stuf:applicatie>\n"
						+ "                  </stuf:zender>\n" + "                  <stuf:ontvanger>\n"
						+ "                     <stuf:organisatie>0392</stuf:organisatie>\n"
						+ "                     <stuf:applicatie>TEST</stuf:applicatie>\n"
						+ "                  </stuf:ontvanger>\n"
						+ "                  <stuf:referentienummer>0392b6eef2dd-6aea-4365-b806-9afd32250d9b</stuf:referentienummer>\n"
						+ "                  <stuf:tijdstipBericht>20200206213813</stuf:tijdstipBericht>\n"
						+ "                  <stuf:crossRefnummer>e443d87f-69ee-4fd4-b1d9-dd797ffff628</stuf:crossRefnummer>\n"
						+ "               </stuf:stuurgegevens>\n" + "               <stuf:body>\n"
						+ "                  <stuf:code>StUF064</stuf:code>\n"
						+ "                  <stuf:plek>server</stuf:plek>\n"
						+ "                  <stuf:omschrijving>Object niet gevonden</stuf:omschrijving>\n"
						+ "                  <stuf:details>ZaakType matching query does not exist.</stuf:details>\n"
						+ "               </stuf:body>\n" + "            </stuf:Fo03Bericht>\n" + "         </detail>\n"
						+ "      </soap11env:Fault>\n" + "   </soap11env:Body>\n" + "</soap11env:Envelope>");

	}

}
