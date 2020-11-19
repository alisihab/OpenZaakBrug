package nl.haarlem.translations.zdstozgw.translation.zds.model;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.StufUtils;

@Data
@XmlRootElement(name = "Bv03Bericht", namespace = STUF)
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsBv03 extends ZdsStufDocument {

	public ZdsBv03() {
	}

	public ZdsBv03(ZdsStuurgegevens zdsStuurgegevens, String referentienummer) {
		this.stuurgegevens = new ZdsStuurgegevens(zdsStuurgegevens, referentienummer);
		this.stuurgegevens.tijdstipBericht = StufUtils.getStufDateTime();
		this.stuurgegevens.berichtcode = "Bv03";
		this.stuurgegevens.crossRefnummer = zdsStuurgegevens.referentienummer;
	}
}
