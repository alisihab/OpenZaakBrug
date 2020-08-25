package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.StufUtils;

import javax.xml.bind.annotation.*;
import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlRootElement(namespace = ZKN, name = "genereerZaakIdentificatie_Du02")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsGenereerZaakIdentificatieDu02 extends ZdsZknDocument {
        
    @XmlElement(namespace = ZKN)
	public ZdsZaakIdentificatie zaak;
    
    private ZdsGenereerZaakIdentificatieDu02() {    	
    }
        
    public ZdsGenereerZaakIdentificatieDu02(ZdsStuurgegevens zdsStuurgegevens) {
        this.stuurgegevens = new ZdsStuurgegevens(zdsStuurgegevens);
        this.stuurgegevens.berichtcode = "Du02";
        this.stuurgegevens.functie = "genereerZaakidentificatie";        
    }          
}










