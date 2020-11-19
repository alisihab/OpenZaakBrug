package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = ZKN, name = "edcLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsEdcLa01GeefZaakdocumentLezen extends ZdsZknDocument {

	@XmlElement(namespace = ZKN)
	public ZdsParameters parameters;

	@XmlElement(namespace = ZKN, name = "antwoord")
	public ZdsZaakDocumentAntwoord antwoord;

	private ZdsEdcLa01GeefZaakdocumentLezen() {
	}

	public ZdsEdcLa01GeefZaakdocumentLezen(ZdsStuurgegevens fromRequest, String referentienummer) {
		super(fromRequest, referentienummer);
		this.stuurgegevens.crossRefnummer = fromRequest.referentienummer;
		this.stuurgegevens.berichtcode = "La01";
		this.stuurgegevens.entiteittype = "EDC";
	}

}
