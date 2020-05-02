package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.translation.zgw.model.ZgwZaakInformatieObject;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class VoegZaakdocumentToeConverter extends Converter {
	public VoegZaakdocumentToeConverter(String templatePath, String legacyService) {
		super(templatePath, legacyService);
	}

	@Override
	public String Convert(ZaakService zaakService, ApplicationParameterRepository repository, String requestbody) {
		try {

			EdcLk01 object = (EdcLk01) XmlUtils.getStUFObject(requestbody, EdcLk01.class);
			ZgwZaakInformatieObject zgwZaakInformatieObject = zaakService.voegZaakDocumentToe((EdcLk01) object);
			var bv03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.Bv03();
			bv03.setReferentienummer(zgwZaakInformatieObject.getUuid());
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