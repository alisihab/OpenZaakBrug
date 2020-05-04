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
public class ZakLk01_v2 {

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Object {
		@XmlAttribute(namespace = STUF)
		public String entiteittype;

		@XmlElement(namespace = ZKN)
		public String identificatie;

		@XmlElement(namespace = ZKN)
		public String omschrijving;

		@XmlElement(namespace = ZKN)
		public String toelichting;

		@XmlElement(namespace = ZKN)
		public String startdatum;

		@XmlElement(namespace = ZKN)
		public String einddatumGepland;

		@XmlElement(namespace = ZKN)
		public String archiefnominatie;

		@XmlElement(namespace = ZKN)
		public String registratiedatum;

		@XmlElement(namespace = ZKN)
		public GerelateerdeWrapper isVan;

		@XmlElement(namespace = ZKN)
		public Heeft heeft;

		//@XmlElement(namespace = ZKN, name = "heeftAlsBelanghebbende")
		//public HeeftAlsBelanghebbende heeftAlsBelanghebbende;
		
		@XmlElement(namespace = ZKN, name = "heeftAlsInitiator")
		public HeeftAlsInitiator heeftAlsInitiator;
		
		@XmlElement(namespace = ZKN, name = "heeftAlsUitvoerende")
		public HeeftAlsUitvoerende heeftAlsUitvoerende;

		//@XmlElement(namespace = ZKN, name = "heeftAlsAanspreekpunt")
		//public HeeftAlsAanspreekpunt heeftAlsAanspreekpunt;		
	}

	@XmlElement(namespace = ZKN, name = "object")
	public List<Object> objects;

	@XmlElement(namespace = ZKN, name = "stuurgegevens")
	public Stuurgegevens stuurgegevens;

}
