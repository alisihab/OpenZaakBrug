package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Parameters {
	//TODO: for now we ignore this!
	@XmlElement(namespace = STUF)
	public String sortering;
	@XmlElement(namespace = STUF)
	public String indicatorVervolgvraag;
	@XmlElement(namespace = STUF)
	public String indicatorAfnemerIndicatie;
	@XmlElement(namespace = STUF)
	public String indicatorAantal;	
	
}