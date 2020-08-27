package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

import javax.xml.bind.annotation.XmlElement;

abstract public class ZdsStufDocument extends ZdsZknDocument  {
	@XmlElement(namespace = STUF, name = "stuurgegevens")
    public ZdsStuurgegevens stuurgegevens;


}
