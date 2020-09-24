package nl.haarlem.translations.zdstozgw.converter.impl.replicate.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLv01;

@Data
@XmlRootElement(namespace = ZKN, name = "zakLv01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsReplicateGeefZaakdetailsLv01 extends ZdsZakLv01 {

}
