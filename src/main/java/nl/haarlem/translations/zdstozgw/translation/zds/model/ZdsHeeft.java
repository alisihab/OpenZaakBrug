package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsHeeft  extends ZdsObject {
    @XmlAttribute(namespace = STUF)
    public String entiteittype = "ZAKSTT";	
	
    @XmlElement(namespace = ZKN)
    public ZdsGerelateerde gerelateerde;

    @XmlElement(namespace = ZKN)
    public String toelichting;    
    
    @XmlElement(namespace = ZKN)
    public String datumStatusGezet;

    @XmlElement(namespace = ZKN)
    public String statustoelichting;

    @XmlElement(namespace = ZKN)
    public ZdsRol isGezetDoor;
    
    @XmlElement(namespace = ZKN)
    public String indicatieLaatsteStatus;    
}
