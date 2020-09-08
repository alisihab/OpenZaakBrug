package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlRootElement(namespace = ZKN, name = "genereerZaakIdentificatie_Di02")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsGenereerZaakIdentificatieDi02 extends ZdsZknDocument {

    public ZdsGenereerZaakIdentificatieDi02() {
    }
}