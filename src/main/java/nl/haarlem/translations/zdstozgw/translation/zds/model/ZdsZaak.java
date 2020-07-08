package nl.haarlem.translations.zdstozgw.translation.zds.model;


import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsZaak {
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
		public String einddatum;
		
		@XmlElement(namespace = ZKN)		
		public ZdsOpschorting opschorting;
		
		@XmlElement(namespace = ZKN)
		public GerelateerdeWrapper isVan;

		@XmlElement(namespace = ZKN)
		public Heeft heeft;

		@XmlElement(namespace = ZKN, name = "heeftAlsBelanghebbende")
		public ZdsRol heeftAlsBelanghebbende;
		
		@XmlElement(namespace = ZKN, name = "heeftAlsInitiator")
		public ZdsRol heeftAlsInitiator;
		
		@XmlElement(namespace = ZKN, name = "heeftAlsUitvoerende")
		public ZdsRol heeftAlsUitvoerende;

		@XmlElement(namespace = ZKN, name = "heeftAlsAanspreekpunt")
		public ZdsRol heeftAlsAanspreekpunt;

		@XmlElement(namespace = ZKN, name = "heeftBetrekkingOp")
		public ZdsRol heeftBetrekkingOp;

		@XmlElement(namespace = ZKN, name = "heeftAlsVerantwoordelijke")
		public ZdsRol heeftAlsVerantwoordelijke;

		@XmlElement(namespace = ZKN, name = "heeftRelevant")
		//public ZdsRelatieZaakDocument heeftRelevant;				
		public List<ZdsRelatieZaakDocument> heeftRelevant;

	}