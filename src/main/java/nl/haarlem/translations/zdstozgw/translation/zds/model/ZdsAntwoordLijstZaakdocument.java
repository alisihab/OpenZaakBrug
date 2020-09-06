package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.*;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.MIME;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import java.util.List;

@XmlRootElement(namespace = ZKN, name = "edcLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsAntwoordLijstZaakdocument extends ZdsAntwoord {    
    //@XmlAttribute(namespace = STUF)
    //public String entiteittype = "ZAK";     	
	
    @XmlElement(namespace = ZKN)
    public String identificatie;

    @XmlElement(namespace = ZKN)
    public List<ZdsHeeftRelevant> heeftRelevant;
 
	@XmlElement(namespace = ZKN, name = "object")
    public List<ZdsHeeftRelevant> object;	
}