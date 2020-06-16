package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsRelatieZaakDocument {
	@XmlAttribute(namespace = STUF)
	public String entiteittype;
		
	@XmlElement(namespace = ZKN)
	//public List<ZdsZaakDocument> gerelateerde;	
	public ZdsZaakDocument gerelateerde;
	
	@XmlElement(namespace = ZKN)
	public String titel;

}