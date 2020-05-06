package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Stuurgegevens {

	@XmlElement(namespace = STUF)
	public String berichtcode;

	@XmlElement(namespace = STUF)
	public Zender zender;

	@XmlElement(namespace = STUF)
	public Ontvanger ontvanger;
	
	@XmlElement(namespace = STUF)
	public String referentienummer;
}