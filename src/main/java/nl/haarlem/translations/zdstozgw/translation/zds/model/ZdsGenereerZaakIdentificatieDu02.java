package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlRootElement(namespace = ZKN, name = "genereerZaakIdentificatie_Du02")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsGenereerZaakIdentificatieDu02 extends ZdsZknDocument {
        
    @XmlElement(namespace = ZKN)
	public ZdsZaakFunctie zaak;
    
    private ZdsGenereerZaakIdentificatieDu02() {    	
    }
        
    public ZdsGenereerZaakIdentificatieDu02(ZdsStuurgegevens zdsStuurgegevens, String referentienummer) {
        this.stuurgegevens = new ZdsStuurgegevens(zdsStuurgegevens, referentienummer);
        this.stuurgegevens.berichtcode = "Du02";
        this.stuurgegevens.functie = "genereerZaakidentificatie";        
    }          
}










