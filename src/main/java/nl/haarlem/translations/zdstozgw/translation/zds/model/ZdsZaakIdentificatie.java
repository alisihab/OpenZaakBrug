package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsZaakIdentificatie extends ZdsObject {
    @XmlAttribute(namespace = STUF)
    public String entiteittype = "ZAK";

    @XmlAttribute(namespace = STUF)
    public String functie = "entiteit";    
    
    @XmlElement(namespace = ZKN)
    public String identificatie;
}

