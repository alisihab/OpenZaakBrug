package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsZaakDocument extends ZdsZaakDocumentIdentificatie {
    @XmlElement(namespace = ZKN, name = "dct.omschrijving")
    public String omschrijving;    
    
    @XmlElement(namespace = ZKN, nillable = true)
    public String creatiedatum;

    @XmlElement(namespace = ZKN, nillable = true)
    public String ontvangstdatum;

    @XmlElement(namespace = ZKN, nillable = true)
    public String titel;

    @XmlElement(namespace = ZKN, nillable = true)
    public String taal;

    @XmlElement(namespace = ZKN)
  	public ZdsInhoud inhoud;    
    
    @XmlElement(namespace = ZKN, nillable = true)
    public String versie;

    @XmlElement(namespace = ZKN, nillable = true)
    public String status;

    @XmlElement(namespace = ZKN, nillable = true)
    public String vezenddatum;

    @XmlElement(namespace = ZKN, nillable = true)
    public String vertrouwelijkheidAanduiding;

    @XmlElement(namespace = ZKN, nillable = true)
    public String auteur;

    @XmlElement(namespace = ZKN, nillable = true)
    public String link;

    @XmlElement(namespace = ZKN, nillable = true)    
    public ZdsIsRelevantVoor isRelevantVoor;
}
