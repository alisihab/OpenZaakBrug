package nl.haarlem.translations.zdstozgw.translation.zds.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;


@XmlRootElement(namespace = ZKN, name = "scope")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsScope  extends ZdsObject {
	
    @XmlElement(namespace = ZKN, name = "object")
    public ZdsZaakDocumentInhoud object;

    @XmlElement(namespace = STUF, name = "object")    
    public String scope;
}
