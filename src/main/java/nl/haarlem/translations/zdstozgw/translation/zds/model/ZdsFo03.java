package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.StufUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

@Data
@XmlRootElement(name = "Fo03Bericht", namespace = STUF)
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsFo03 extends ZdsDocument {

    @XmlElement(namespace = STUF)
    public Body body;

    public ZdsFo03(ZdsStuurgegevens stuurgegevens) {    	
    	this.stuurgegevens = new ZdsStuurgegevens(stuurgegevens);    	
    	this.stuurgegevens.berichtcode = "Fo03";
    }

    private ZdsFo03() {
	}

	@Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Body {
        @XmlElement(namespace = STUF)
        public String code;
        @XmlElement(namespace = STUF)
        public String plek;        
        @XmlElement(namespace = STUF)
        public String omschrijving;
        @XmlElement(namespace = STUF)
        public String details;
        @XmlElement(namespace = STUF)
		public String entiteittype;
        @XmlElement(namespace = STUF)        
		public String detailsXML;

        public Body() {
        }
    }
}
