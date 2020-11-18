package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(namespace = ZKN, name = "edcLk01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsEdcLk01 extends ZdsZknDocument {

	@XmlElement(namespace = ZKN, name = "object")
	public List<ZdsZaakDocumentInhoud> objects;
}
