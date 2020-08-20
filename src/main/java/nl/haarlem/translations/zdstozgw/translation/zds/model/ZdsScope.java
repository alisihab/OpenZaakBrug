package nl.haarlem.translations.zdstozgw.translation.zds.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@XmlRootElement(namespace = ZKN, name = "scope")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsScope  extends ZdsObject {
    @XmlElement(namespace = ZKN, name = "object")
    //public ZdsEdcLk01.Object object;
    public ZdsZaakDocument object;
}
