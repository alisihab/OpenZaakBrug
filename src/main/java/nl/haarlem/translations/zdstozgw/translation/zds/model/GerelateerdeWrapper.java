package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakInformatieObject;

@XmlAccessorType(XmlAccessType.FIELD)
public class GerelateerdeWrapper {

	@XmlElement(namespace = ZKN)
	public GerelateerdeRol gerelateerde;
	
    @XmlElement(namespace = ZKN,name="stt.volgnummer", nillable = true)
	public String sttVolgnummer;
	
    @XmlElement(namespace = ZKN,name="stt.omschrijving", nillable = true)
	public String sttOmschrijving;
	
    @XmlElement(namespace = ZKN,name="sta.datumStatusGezet", nillable = true)
	public String staDatumStatusGezet;
}
