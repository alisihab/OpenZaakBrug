package nl.haarlem.translations.zdstozgw.translation.zds.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@XmlRootElement(namespace = ZKN, name = "edcLv01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsEdcLv01 {

    @XmlElement(namespace = ZKN)
    public ZdsStuurgegevens stuurgegevens;

    @XmlElement(namespace = ZKN)
    public ZdsParameters parameters;

    @XmlElement(namespace = ZKN)
    public ZdsScope zdsScope;

    @XmlElement(namespace = ZKN)
    public ZdsZaakDocument gelijk;
}
