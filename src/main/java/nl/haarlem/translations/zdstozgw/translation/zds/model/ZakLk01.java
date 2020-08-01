package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlRootElement(namespace = ZKN, name="zakLk01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZakLk01 {

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
        public String toelichting;

        @XmlElement(namespace = ZKN)
        public String startdatum;

        @XmlElement(namespace = ZKN)
        public String einddatumGepland;

        @XmlElement(namespace = ZKN)
        public String archiefnominatie;

        @XmlElement(namespace = ZKN)
        public String registratiedatum;

        @XmlElement(namespace = ZKN)
        public Rol isVan;

        @XmlElement(namespace = ZKN)
        public Heeft heeft;

        @XmlElement(namespace = ZKN, name="heeftAlsInitiator")
        public Rol heeftAlsInitiator;

        @XmlElement(namespace = ZKN, name="heeftAlsBelanghebbende")
        public Rol heeftAlsBelanghebbende;

        @XmlElement(namespace = ZKN, name="heeftAlsGemachtigde")
        public Rol heeftAlsGemachtigde;

        @XmlElement(namespace = ZKN, name="heeftAlsUitvoerende")
        public Rol heeftAlsUitvoerende;

        @XmlElement(namespace = ZKN, name="heeftAlsVerantwoordelijke")
        public Rol heeftAlsVerantwoordelijke;

        @XmlElement(namespace = ZKN, name="heeftAlsOverigBetrokkene")
        public Rol heeftAlsOverigBetrokkene;
    }


    @XmlElement(namespace = ZKN, name="object")
    public List<Object> objects;

    @XmlElement(namespace = ZKN, name="stuurgegevens")
    public Stuurgegevens stuurgegevens;

}
