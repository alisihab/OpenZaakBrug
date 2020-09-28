package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

import javax.xml.bind.annotation.XmlElement;

import nl.haarlem.translations.zdstozgw.utils.StufUtils;

abstract public class ZdsZknDocument extends ZdsObject {
	@XmlElement(namespace = ZKN, name = "stuurgegevens")
	public ZdsStuurgegevens stuurgegevens;

	protected ZdsZknDocument() {
	}

	public ZdsZknDocument(ZdsStuurgegevens fromRequest, String referentienummer) {
		this.stuurgegevens = new ZdsStuurgegevens(fromRequest, referentienummer);
		this.stuurgegevens.tijdstipBericht = StufUtils.getStufDateTime();
	}
}
