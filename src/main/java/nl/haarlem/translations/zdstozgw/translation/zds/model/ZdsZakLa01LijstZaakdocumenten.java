package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlRootElement(namespace = ZKN, name = "zakLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsZakLa01LijstZaakdocumenten {

    @XmlElement(namespace = ZKN)
    public ZdsStuurgegevens stuurgegevens;

    @XmlElement(namespace = ZKN)
    public ZdsParameters parameters;

    @XmlElement(namespace = ZKN)
    public Antwoord antwoord;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Antwoord {
        @XmlElement(namespace = ZKN)
        public ZdsZakLa01LijstZaakdocumenten.Antwoord.Object object;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Object {

            @XmlElement(namespace = ZKN)
            public String identificatie;

            @XmlElement(namespace = ZKN)
            public List<HeeftRelevant> heeftRelevant;

            @Data
            @XmlAccessorType(XmlAccessType.FIELD)
            public static class HeeftRelevant {
                @XmlAttribute(namespace = STUF)
                public String entiteittype;

                @XmlElement(namespace = ZKN)
                public ZdsZaakDocument gerelateerde;

                @XmlElement(namespace = ZKN)
                public String titel;

                @XmlElement(namespace = ZKN)
                public String beschrijving;

                @XmlElement(namespace = ZKN)
                public String registratiedatum;
            }
        }
    }
}
