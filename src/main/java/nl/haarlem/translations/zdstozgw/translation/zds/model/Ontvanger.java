package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Ontvanger {

    @XmlElement(namespace = STUF)
    public String organisatie;

    @XmlElement(namespace = STUF)
    public String applicatie;

    @XmlElement(namespace = STUF)
    public String gebruiker;
}
