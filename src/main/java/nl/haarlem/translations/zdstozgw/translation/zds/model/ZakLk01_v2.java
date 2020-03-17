package nl.haarlem.translations.zdstozgw.translation.zds.model;
import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.*;

@Data
@XmlRootElement(namespace = ZKN, name="zakLk01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZakLk01_v2 {

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Object{
        @XmlAttribute(namespace = STUF)
        public String entiteittype;

        @XmlElement(namespace = ZKN)
        public String identificatie;

        @XmlElement(namespace = ZKN)
        public String omschrijving;

        @XmlElement(namespace = ZKN)
        public String toelichting;

        @XmlElement(namespace = ZKN)
        public String startdatum;

        @XmlElement(namespace = ZKN)
        public String einddatumGepland;

        @XmlElement(namespace = ZKN)
        public String archiefnominatie;

        @XmlElement(namespace = ZKN)
        public String registratiedatum;

        @XmlElement(namespace = ZKN, name="isVan")
        public GerelateerdeWrapper isVan;

        @XmlElement(namespace = ZKN, name="heeft")
        public GerelateerdeWrapper heeft;

        @XmlElement(namespace = ZKN, name="heeftAlsInitiator")
        public HeeftAlsInitiator heeftAlsInitiator;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Stuurgegevens{

        @XmlElement(namespace = STUF)
        public Zender zender;

        @XmlElement(namespace = STUF)
        public Ontvanger ontvanger;

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Zender{

        @XmlElement(namespace = STUF)
        public String organisatie;

        @XmlElement(namespace = STUF)
        public String applicatie;

        @XmlElement(namespace = STUF)
        public String gebruiker;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Ontvanger{

        @XmlElement(namespace = STUF)
        public String organisatie;

        @XmlElement(namespace = STUF)
        public String applicatie;

        @XmlElement(namespace = STUF)
        public String gebruiker;
    }

    @XmlElement(namespace = ZKN, name="object")
    public List<Object> objects;

    @XmlElement(namespace = ZKN, name="stuurgegevens")
    public Stuurgegevens stuurgegevens;

}
