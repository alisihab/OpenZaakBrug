package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(namespace = ZKN, name = "zakLk01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsZakLk01ActualiseerZaakstatus extends ZdsZknDocument {

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Object {
		@XmlAttribute(namespace = STUF)
		public String entiteittype;

		@XmlElement(namespace = ZKN)
		public String identificatie;

		@XmlElement(namespace = ZKN)
		public String omschrijving;

		@XmlElement(namespace = ZKN)
		public ZdsHeeft heeft;
	}

	@XmlElement(namespace = ZKN, name = "object")
	public List<ZdsZaak> objects;

}
