package nl.haarlem.translations.zdstozgw.converter.impl.replicate.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLv01;


@XmlRootElement(namespace = ZKN, name = "edcLv01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsReplicateGeefZaakdocumentLezenLv01 extends ZdsEdcLv01 {

}
