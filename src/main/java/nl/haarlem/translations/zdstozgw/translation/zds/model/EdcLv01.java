package nl.haarlem.translations.zdstozgw.translation.zds.model;

import javax.xml.bind.annotation.*;

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
