package nl.haarlem.translations.zdstozgw.translation.zds.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsIsRelevantVoor  extends ZdsObject {
    @XmlAttribute(namespace = STUF)
    public String entiteittype;

    @XmlElement(namespace = ZKN)
    public ZdsGerelateerde gerelateerde;

}
