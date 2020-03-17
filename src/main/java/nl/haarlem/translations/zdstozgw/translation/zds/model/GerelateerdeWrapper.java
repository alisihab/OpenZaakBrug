package nl.haarlem.translations.zdstozgw.translation.zds.model;

import javax.xml.bind.annotation.*;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class GerelateerdeWrapper {

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Gerelateerde{
        @XmlAttribute(namespace = STUF)
        public String verwerkingssoort;

        @XmlElement(namespace = ZKN)
        public String omschrijving;

        @XmlElement(namespace = ZKN)
        public String code;

        @XmlElement(namespace = ZKN)
        public String ingangsdatumObject;

        @XmlElement(namespace =  ZKN, name="zkt.code")
        public String zktCode;

        @XmlElement(namespace =  ZKN, name="zkt.omschrijving")
        public String zktOmschrijving;

        @XmlElement(namespace =  ZKN)
        public String volgnummer;
    }

    @XmlAttribute(namespace = STUF)
    public String verwerkingssoort;

    @XmlElement(namespace = ZKN)
    public Gerelateerde gerelateerde;


}
