package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = ZKN, name = "scope")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsScope extends ZdsObject {
	@XmlElement(namespace = STUF)
	public String entiteittype = "ZAK";
	
	@XmlElement(namespace = ZKN, name = "object")
	public ZdsZaak object;

	@XmlAttribute(namespace = STUF)
	public String scope;
}