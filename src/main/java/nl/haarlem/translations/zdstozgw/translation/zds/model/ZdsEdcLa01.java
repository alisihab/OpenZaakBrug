package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.*;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.MIME;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@XmlRootElement(namespace = ZKN, name = "edcLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsEdcLa01 extends ZdsDocument {
    @XmlElement(namespace = ZKN)
    public ZdsParameters parameters;

    @XmlElement(namespace = ZKN, name = "antwoord")
    public Antwoord antwoord;

    @XmlElement(namespace = ZKN, name = "isRelevantVoor")
    public ZdsIsRelevantVoor isRelevantVoor;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Antwoord {

        @XmlElement(namespace = ZKN)
        public Object object;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Object  {

        @XmlElement(namespace = ZKN, name = "dct.omschrijving")
        public String omschrijving;

        @XmlElement(namespace = ZKN, name = "dct.categorie")
        public String categorie;

        @XmlElement(namespace = ZKN)
        public String inhoud;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Inhoud {

            @XmlAttribute(namespace = MIME)
            public String contentType;

            @XmlAttribute(namespace = ZKN)
            public String bestandsnaam;

            @XmlElement(namespace = ZKN)
            public String inhoud;
        }
    }

}
