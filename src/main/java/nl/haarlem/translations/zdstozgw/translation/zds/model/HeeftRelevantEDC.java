package nl.haarlem.translations.zdstozgw.translation.zds.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

@Data
public class HeeftRelevantEDC {

	private Node heeftRelevant;
	private Document document;

	public HeeftRelevantEDC() {
		getBaseDocument();
		getBaseNode();
	}

	private void getBaseDocument() {
		this.document = XmlUtils.convertStringToDocument("<root></root>");
	}

	private void getBaseNode() {
		Element heeftRelevant = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310", "heeftRelevant");
		heeftRelevant.setAttributeNS("http://www.egem.nl/StUF/StUF0301", "entiteittype", "ZAKEDC");
		Element gerelateerde = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310", "gerelateerde");
		gerelateerde.setAttributeNS("http://www.egem.nl/StUF/StUF0301", "entiteittype", "EDC");

		heeftRelevant.appendChild(gerelateerde);

		this.heeftRelevant = (Node) heeftRelevant;
	}

	public void setTitel(String titel) {
		Element el = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "titel");
		el.setTextContent(titel);
		Element el2 = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "titel");
		el2.setTextContent(titel);
		this.heeftRelevant.appendChild(el);
		this.heeftRelevant.getChildNodes().item(0).appendChild(el2);
	}

	public void setDctOmschrijving(String dctOmschrijving) {
		Element el = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "dct.omschrijving");
		el.setTextContent(dctOmschrijving);
		this.heeftRelevant.getChildNodes().item(0).appendChild(el);
	}

	public void setIdentificatie(String identificatie) {
		Element el = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "identificatie");
		el.setTextContent(identificatie);
		this.heeftRelevant.getChildNodes().item(0).appendChild(el);
	}

	public void setOntvangstDatum(String ontvangstDatum) {
		Element el = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "ontvangstdatum");
		el.setTextContent(ontvangstDatum);
		this.heeftRelevant.getChildNodes().item(0).appendChild(el);
	}

	public void setBeschrijving(String beschrijving) {
		Element el = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "beschrijving");
		el.setTextContent(beschrijving);
		this.heeftRelevant.getChildNodes().item(0).appendChild(el);
	}

	public void setFormaat(String formaat) {
		Element el = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "foraat");
		el.setTextContent(formaat);
		this.heeftRelevant.getChildNodes().item(0).appendChild(el);
	}

	public void setTaal(String taal) {
		Element el = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "taal");
		el.setTextContent(taal);
		this.heeftRelevant.getChildNodes().item(0).appendChild(el);
	}

	public void setVersie(String versieum) {
		Element el = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "versie");
		el.setTextContent(versieum);
		this.heeftRelevant.getChildNodes().item(0).appendChild(el);
	}

	public void setCreatieDatum(String creatieDatum) {
		Element el = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "creatiedatum");
		el.setTextContent(creatieDatum);
		this.heeftRelevant.getChildNodes().item(0).appendChild(el);
	}

	public void setStatus(String status) {
		Element el = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "status");
		el.setTextContent(status);
		this.heeftRelevant.getChildNodes().item(0).appendChild(el);
	}

	public void setVerzendDatum(String verzendDatum) {
		Element el = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "verzenddatum");
		el.setTextContent(verzendDatum);
		this.heeftRelevant.getChildNodes().item(0).appendChild(el);
	}

	public void setVertrouwelijkAanduiding(String vertrouwelijkAanduiding) {
		Element el = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "vertrouwelijkAanduiding");
		el.setTextContent(vertrouwelijkAanduiding);
		this.heeftRelevant.getChildNodes().item(0).appendChild(el);
	}

	public void setAuteur(String auteur) {
		Element el = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "auteur");
		el.setTextContent(auteur);
		this.heeftRelevant.getChildNodes().item(0).appendChild(el);
	}

	public void setLink(String link) {
		Element el = this.document.createElementNS("http://www.egem.nl/StUF/sector/zkn/0310\"", "link");
		el.setTextContent(link);
		this.heeftRelevant.getChildNodes().item(0).appendChild(el);
	}
}
