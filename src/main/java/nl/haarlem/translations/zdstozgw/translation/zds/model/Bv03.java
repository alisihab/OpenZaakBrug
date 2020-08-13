package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.StufUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

@Data
@XmlRootElement(name = "Bv03Bericht", namespace = STUF)
@XmlAccessorType(XmlAccessType.FIELD)
public class Bv03 {

    @XmlElement(namespace = STUF)
    public Stuurgegevens stuurgegevens;

    public Bv03() {
    }

    public Bv03(Stuurgegevens stuurgegevens) {
        this.stuurgegevens = new Stuurgegevens(stuurgegevens);
        this.stuurgegevens.tijdstipBericht = StufUtils.getStufDateTime();
        this.stuurgegevens.berichtcode = "Bv03";
        this.stuurgegevens.referentienummer = stuurgegevens.referentienummer;
    }
}
