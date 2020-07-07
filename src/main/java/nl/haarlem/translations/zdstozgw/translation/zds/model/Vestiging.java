package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.BG;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Vestiging {
    @XmlElement(namespace = BG)
    public String vestigingsNummer;

    @XmlElement(namespace = BG)
    public String handelsnaam;

    @XmlElement(namespace = BG)
    public VerblijfsAdres verblijfsAdres;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class VerblijfsAdres{
        @XmlElement(namespace = BG, name="wpl.woonplaatsNaam")
        public String woonplaatsNaam;

        @XmlElement(namespace = BG, name="gor.openbareRuimteNaam")
        public String openbareRuimteNaam;

        @XmlElement(namespace = BG, name="gor.straatnaam")
        public String straatNaam;

        @XmlElement(namespace = BG, name="aoa.postcode")
        public String postcode;

        @XmlElement(namespace = BG, name="aoa.huisnummer")
        public String huisnummer;

        @XmlElement(namespace = BG, name="aoa.huisletter")
        public String huisletter;

        @XmlElement(namespace = BG, name="aoa.huisnummertoevoeging")
        public String huisnummertoevoeging;
    }
}
