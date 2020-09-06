package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsHeeftRelevant {
    @XmlAttribute(namespace = STUF)
    public String entiteittype = "ZAK";
    
        @XmlElement(namespace = ZKN)
        public ZdsZaakDocument gerelateerde;

        @XmlElement(namespace = ZKN)
        public String titel;

        @XmlElement(namespace = ZKN)
        public String beschrijving;

        @XmlElement(namespace = ZKN)
        public String registratiedatum;
}
