package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.StufUtils;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsStuurgegevens extends ZdsObject {

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
	public String functie;

	@XmlElement(namespace = STUF)
	public String entiteittype;

	public ZdsStuurgegevens() {
	}

	public ZdsStuurgegevens(ZdsStuurgegevens stuurgegevens, String referentienummer) {
		this.zender = new Endpoint(stuurgegevens.ontvanger);
		this.ontvanger = new Endpoint(stuurgegevens.zender);
		this.referentienummer = referentienummer;
		this.crossRefnummer = stuurgegevens.referentienummer;
		this.tijdstipBericht = StufUtils.getStufDateTime();
	}
}