package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.BG;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsNatuurlijkPersoon extends ZdsObject {

	@XmlAttribute(namespace = STUF)
	public String entiteittype;

	@XmlElement(namespace = BG, name = "inp.bsn")
	public String bsn;

	@XmlElement(namespace = BG)
	public ZdsAuthentiek authentiek = new ZdsAuthentiek();

	@XmlElement(namespace = BG)
	public String geslachtsnaam;

	@XmlElement(namespace = BG)
	public String voorvoegselGeslachtsnaam;

	@XmlElement(namespace = BG)
	public String voorletters;

	@XmlElement(namespace = BG)
	public String voornamen;

	@XmlElement(namespace = BG)
	public String geslachtsaanduiding;

	@XmlElement(namespace = BG)
	public String geboortedatum;

	@XmlElement(namespace = BG)
	public ZdsAdres verblijfsadres;
}
