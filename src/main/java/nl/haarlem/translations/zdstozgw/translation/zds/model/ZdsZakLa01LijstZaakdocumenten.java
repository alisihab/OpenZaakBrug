package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

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
