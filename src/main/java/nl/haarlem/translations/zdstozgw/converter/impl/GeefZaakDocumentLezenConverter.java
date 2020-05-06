package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.translation.ZaakTranslator;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLa01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Ontvanger;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Stuurgegevens;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Zender;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GeefZaakDocumentLezenConverter extends Converter {

	public GeefZaakDocumentLezenConverter(String templatePath, String legacyService) {
		super(templatePath, legacyService);
	}

	@Override
	public String Convert(ZGWClient zgwClient, ConfigService configService, ApplicationParameterRepository repository, String requestBody) {
		String result = "";
		try {

			EdcLv01 object = (EdcLv01) XmlUtils.getStUFObject(requestBody, EdcLv01.class);
			var translator = new ZaakTranslator(zgwClient, configService);
			EdcLa01 edcLa01 = translator.getZaakDoumentLezen((EdcLv01) object);

			edcLa01.stuurgegevens = new Stuurgegevens();
			edcLa01.stuurgegevens.zender = new Zender();
			edcLa01.stuurgegevens.zender.applicatie = ((EdcLv01) object).stuurgegevens.ontvanger.applicatie;
			edcLa01.stuurgegevens.zender.organisatie = ((EdcLv01) object).stuurgegevens.ontvanger.organisatie;
			edcLa01.stuurgegevens.zender.gebruiker = ((EdcLv01) object).stuurgegevens.ontvanger.organisatie;

			edcLa01.stuurgegevens.ontvanger = new Ontvanger();
			edcLa01.stuurgegevens.ontvanger.applicatie = ((EdcLv01) object).stuurgegevens.zender.applicatie;
			edcLa01.stuurgegevens.ontvanger.organisatie = ((EdcLv01) object).stuurgegevens.zender.organisatie;
			edcLa01.stuurgegevens.ontvanger.gebruiker = ((EdcLv01) object).stuurgegevens.zender.gebruiker;

			edcLa01.stuurgegevens.berichtcode = "La01";

			return XmlUtils.getSOAPMessageFromObject(edcLa01);

		} catch (Exception ex) {
			ex.printStackTrace();
			var f03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.F03();
			f03.setFaultString("Object was not saved");
			f03.setCode("StUF046");
			f03.setOmschrijving("Object niet opgeslagen");
			f03.setDetails(ex.getMessage());
			return f03.getSoapMessageAsString();
		}
	}
}