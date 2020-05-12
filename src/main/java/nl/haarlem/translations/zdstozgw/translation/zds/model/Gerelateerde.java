package nl.haarlem.translations.zdstozgw.translation.zds.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

public class Gerelateerde {

    @XmlAttribute(namespace = STUF)
    public String entiteittype;

    @XmlElement(namespace = ZKN)
    public String identificatie;

    @XmlAttribute(namespace = STUF)
    public String verwerkingssoort;

    @XmlElement(namespace = ZKN)
    public String omschrijving;

    @XmlElement(namespace = ZKN)
    public String code;

    @XmlElement(namespace = ZKN)
    public String ingangsdatumObject;

    @XmlElement(namespace = ZKN, name = "zkt.code")
    public String zktCode;

    @XmlElement(namespace = ZKN, name = "zkt.omschrijving")
    public String zktOmschrijving;

    @XmlElement(namespace = ZKN)
    public String volgnummer;

    @XmlElement(namespace = ZKN)
    public Medewerker medewerker;

    @XmlElement(namespace = ZKN)
    public NatuurlijkPersoon natuurlijkPersoon;
}