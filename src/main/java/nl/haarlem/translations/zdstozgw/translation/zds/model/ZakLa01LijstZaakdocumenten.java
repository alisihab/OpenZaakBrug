package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;
import javax.xml.bind.annotation.*;

import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlRootElement(namespace = ZKN, name = "ZakLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZakLa01LijstZaakdocumenten {

    @XmlElement(namespace = ZKN)
    public Antwoord antwoord;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Antwoord{
        @XmlElement(namespace = ZKN)
        public ZakLa01LijstZaakdocumenten.Antwoord.Object object;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Object{

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
                public ZaakDocument gerelateerde;

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
