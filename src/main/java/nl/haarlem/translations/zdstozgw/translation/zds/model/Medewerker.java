package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlElement;

public class Medewerker {
	@XmlElement(namespace = ZKN)
	public String identificatie;

	@XmlElement(namespace = ZKN)
	public String achternaam;

	@XmlElement(namespace = ZKN)
	public String voorletters;
}
