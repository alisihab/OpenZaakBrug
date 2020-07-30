package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.StufUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

@Data
@XmlRootElement(name="Fo03Bericht", namespace = STUF)
@XmlAccessorType(XmlAccessType.FIELD)
public class Fo03 {

    @XmlElement(namespace = STUF)
    public Stuurgegevens stuurgegevens;

    @XmlElement(namespace = STUF)
    public Body body;

    public Fo03(){}

    public Fo03(Stuurgegevens stuurgegevens){
        this.stuurgegevens = stuurgegevens;
        this.stuurgegevens.tijdstipBericht = StufUtils.getStufDateTime();
        this.stuurgegevens.berichtcode = "Fo03";
        this.stuurgegevens.referentienummer = stuurgegevens.referentienummer;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Body{

        @XmlElement(namespace = STUF)
        public String plek;
        @XmlElement(namespace = STUF)
        public String code;
        @XmlElement(namespace = STUF)
        public String omschrijving;
        @XmlElement(namespace = STUF)
        public String details;

        public Body(){}

        public Body(Exception ex){
            this.code = "StUF046";
            this.details = "Object was not saved";
            this.omschrijving = "Object niet opgeslagen";
            this.details = ex.getMessage();
        }

    }
}
