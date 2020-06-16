package nl.haarlem.translations.zdstozgw.translation.zds.model;


import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsZaakDocumentInhoud extends ZdsZaakDocument {
    @XmlElement(namespace = ZKN, nillable =  true)
	public String inhoud;
}
