package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsParametersMetSortering  extends ZdsParameters {
	@XmlElement(namespace = STUF, nillable = true)
    public String sortering;

    public ZdsParametersMetSortering(ZdsParametersMetSortering zdsParameters) {
    	super(zdsParameters);
    	this.sortering = zdsParameters.sortering;
    }
}
