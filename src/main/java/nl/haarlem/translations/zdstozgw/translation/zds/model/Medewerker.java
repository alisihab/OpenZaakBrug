package nl.haarlem.translations.zdstozgw.translation.zds.model;

import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

public class Medewerker {
    @XmlElement(namespace = ZKN)
    public String identificatie;

    @XmlElement(namespace = ZKN)
    public String achternaam;

    @XmlElement(namespace = ZKN)
    public String voorletters;
}
