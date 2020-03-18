package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;
import org.w3c.dom.Document;

import javax.xml.bind.annotation.*;

import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.*;

@XmlRootElement(namespace = ZKN, name="edcLk01")
@XmlAccessorType(XmlAccessType.FIELD)
public class EdcLk01 {

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Object{
        @XmlAttribute(namespace = STUF)
        public String entiteittype;

        @XmlElement(namespace = ZKN)
        public String identificatie;

        @XmlElement(namespace = ZKN, name="dct.omschrijving")
        public String omschrijving;

        @XmlElement(namespace = ZKN)
        public String formaat;

        @XmlElement(namespace = ZKN)
        public String taal;

        @XmlElement(namespace = ZKN)
        public Inhoud inhoud;

        @XmlElement(namespace = ZKN)
        public String auteur;

        @XmlElement(namespace = ZKN)
        public String creatiedatum;

        @XmlElement(namespace = ZKN)
        public String titel;

        @XmlElement(namespace = ZKN)
        public String vertrouwelijkAanduiding;

        @XmlElement(namespace = ZKN, name="isRelevantVoor")
        public GerelateerdeWrapper isRelevantVoor;

        @XmlElement(namespace = ZKN, name="isVan")
        public GerelateerdeWrapper isVan;

        @XmlElement(namespace = ZKN, name="heeft")
        public GerelateerdeWrapper heeft;

        @XmlElement(namespace = ZKN, name="heeftAlsInitiator")
        public HeeftAlsInitiator heeftAlsInitiator;
    }



    @XmlElement(namespace = ZKN, name="object")
    public List<EdcLk01.Object> objects;

    @XmlElement(namespace = ZKN, name="stuurgegevens")
    public Stuurgegevens stuurgegevens;

}
