package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = ZKN, name = "edcLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class EdcLa01 {
	@XmlElement(namespace = ZKN, name = "stuurgegevens")
	public Stuurgegevens stuurgegevens;

	@XmlElement(namespace = ZKN, name = "antwoord")
	public Antwoord antwoord;

    @XmlElement(namespace = ZKN, name="isRelevantVoor")
    public IsRelevantVoor isRelevantVoor;

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Antwoord {

		@XmlElement(namespace = ZKN)
		public Object object;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Object {

		@XmlAttribute(namespace = STUF)
		public String entiteittype;

        @XmlElement(namespace = ZKN, nillable =  true)
		public String identificatie;

        @XmlElement(namespace = ZKN,name="dct.omschrijving", nillable = true)
		public String dctOmschrijving;

        @XmlElement(namespace = ZKN,name="dct.categorie", nillable =  true)
		public String dctCategorie;

        @XmlElement(namespace = ZKN, nillable =  true)
		public String creatiedatum;

        @XmlElement(namespace = ZKN, nillable =  true)
		public String ontvangstdatum;

        @XmlElement(namespace = ZKN, nillable =  true)
		public String titel;

        @XmlElement(namespace = ZKN, nillable =  true)
		public String taal;

        @XmlElement(namespace = ZKN, nillable =  true)
		public String versie;

        @XmlElement(namespace = ZKN, nillable =  true)
		public String status;

        @XmlElement(namespace = ZKN, nillable =  true)
		public String vezenddatum;

        @XmlElement(namespace = ZKN, nillable =  true)
		public String vertrouwelijkAanduiding;

        @XmlElement(namespace = ZKN,nillable = true)
		public String auteur;

        @XmlElement(namespace = ZKN, nillable =  true)
		public String link;

        @XmlElement(namespace = ZKN, nillable =  true)
		public String inhoud;

	}

}
