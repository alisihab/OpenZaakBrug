package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(namespace = ZKN, name = "genereerDocumentIdentificatie_Di02")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsGenereerDocumentIdentificatieDi02 extends ZdsZknDocument {

	public ZdsGenereerDocumentIdentificatieDi02() {
	}
}
