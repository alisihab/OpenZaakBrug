package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.MIME;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class Inhoud {
	@XmlAttribute(namespace = STUF)
	public String bestandsnaam;

	@XmlAttribute(namespace = MIME)
	public String contentType;

	@XmlValue()
	public String value;
}