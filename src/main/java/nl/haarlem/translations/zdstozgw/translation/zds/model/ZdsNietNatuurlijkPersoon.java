package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.BG;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsNietNatuurlijkPersoon extends ZdsObject {

	@XmlAttribute(namespace = STUF)
	public String entiteittype;
	
	@XmlElement(namespace = BG, name = "inn.nnpId")
	public String innNnpId;
	
	@XmlElement(namespace = BG)
	public ZdsAuthentiek authentiek = new ZdsAuthentiek();	
	
	//@XmlElement(namespace = BG, name = "ann.identificatie")
	//public String annIdentificatie;

	@XmlElement(namespace = BG)
	public String statutaireNaam;

	@XmlElement(namespace = BG, name = "inn.rechtsvorm")
	public String innRechtsvorm;
	

	@XmlElement(namespace = ZKN)
	public ZdsAdres bezoekadres;
}
