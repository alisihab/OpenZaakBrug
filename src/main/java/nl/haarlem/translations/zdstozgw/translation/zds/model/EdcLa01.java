package nl.haarlem.translations.zdstozgw.translation.zds.model;

import javax.xml.bind.annotation.*;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@XmlRootElement(namespace = ZKN, name="edcLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class EdcLa01 {
    @XmlElement(namespace = ZKN, name="stuurgegevens")
    public Stuurgegevens stuurgegevens;

    @XmlElement(namespace = ZKN, name="antwoord")
    public Antwoord antwoord;

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Antwoord{
        @XmlElement(namespace = ZKN)
        public Object object;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Object{
        @XmlAttribute(namespace = STUF)
        public String entiteittype;

        @XmlElement(namespace = ZKN)
        public String identificatie;

        @XmlElement(namespace = ZKN,name="dct.omschrijving")
        public String dctOmschrijving;

        @XmlElement(namespace = ZKN,name="dct.categorie")
        public String dctCategorie;

        @XmlElement(namespace = ZKN)
        public String creatiedatum;

        @XmlElement(namespace = ZKN)
        public String ontvangstdatum;

        @XmlElement(namespace = ZKN)
        public String titel;

        @XmlElement(namespace = ZKN)
        public String taal;

        @XmlElement(namespace = ZKN)
        public String versie;

        @XmlElement(namespace = ZKN)
        public String status;

        @XmlElement(namespace = ZKN)
        public String vezenddatum;

        @XmlElement(namespace = ZKN)
        public String vertrouwelijkAanduiding;

        @XmlElement(namespace = ZKN)
        public String auteur;

        @XmlElement(namespace = ZKN)
        public String link;

        @XmlElement(namespace = ZKN)
        public String inhoud;

    }

}
