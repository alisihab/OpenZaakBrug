package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.translation.ZaakTranslator;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01_v2;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.client.ZGWClient;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class CreeerZaakConverter extends Converter {
	public CreeerZaakConverter(String templatePath, String legacyService) {
		super(templatePath, legacyService);
	}

	@Override
	public String Convert(ZaakService zaakService, ApplicationParameterRepository repository, String requestBody) {
		try {
			ZakLk01_v2 object = (ZakLk01_v2) XmlUtils.getStUFObject(requestBody, ZakLk01_v2.class);

			var zaak = zaakService.creeerZaak(object);
			var bv03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.Bv03();
			bv03.setReferentienummer(zaak.getUuid());
			return bv03.getSoapMessageAsString();
		} catch (ZGWClient.ZGWClientException hsce) {
			throw new ConverterException(this, hsce.getMessage(), hsce.getDetails(), hsce);
		} catch (ZaakTranslator.ZaakTranslatorException zte) {
			throw new ConverterException(this, zte.getMessage(), requestBody, zte);
		}
	}
}