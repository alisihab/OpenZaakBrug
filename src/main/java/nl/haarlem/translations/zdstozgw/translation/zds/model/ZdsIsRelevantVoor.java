package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsIsRelevantVoor extends ZdsObject {

	@XmlAttribute(namespace = STUF)
	public String entiteittype = "EDCZAK";

	@XmlElement(namespace = ZKN)
	public ZdsGerelateerde gerelateerde;

	@XmlElement(namespace = ZKN, name = "dct.omschrijving")
	public String volgnummer;

	@XmlElement(namespace = ZKN, name = "stt.omschrijving")
	public String omschrijving;

	@XmlElement(namespace = ZKN, name = "sta.datumStatusGezet")
	public String datumStatusGezet;

}
