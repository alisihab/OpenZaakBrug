package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsStuurgegevens {

    @XmlElement(namespace = STUF)
    public String berichtcode;

    @XmlElement(namespace = STUF)
    public String referentienummer;

    @XmlElement(namespace = STUF)
    public String tijdstipBericht;

    @XmlElement(namespace = STUF)
    public String crossRefnummer;

    @XmlElement(namespace = STUF)
    public ZdsZender zdsZender;

    @XmlElement(namespace = STUF)
    public ZdsOntvanger zdsOntvanger;

    @XmlElement(namespace = STUF)
    public String entiteittype;

    public ZdsStuurgegevens(ZdsStuurgegevens zdsStuurgegevens) {
        this.zdsZender = new ZdsZender()
                .setApplicatie(zdsStuurgegevens.zdsOntvanger.applicatie)
                .setOrganisatie(zdsStuurgegevens.zdsOntvanger.organisatie)
                .setGebruiker(zdsStuurgegevens.zdsOntvanger.gebruiker);
        this.zdsOntvanger = new ZdsOntvanger()
                .setApplicatie(zdsStuurgegevens.zdsZender.applicatie)
                .setOrganisatie(zdsStuurgegevens.zdsZender.organisatie)
                .setGebruiker(zdsStuurgegevens.zdsZender.gebruiker);

        this.entiteittype = zdsStuurgegevens.entiteittype;
    }

    public ZdsStuurgegevens() {
    }
}