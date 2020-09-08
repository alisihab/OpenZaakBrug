package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import java.util.List;

@Data
@XmlRootElement(namespace = ZKN, name = "zakLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsZakLa01GeefZaakDetails extends ZdsZknDocument {

	@XmlElement(namespace = ZKN)
	public ZdsParameters parameters;

	@XmlElement(namespace = ZKN)
	public Antwoord antwoord;

	@Data
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Antwoord {

		@XmlElement(namespace = ZKN, name = "object")
		public List<ZdsZaak> zaak;

//        @Data
//        @XmlAccessorType(XmlAccessType.FIELD)
//        public static class Object extends ZdsZaak {
//            @XmlElement(namespace = ZKN)
//            public List<Status> heeft;
//        }
	}
//
//    @Data
//    @XmlAccessorType(XmlAccessType.FIELD)
//    public static class Status {
//        @XmlAttribute(namespace = STUF)
//        public String entiteittype;
//
//        @XmlElement(namespace = ZKN)
//        public String toelichting;
//
//        @XmlElement(namespace = ZKN)
//        public String datumStatusGezet;
//
//        @XmlElement(namespace = ZKN)
//        public String indicatieLaatsteStatus;
//
//        @XmlElement(namespace = ZKN)
//        public String isGezetDoor;
//    }

}