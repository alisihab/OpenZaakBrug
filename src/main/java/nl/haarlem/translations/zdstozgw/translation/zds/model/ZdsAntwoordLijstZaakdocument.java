package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = ZKN, name = "edcLa01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsAntwoordLijstZaakdocument extends ZdsObject {

	@XmlElement(namespace = ZKN)
	public String identificatie;

	@XmlElement(namespace = ZKN, name = "object")
	public ZdsObjectLijstZaakDocument object;
}