package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(namespace = ZKN, name = "zakLv01")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZakLv01 {
	@XmlElement(namespace = ZKN, name = "stuurgegevens")
	public Stuurgegevens stuurgegevens;
		
	@XmlElement(namespace = ZKN, name = "gelijk")
	public Gelijk gelijk;		
}