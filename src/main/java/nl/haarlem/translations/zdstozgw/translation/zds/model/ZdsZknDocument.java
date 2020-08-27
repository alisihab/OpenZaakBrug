package nl.haarlem.translations.zdstozgw.translation.zds.model;

import nl.haarlem.translations.zdstozgw.utils.StufUtils;

import javax.xml.bind.annotation.XmlElement;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

abstract public class ZdsZknDocument extends  ZdsObject {
    @XmlElement(namespace = ZKN, name = "stuurgegevens")
    public ZdsStuurgegevens stuurgegevens;
    
    protected ZdsZknDocument() {    	
    }
    
    public ZdsZknDocument(ZdsStuurgegevens fromRequest) {
        this.stuurgegevens = new ZdsStuurgegevens(fromRequest);
        this.stuurgegevens.tijdstipBericht = StufUtils.getStufDateTime();
        this.stuurgegevens.referentienummer = java.util.UUID.randomUUID().toString();    
    }
}
