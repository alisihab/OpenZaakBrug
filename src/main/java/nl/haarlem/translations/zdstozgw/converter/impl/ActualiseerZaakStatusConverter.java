package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01_v2;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class ActualiseerZaakStatusConverter extends Converter {
	public ActualiseerZaakStatusConverter(String templatePath, String legacyService) {
		super(templatePath, legacyService);
	}

	@Override
	public String Convert(ZaakService zaakService, ApplicationParameterRepository repository, String requestBody) {
		try {
			ZakLk01_v2 object = (ZakLk01_v2) XmlUtils.getStUFObject(requestBody, ZakLk01_v2.class);
			var zaak = zaakService.actualiseerZaakstatus((ZakLk01_v2) object);
			var bv03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.Bv03();
			bv03.setReferentienummer(zaak.getUuid());
			return bv03.getSoapMessageAsString();

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
