package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaak;

@XmlRootElement(namespace = ZKN, name = "edcLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZakLa01 {
	@XmlElement(namespace = ZKN, name = "stuurgegevens")
	public Stuurgegevens stuurgegevens;

	@XmlElement(namespace = ZKN, name = "antwoord")
	public Antwoord antwoord;

    @XmlElement(namespace = ZKN, name="isRelevantVoor")
    public IsRelevantVoor isRelevantVoor;

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Antwoord {

		@XmlElement(namespace = ZKN)
		public ZdsZaak object;
	}
}
