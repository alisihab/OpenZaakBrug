package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsZaakDocumentInhoud extends ZdsZaakDocument {

	@XmlElement(namespace = ZKN)
	public ZdsInhoud inhoud;

	@XmlElement(namespace = ZKN, nillable = true)
	public ZdsIsRelevantVoor isRelevantVoor;

}
