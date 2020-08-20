package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsParameters;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLa01GeefZaakDetails;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GeefZaakDetailsTranslator extends Converter {

    public GeefZaakDetailsTranslator(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

	@Override
	public ZdsDocument load(String request) throws ResponseStatusException {
        return (ZdsZakLv01) XmlUtils.getStUFObject(request, ZdsZakLv01.class);
	}

	@Override
	public ZdsDocument execute(ZdsDocument document) throws ConverterException {
		var zdsZakLv01 = (ZdsZakLv01) document;
        ZdsZakLa01GeefZaakDetails zdsZakLa01GeefZaakDetails = this.getZaakService().getZaakDetails(zdsZakLv01);
        zdsZakLa01GeefZaakDetails.parameters = new ZdsParameters(zdsZakLv01.parameters);
        return zdsZakLa01GeefZaakDetails;				
	}
}
