package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Endpoint extends ZdsObject {

	@XmlElement(namespace = STUF)
	public String organisatie;

	@XmlElement(namespace = STUF)
	public String applicatie;

	@XmlElement(namespace = STUF)
	public String gebruiker;

	private Endpoint() {
	}

	public Endpoint(Endpoint zender) {
		if(zender != null) {
			this.applicatie = zender.applicatie;
			this.organisatie = zender.organisatie;
			this.gebruiker = zender.gebruiker;
		}
	}
}
