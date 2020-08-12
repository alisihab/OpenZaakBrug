package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Stuurgegevens {

    @XmlElement(namespace = STUF)
    public String berichtcode;

    @XmlElement(namespace = STUF)
    public String referentienummer;

    @XmlElement(namespace = STUF)
    public String tijdstipBericht;

    @XmlElement(namespace = STUF)
    public String crossRefnummer;

    @XmlElement(namespace = STUF)
    public Zender zender;

    @XmlElement(namespace = STUF)
    public Ontvanger ontvanger;

    @XmlElement(namespace = STUF)
    public String entiteittype;

    public Stuurgegevens(Stuurgegevens stuurgegevens) {
        this.zender = new Zender()
                .setApplicatie(stuurgegevens.ontvanger.applicatie)
                .setOrganisatie(stuurgegevens.ontvanger.organisatie)
                .setGebruiker(stuurgegevens.ontvanger.gebruiker);
        this.ontvanger = new Ontvanger()
                .setApplicatie(stuurgegevens.zender.applicatie)
                .setOrganisatie(stuurgegevens.zender.organisatie)
                .setGebruiker(stuurgegevens.zender.gebruiker);

        this.entiteittype = stuurgegevens.entiteittype;
    }

    public Stuurgegevens() {
    }
}