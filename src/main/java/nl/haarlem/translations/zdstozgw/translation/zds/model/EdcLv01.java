package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = ZKN, name = "edcLv01")
@XmlAccessorType(XmlAccessType.FIELD)
public class EdcLv01 {

	@XmlElement(namespace = ZKN, name = "stuurgegevens")
	public Stuurgegevens stuurgegevens;

	@XmlElement(namespace = ZKN, name = "parameters")
	public Parameters parameters;	
	
	@XmlElement(namespace = ZKN, name = "gelijk")
	public Gelijk gelijk;
	
	@XmlElement(namespace = ZKN, name = "scope")
	public Scope scope;	
}
