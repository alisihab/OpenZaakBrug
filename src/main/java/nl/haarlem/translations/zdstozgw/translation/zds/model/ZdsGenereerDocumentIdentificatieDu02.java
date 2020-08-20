package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.StufUtils;

import javax.xml.bind.annotation.*;
import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlRootElement(namespace = ZKN, name = "genereerDocumentIdentificatie_Du02")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsGenereerDocumentIdentificatieDu02 {

    @XmlElement(namespace = ZKN)
    public ZdsStuurgegevens stuurgegevens;   
        
    @XmlElement(namespace = ZKN)
	public ZdsZaakDocument document;
    
    private ZdsGenereerDocumentIdentificatieDu02() {    	
    }
        
    public ZdsGenereerDocumentIdentificatieDu02(ZdsStuurgegevens zdsStuurgegevens) {
        this.stuurgegevens = new ZdsStuurgegevens(zdsStuurgegevens);
        this.stuurgegevens.tijdstipBericht = StufUtils.getStufDateTime();
        this.stuurgegevens.referentienummer = zdsStuurgegevens.referentienummer;
        this.stuurgegevens.berichtcode = "Du02";   
        this.stuurgegevens.functie = "genereerDocumentidentificatie";
        this.stuurgegevens.entiteittype = "ZAK";
    }        
    
}










