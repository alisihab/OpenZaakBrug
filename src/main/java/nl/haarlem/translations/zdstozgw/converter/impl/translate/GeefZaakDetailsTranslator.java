package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsParameters;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsStuurgegevens;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakAntwoord;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLa01GeefZaakDetails;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GeefZaakDetailsTranslator extends Converter {

	public GeefZaakDetailsTranslator(RequestResponseCycle session, Translation translation, ZaakService zaakService) {
		super(session, translation, zaakService);
	}

	@Override
	public void load() throws ResponseStatusException {
		this.zdsDocument = (ZdsZakLv01) XmlUtils.getStUFObject(this.getSession().getClientRequestBody(), ZdsZakLv01.class);
	}

	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		var zdsZakLv01 = (ZdsZakLv01) this.getZdsDocument();

		ZdsZakLa01GeefZaakDetails zdsResponse = new ZdsZakLa01GeefZaakDetails();
		zdsResponse.stuurgegevens = new ZdsStuurgegevens(zdsZakLv01.stuurgegevens, this.getSession().getReferentienummer());
		zdsResponse.stuurgegevens.berichtcode = "La01";
		zdsResponse.stuurgegevens.entiteittype = "ZAK";
		zdsResponse.parameters = new ZdsParameters(zdsZakLv01.parameters);
		zdsResponse.antwoord = new ZdsZaakAntwoord();

		if (zdsZakLv01.gelijk != null && zdsZakLv01.gelijk.identificatie != null) {
			zdsResponse.antwoord.zaak = new ArrayList<ZdsZaak>();
			
			this.getSession().setFunctie("GeefZaakDetails-ZaakId");		
			this.getSession().setKenmerk("zaakidentificatie:" + zdsZakLv01.gelijk.identificatie);
			
			zdsResponse.antwoord.zaak
					.add(this.getZaakService().getZaakDetailsByIdentificatie(zdsZakLv01.gelijk.identificatie));
		} else if (zdsZakLv01.gelijk != null && zdsZakLv01.gelijk.heeftAlsInitiator != null
				&& zdsZakLv01.gelijk.heeftAlsInitiator.gerelateerde != null
				&& zdsZakLv01.gelijk.heeftAlsInitiator.gerelateerde.identificatie != null) {
			var gerelateerdeidentificatie = zdsZakLv01.gelijk.heeftAlsInitiator.gerelateerde.identificatie;
			if (!gerelateerdeidentificatie.startsWith("11")) {
				throw new ConverterException("gerelateerdeidentificatie: '" + gerelateerdeidentificatie
						+ "' moet beginnen met '11' gevolgd door het bsnnummer");
			}
			var bsn = gerelateerdeidentificatie.substring(2);
			
			this.getSession().setFunctie("GeefZaakDetails-Bsn");		
			this.getSession().setKenmerk("bsn:" + bsn);

			zdsResponse.antwoord.zaak = this.getZaakService()
					.getZaakDetailsByBsn(gerelateerdeidentificatie.substring(2));
		} else {
			throw new ConverterException("Niet ondersteunde vraag binnengekregen");
		}
		var response = XmlUtils.getSOAPMessageFromObject(zdsResponse);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
