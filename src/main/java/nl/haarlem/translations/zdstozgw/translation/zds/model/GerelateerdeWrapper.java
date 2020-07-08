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
	
	///////////////////////////////////////////////////////////////////

    @XmlElement(namespace = ZKN,name="zkt.code", nillable = true)
	public String zktCode;
	
    @XmlElement(namespace = ZKN,name="zkt.omschrijving", nillable = true)
	public String zktOmschrijving;
	
    @XmlElement(namespace = ZKN,name="volgnummer", nillable = true)
	public String volgnummer;

    @XmlElement(namespace = ZKN,name="code", nillable = true)
	public String code;

    @XmlElement(namespace = ZKN,name="omschrijving", nillable = true)
	public String omschrijving;
    
    @XmlElement(namespace = ZKN,name="ingangsdatumObject", nillable = true)
	public String ingangsdatumObject;

    //////////////////////////////////////////////////////////////////
    
    @XmlElement(namespace = ZKN,name="stt.volgnummer", nillable = true)
	public String sttVolgnummer;
	
    @XmlElement(namespace = ZKN,name="stt.omschrijving", nillable = true)
	public String sttOmschrijving;
	
    @XmlElement(namespace = ZKN,name="sta.datumStatusGezet", nillable = true)
	public String staDatumStatusGezet;    
}
