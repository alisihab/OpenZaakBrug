package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlRootElement(namespace = ZKN, name = "zakLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsZakLa01GeefZaakDetails extends ZdsDocument{

    @XmlElement(namespace = ZKN)
    public ZdsParameters parameters;


    @XmlElement(namespace = ZKN)
    public Antwoord antwoord;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Antwoord {
        @XmlElement(namespace = ZKN, name = "object")
        public Object zaak;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Object extends ZdsZaak {
            @XmlElement(namespace = ZKN)
            public List<Status> heeft;
        }
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Status {
        @XmlAttribute(namespace = STUF)
        public String entiteittype;

        @XmlElement(namespace = ZKN)
        public String toelichting;

        @XmlElement(namespace = ZKN)
        public String datumStatusGezet;

        @XmlElement(namespace = ZKN)
        public String indicatieLaatsteStatus;

        @XmlElement(namespace = ZKN)
        public String isGezetDoor;
    }

}