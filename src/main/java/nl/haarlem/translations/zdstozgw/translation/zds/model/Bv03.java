package nl.haarlem.translations.zdstozgw.translation.zds.model;

import java.lang.invoke.MethodHandles;

import org.apache.http.annotation.Obsolete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.StufUtils;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;

@Obsolete
@Data
public class Bv03 {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private Document document;
	private XpathDocument xpathDocument;

	public Bv03() {
		this.document = createBaseDocument();
		this.xpathDocument = new XpathDocument(this.document);
		this.xpathDocument.setNodeValue("//stuf:tijdstipBericht", StufUtils.getTijdstipBericht());
	}

	public String getSoapMessageAsString() {
		return XmlUtils.getSoapMessageAsString(this.document);
	}

	public void setReferentienummer(String referentienummer) {
		this.xpathDocument.setNodeValue("//stuf:referentienummer", referentienummer);
	}

	private Document createBaseDocument() {
		var document = XmlUtils
				.convertStringToDocument("<stuf:Bv03Bericht xmlns:stuf=\"http://www.egem.nl/StUF/StUF0301\">\n"
						+ "            <stuf:stuurgegevens>\n"
						+ "                <stuf:berichtcode>Bv03</stuf:berichtcode>\n"
						+ "                <stuf:zender>\n"
						+ "                    <stuf:organisatie>0392</stuf:organisatie>\n"
						+ "                    <stuf:applicatie>ZDS-ZGW-Translator</stuf:applicatie>\n"
						+ "                </stuf:zender>\n" + "                <stuf:ontvanger>\n"
						+ "                    <stuf:organisatie>0392</stuf:organisatie>\n"
						+ "                    <stuf:applicatie>TEST</stuf:applicatie>\n"
						+ "                </stuf:ontvanger>\n"
						+ "                <stuf:referentienummer>039211d32cb2-b4b0-4a95-ae4d-ba7625824516</stuf:referentienummer>\n"
						+ "                <stuf:tijdstipBericht>20200206104009</stuf:tijdstipBericht>\n"
						+ "                <stuf:crossRefnummer>e443d87f-69ee-4fd4-b1d9-dd797ffff628</stuf:crossRefnummer>\n"
						+ "            </stuf:stuurgegevens>\n" + "        </stuf:Bv03Bericht>");
		return document;
	}

}
