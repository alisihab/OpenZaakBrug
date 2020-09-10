package nl.haarlem.translations.zdstozgw.translation.zds.model;

import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

abstract public class ZdsStufDocument extends ZdsZknDocument  {
	
	@XmlElement(namespace = STUF, name = "stuurgegevens")
    public ZdsStuurgegevens stuurgegevens;
}
