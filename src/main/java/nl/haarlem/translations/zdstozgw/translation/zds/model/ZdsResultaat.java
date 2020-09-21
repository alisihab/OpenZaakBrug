package nl.haarlem.translations.zdstozgw.translation.zds.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;


@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsResultaat {
    @XmlElement(namespace = ZKN)
    private String omschrijving;

    @XmlElement(namespace = ZKN)
    private String toelichting;

}
