package nl.haarlem.translations.zdstozgw.translation.zds.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@XmlRootElement(namespace = ZKN, name="edcLv01")
@XmlAccessorType(XmlAccessType.FIELD)
public class EdcLv01 {

    @XmlElement(namespace = ZKN, name="stuurgegevens")
    public Stuurgegevens stuurgegevens;

    @XmlElement(namespace = ZKN, name="scope")
    public Scope scope;

    @XmlElement(namespace = ZKN, name="gelijk")
    public Gelijk gelijk;
}
