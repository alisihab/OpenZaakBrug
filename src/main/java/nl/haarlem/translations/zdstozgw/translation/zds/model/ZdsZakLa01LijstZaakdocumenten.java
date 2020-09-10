package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlRootElement(namespace = ZKN, name = "zakLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsZakLa01LijstZaakdocumenten extends ZdsZknDocument {

	@XmlElement(namespace = ZKN)
    public ZdsParameters parameters;

    @XmlElement(namespace = ZKN)
    public ZdsAntwoordLijstZaakdocument antwoord;
    
    private ZdsZakLa01LijstZaakdocumenten() {
    }

    public ZdsZakLa01LijstZaakdocumenten(ZdsStuurgegevens fromRequest, String referentienummer) {
    	super(fromRequest, referentienummer);
        this.stuurgegevens.entiteittype = "ZAK";    	
        this.stuurgegevens.berichtcode = "Bv03";
        this.stuurgegevens.crossRefnummer = fromRequest.referentienummer;
    }  
}
