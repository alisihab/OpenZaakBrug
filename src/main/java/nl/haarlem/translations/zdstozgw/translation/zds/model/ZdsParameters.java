package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsParameters extends ZdsObject {
	@XmlElement(namespace = STUF, nillable = true)
	public String sortering;

	@XmlElement(namespace = STUF, nillable = true)
	public String indicatorVervolgvraag;

	public ZdsParameters() {
	}

	public ZdsParameters(ZdsParameters zdsParameters) {
		this.sortering = zdsParameters.sortering;
		this.indicatorVervolgvraag = zdsParameters.indicatorVervolgvraag;
	}
}
