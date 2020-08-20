package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class CreeerZaakTranslator extends Converter {

    public CreeerZaakTranslator(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

	@Override
	public ZdsDocument load(String request) throws ResponseStatusException {
        return (ZdsZakLk01) XmlUtils.getStUFObject(request, ZdsZakLk01.class);
	}

	@Override
	public ZdsDocument execute(ZdsDocument document) throws ConverterException {
		var zgwZaak = this.getZaakService().creeerZaak((ZdsZakLk01) document);
		return new ZdsBv03(document.stuurgegevens);				
	}
}