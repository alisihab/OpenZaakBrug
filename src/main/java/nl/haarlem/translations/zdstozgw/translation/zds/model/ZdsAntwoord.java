package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.*;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.MIME;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@XmlRootElement(namespace = ZKN, name = "edcLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsAntwoord extends ZdsObject {
//	@XmlAttribute(namespace = STUF)
//    public String entiteittype = "EDC";
    
    @XmlElement(namespace = ZKN, name = "object")        
    public ZdsZaakDocument object;	
}