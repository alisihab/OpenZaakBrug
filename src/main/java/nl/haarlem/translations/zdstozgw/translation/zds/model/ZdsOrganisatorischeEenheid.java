package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsOrganisatorischeEenheid  extends ZdsObject {

    @XmlAttribute(namespace = STUF)
    public String entiteittype;

    @XmlElement(namespace = ZKN)
    public String identificatie;

    @XmlElement(namespace = ZKN)
    public String naam;

    @XmlElement(namespace = ZKN)
    public String naamVerkort;

    @XmlElement(namespace = ZKN)
    public String omschrijving;

    @XmlElement(namespace = ZKN)
    public String toelichting;

    @XmlElement(namespace = ZKN)
    public String telefoonnummer;

    @XmlElement(namespace = ZKN)
    public String faxnummer;

    @XmlElement(namespace = ZKN)
    public String emailadres;

    @XmlElement(namespace = ZKN)
    public IsGehuisvestIn isGehuisvestIn;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class IsGehuisvestIn {

        @XmlElement(namespace = ZKN)
        ZdsRol isEen;
    }

}
