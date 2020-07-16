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
public class HeeftRelevant {
    @XmlAttribute(namespace = STUF)
    public String entiteittype;

    @XmlElement(namespace = ZKN)
    public ZaakDocument gerelateerde;

    @XmlElement(namespace = ZKN)
    public String titel;

    @XmlElement(namespace = ZKN)
    public String beschrijving;

    @XmlElement(namespace = ZKN)
    public String registratiedatum;
}
