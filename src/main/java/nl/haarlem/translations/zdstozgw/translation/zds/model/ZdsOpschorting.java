package nl.haarlem.translations.zdstozgw.translation.zds.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsOpschorting {
    @XmlElement(namespace = ZKN)
    public String indicatie;

    @XmlElement(namespace = ZKN)
    public String reden;
}
