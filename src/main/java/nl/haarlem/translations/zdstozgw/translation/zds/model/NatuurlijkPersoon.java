package nl.haarlem.translations.zdstozgw.translation.zds.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.BG;

@XmlAccessorType(XmlAccessType.FIELD)
public class NatuurlijkPersoon {

    @XmlElement(namespace = BG, name = "inp.bsn")
    public String bsn;

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
}
