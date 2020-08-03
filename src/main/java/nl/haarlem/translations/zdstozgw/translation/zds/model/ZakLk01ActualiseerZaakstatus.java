package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlRootElement(namespace = ZKN, name="zakLk01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZakLk01ActualiseerZaakstatus {

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Object{
        @XmlAttribute(namespace = STUF)
        public String entiteittype;

        @XmlElement(namespace = ZKN)
        public String identificatie;

        @XmlElement(namespace = ZKN)
        public String omschrijving;

        @XmlElement(namespace = ZKN)
        public Heeft heeft;
    }


    @XmlElement(namespace = ZKN, name="object")
    public List<Object> objects;

    @XmlElement(namespace = ZKN, name="stuurgegevens")
    public Stuurgegevens stuurgegevens;

}
