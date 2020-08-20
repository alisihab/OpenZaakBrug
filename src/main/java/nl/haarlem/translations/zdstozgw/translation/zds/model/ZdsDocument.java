package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlElement;

abstract public class ZdsDocument extends ZdsObject {

    @XmlElement(namespace = ZKN, name = "stuurgegevens")
    public ZdsStuurgegevens stuurgegevens;
}
