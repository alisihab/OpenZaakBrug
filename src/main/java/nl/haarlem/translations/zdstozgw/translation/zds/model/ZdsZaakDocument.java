package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsZaakDocument extends ZdsZaakDocumentIdentificatie {
	@XmlElement(namespace = ZKN, name = "dct.omschrijving")
	public String omschrijving;

	@XmlElement(namespace = ZKN)
	public String creatiedatum;

	@XmlElement(namespace = ZKN)
	public String ontvangstdatum;

	@XmlElement(namespace = ZKN)
	public String titel;

	@XmlElement(namespace = ZKN)
	public String beschrijving;

	@XmlElement(namespace = ZKN)
	public String formaat;

	@XmlElement(namespace = ZKN)
	public String taal;

	@XmlElement(namespace = ZKN)
	public String versie;

	@XmlElement(namespace = ZKN)
	public String status;

	@XmlElement(namespace = ZKN)
	public String verzenddatum;

	@XmlElement(namespace = ZKN)
	public String vertrouwelijkAanduiding;

	@XmlElement(namespace = ZKN)
	public String auteur;

	@XmlElement(namespace = ZKN)
	public String link;
}
