package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlRootElement(namespace = ZKN, name = "genereerDocumentIdentificatie_Du02")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsGenereerDocumentIdentificatieDu02 extends ZdsZknDocument{

    @XmlElement(namespace = ZKN)
	public ZdsZaakDocumentIdentificatie document;
    
    private ZdsGenereerDocumentIdentificatieDu02() {
    }
        
    public ZdsGenereerDocumentIdentificatieDu02(ZdsStuurgegevens zdsStuurgegevens) {
        this.stuurgegevens = new ZdsStuurgegevens(zdsStuurgegevens);
        this.stuurgegevens.berichtcode = "Du02";   
        this.stuurgegevens.functie = "genereerDocumentidentificatie";
    }        
    
}










