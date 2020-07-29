package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Object{
    //not in the standard?
    @XmlAttribute(namespace = STUF)
    public String entiteittype;

    @XmlElement(namespace = ZKN)
    public String identificatie;

    @XmlElement(namespace = ZKN)
    public String omschrijving;

    @XmlElement(namespace = ZKN)
    public String toelichting;

    @XmlElement(namespace = ZKN)
    public String startdatum;

    @XmlElement(namespace = ZKN)
    public String einddatumGepland;

    @XmlElement(namespace = ZKN)
    public String archiefnominatie;

    @XmlElement(namespace = ZKN)
    public String registratiedatum;

    @XmlElement(namespace = ZKN)
    public String einddatum;

    @XmlElement(namespace = ZKN)
    public Rol isVan;

    @XmlElement(namespace = ZKN)
    public Heeft heeft;

    @XmlElement(namespace = ZKN, name = "heeftAlsBelanghebbende")
    public Rol heeftAlsBelanghebbende;

    @XmlElement(namespace = ZKN, name = "heeftAlsInitiator")
    public Rol heeftAlsInitiator;

    @XmlElement(namespace = ZKN, name = "heeftAlsUitvoerende")
    public Rol heeftAlsUitvoerende;

    @XmlElement(namespace = ZKN, name = "heeftBetrekkingOp")
    public Rol heeftBetrekkingOp;

    @XmlElement(namespace = ZKN, name = "heeftAlsGemachtigde")
    public Rol heeftAlsGemachtigde;

    @XmlElement(namespace = ZKN, name = "heeftAlsOverigBetrokkene")
    public Rol heeftAlsOverigBetrokkene;

//    @XmlElement(namespace = ZKN, name = "heeftAlsVerantwoordelijke")
//    public Rol heeftAlsVerantwoordelijke;

//    @XmlElement(namespace = ZKN, name = "heeftRelevant")
//    //public ZdsRelatieZaakDocument heeftRelevant;
//    public List<ZdsRelatieZaakDocument> heeftRelevant;


}
