package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.StufUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsStuurgegevens {

    @XmlElement(namespace = STUF)
    public String berichtcode;

    @XmlElement(namespace = STUF)
    public Endpoint zender;

    @XmlElement(namespace = STUF)
    public Endpoint ontvanger;
    
    @XmlElement(namespace = STUF)
    public String referentienummer;

    @XmlElement(namespace = STUF)
    public String tijdstipBericht;

    @XmlElement(namespace = STUF)
    public String crossRefnummer;

    @XmlElement(namespace = STUF)
    public String entiteittype;

    @XmlElement(namespace = STUF)
    public String functie;   
    
    private ZdsStuurgegevens() {    	
    }
    
    public ZdsStuurgegevens(ZdsStuurgegevens stuurgegevens) {
    	this.zender = new Endpoint(stuurgegevens.ontvanger);        
    	this.ontvanger = new Endpoint(stuurgegevens.zender);
    	this.referentienummer = stuurgegevens.referentienummer;
    	this.tijdstipBericht = StufUtils.getStufDateTime();
        this.entiteittype = stuurgegevens.entiteittype;
    }

	public ZdsStuurgegevens(String berichtcode) {
        // WORKAROUND ongewenst om aan te roepen: resulteerd in een ongeldige xml
		this.berichtcode = berichtcode;
	}
}