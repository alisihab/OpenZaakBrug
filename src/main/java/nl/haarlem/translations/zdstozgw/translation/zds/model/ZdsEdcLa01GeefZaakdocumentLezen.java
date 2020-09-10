package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.*;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.MIME;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@XmlRootElement(namespace = ZKN, name = "edcLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsEdcLa01GeefZaakdocumentLezen extends ZdsZknDocument {


	@XmlElement(namespace = ZKN)
    public ZdsParameters parameters;

    @XmlElement(namespace = ZKN, name = "antwoord")
    public ZdsAntwoord antwoord;

    private ZdsEdcLa01GeefZaakdocumentLezen() {
    }
        
	public ZdsEdcLa01GeefZaakdocumentLezen(ZdsStuurgegevens fromRequest, String referentienummer) {
		super(fromRequest, referentienummer);
	    this.stuurgegevens.crossRefnummer = fromRequest.referentienummer;
	    this.stuurgegevens.berichtcode = "La01";
	    this.stuurgegevens.entiteittype = "EDC";
	}       

}
