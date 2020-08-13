package nl.haarlem.translations.zdstozgw.translation.zds.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@XmlRootElement(namespace = ZKN, name = "edcLv01")
@XmlAccessorType(XmlAccessType.FIELD)
public class EdcLv01 {

    @XmlElement(namespace = ZKN)
    public Stuurgegevens stuurgegevens;

    @XmlElement(namespace = ZKN)
    public Parameters parameters;

    @XmlElement(namespace = ZKN)
    public Scope scope;

    @XmlElement(namespace = ZKN)
    public Gelijk gelijk;
}
