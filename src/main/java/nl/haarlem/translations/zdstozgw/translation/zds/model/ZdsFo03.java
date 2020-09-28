package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "Fo03Bericht", namespace = STUF)
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsFo03 extends ZdsStufDocument {

	@XmlElement(namespace = STUF)
	public Body body;

	public ZdsFo03() {
	}

	public ZdsFo03(ZdsStuurgegevens stuurgegevens, String referentienummer) {
		this.stuurgegevens = new ZdsStuurgegevens(stuurgegevens, referentienummer);
		this.stuurgegevens.crossRefnummer = stuurgegevens.referentienummer;
		this.stuurgegevens.berichtcode = "Fo03";
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
		public ZdsDetailsXML detailsXML;

		public Body() {
		}
	}
}
