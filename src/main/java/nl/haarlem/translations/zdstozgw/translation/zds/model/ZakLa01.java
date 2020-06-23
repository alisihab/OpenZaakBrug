package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.Opschorting;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(namespace = ZKN, name = "edcLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZakLa01 {

    @XmlElement(namespace = ZKN)
    public Antwoord antwoord;

    @XmlElement(namespace = ZKN)
    public Object object;

    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Antwoord {
        @XmlElement(namespace = ZKN)
        public Object object;

        @XmlAccessorType(XmlAccessType.FIELD)
        private static class Object {
            @XmlElement(namespace = ZKN)
            public String identificatie;

            @XmlElement(namespace = ZKN)
            public String einddatum;

            @XmlElement(namespace = ZKN)
            public String einddatumGepland;

            @XmlElement(namespace = ZKN)
            public String omschrijving;

            @XmlElement(namespace = ZKN)
            public Kenmerk kenmerk;

            @XmlElement(namespace = ZKN)
            public Resultaat resultaat;

            @XmlElement(namespace = ZKN)
            public String startdatum;

            @XmlElement(namespace = ZKN)
            public String toelichting;

            @XmlElement(namespace = ZKN)
            public String uiterlijkeEinddatum;

            @XmlElement(namespace = ZKN)
            public String zaakniveau;

            @XmlElement(namespace = ZKN)
            public String deelzakenIdicatie;

            @XmlElement(namespace = ZKN)
            public String registratiedatum;

            @XmlElement(namespace = ZKN)
            public String publicatiedatum;

            @XmlElement(namespace = ZKN)
            public String archiefnominatie;

            @XmlElement(namespace = ZKN)
            public String datumVernietigingDossier;

            @XmlElement(namespace = ZKN)
            public String betalingsIndicatie;

            @XmlElement(namespace = ZKN)
            public String laatsteBetaaldatum;

            @XmlElement(namespace = ZKN)
            public Opschorting opschorting;

            @XmlElement(namespace = ZKN)
            public Verlenging verlening;

            @XmlElement(namespace = ZKN)
            public AnderZaakObject anderZaakObject;

            @XmlElement(namespace = ZKN)
            public Heeft heeftBetrekkingOp;

            @XmlElement(namespace = ZKN)
            public Rol heeftAlsBelanghebbende;

            @XmlElement(namespace = ZKN)
            public Rol heeftAlsGemachtigde;

            @XmlElement(namespace = ZKN)
            public Rol heeftAlsInitiator;

            @XmlElement(namespace = ZKN)
            public Rol heeftAlsUitvoerende;

            @XmlElement(namespace = ZKN)
            public Rol heeftAlheeftAlsVerantwoordelijke;

            @XmlElement(namespace = ZKN)
            public Rol heeftAlsOverigBetrokkene;

            @XmlElement(namespace = ZKN)
            public Status heeft;

            @XmlAccessorType(XmlAccessType.FIELD)
            private static class Resultaat{
                @XmlElement(namespace = ZKN)
                private String omschrijving;

                @XmlElement(namespace = ZKN)
                private String toelichting;
            }

            @XmlAccessorType(XmlAccessType.FIELD)
            private static class AnderZaakObject {
                @XmlElement(namespace = ZKN)
                public String omschrijving;

                @XmlElement(namespace = ZKN)
                public String aanduiding;

                @XmlElement(namespace = ZKN)
                public String lokatie;

                @XmlElement(namespace = ZKN)
                public String registratie;
            }

            @XmlAccessorType(XmlAccessType.FIELD)
            private static class Verlenging {
                @XmlElement(namespace = ZKN)
                public String duur;

                @XmlElement(namespace = ZKN)
                public String reden;
            }

            @XmlAccessorType(XmlAccessType.FIELD)
            private static class Opschorting {
                @XmlElement(namespace = ZKN)
                public String indicatie;

                @XmlElement(namespace = ZKN)
                public String reden;
            }

            @XmlAccessorType(XmlAccessType.FIELD)
            private static class Kenmerk {
                @XmlElement(namespace = ZKN)
                public String kenmerk;

                @XmlElement(namespace = ZKN)
                public String bron;
            }

            @XmlAccessorType(XmlAccessType.FIELD)
            private static class Status {
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
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Object{
        @XmlElement(namespace = ZKN)
        public Rol isVan;
    }

}