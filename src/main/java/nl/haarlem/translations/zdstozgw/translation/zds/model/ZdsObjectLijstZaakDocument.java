package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;

import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsObjectLijstZaakDocument extends ZdsObject {
	
	@XmlAttribute(namespace = STUF)
	public String entiteittype = "ZAK";
	
	
	@XmlElement(namespace = ZKN, nillable = true)	
	public List<ZdsHeeftRelevant> heeftRelevant;	 
}