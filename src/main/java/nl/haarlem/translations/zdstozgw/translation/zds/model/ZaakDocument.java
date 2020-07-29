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
public class ZaakDocument {
    @XmlAttribute(namespace = STUF)
    public String entiteittype;

    @XmlElement(namespace = ZKN, nillable =  true)
    public String identificatie;

    @XmlElement(namespace = ZKN, nillable =  true)
    public String creatiedatum;

    @XmlElement(namespace = ZKN, nillable =  true)
    public String ontvangstdatum;

    @XmlElement(namespace = ZKN, nillable =  true)
    public String titel;

    @XmlElement(namespace = ZKN, nillable =  true)
    public String taal;

    @XmlElement(namespace = ZKN, nillable =  true)
    public String versie;

    @XmlElement(namespace = ZKN, nillable =  true)
    public String status;

    @XmlElement(namespace = ZKN, nillable =  true)
    public String vezenddatum;

    @XmlElement(namespace = ZKN, nillable =  true)
    public String vertrouwelijkheidAanduiding;

    @XmlElement(namespace = ZKN,nillable = true)
    public String auteur;

    @XmlElement(namespace = ZKN, nillable =  true)
    public String link;
}
