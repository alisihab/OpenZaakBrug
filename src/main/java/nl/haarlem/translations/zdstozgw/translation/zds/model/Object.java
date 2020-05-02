package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Object {
	@XmlAttribute(namespace = STUF)
	public String entiteittype;

	@XmlElement(namespace = ZKN)
	public String identificatie;

	@XmlElement(namespace = ZKN)
	public String omschrijving;

//    niet in standaard?
//    @XmlElement(namespace = ZKN)
//    public String startdatum;

	@XmlElement(namespace = ZKN)
	public String registratiedatum;

//      niet in standaard?
//      @XmlElement(namespace = ZKN)
//      public GerelateerdeWrapper isVan;

	@XmlElement(namespace = ZKN)
	public Heeft heeft;

}
