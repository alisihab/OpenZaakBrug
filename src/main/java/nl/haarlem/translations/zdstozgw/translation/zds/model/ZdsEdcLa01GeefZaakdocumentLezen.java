package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.*;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.MIME;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@XmlRootElement(namespace = ZKN, name = "edcLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsEdcLa01GeefZaakdocumentLezen extends ZdsZknDocument {

/*	
	@XmlAccessorType(XmlAccessType.FIELD)	
    public class Antwoord {
    	@XmlAttribute(namespace = STUF)
        public String entiteittype = "EDC";
        
        @XmlElement(namespace = ZKN, name = "object")        
        public ZdsZaakDocument object;
	}
*/
	@XmlElement(namespace = ZKN)
    public ZdsParameters parameters;

    @XmlElement(namespace = ZKN, name = "antwoord")
    public ZdsAntwoord antwoord;

//    @XmlElement(namespace = ZKN, name = "isRelevantVoor")
//    public ZdsIsRelevantVoor isRelevantVoor;

    private ZdsEdcLa01GeefZaakdocumentLezen() {
    }
        
	public ZdsEdcLa01GeefZaakdocumentLezen(ZdsStuurgegevens fromRequest) {
		super(fromRequest);
	    this.stuurgegevens.crossRefnummer = fromRequest.referentienummer;
	    this.stuurgegevens.berichtcode = "La01";
	    this.stuurgegevens.entiteittype = "EDC";
	}       

/*
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public class Object  {

    	@XmlAttribute(namespace = STUF)
        public String entiteittype = "EDC";

		@XmlElement(namespace = ZKN, name = "dct.omschrijving")
        public String omschrijving;

        @XmlElement(namespace = ZKN, name = "dct.categorie")
        public String categorie;

        @XmlElement(namespace = ZKN)
        public Inhoud inhoud;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        public class Inhoud {

            @XmlAttribute(namespace = MIME)
            public String contentType;

            @XmlAttribute(namespace = ZKN)
            public String bestandsnaam;

            @XmlValue
            public String inhoud;
        }        
    }
*/
}
