package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.http.annotation.Obsolete;

@Obsolete
@XmlAccessorType(XmlAccessType.FIELD)
public class HeeftAlsUitvoerende {
	@XmlElement(namespace = ZKN)
	public GerelateerdeRol gerelateerde;
}
