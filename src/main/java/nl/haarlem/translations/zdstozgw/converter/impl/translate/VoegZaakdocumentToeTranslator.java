package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class VoegZaakdocumentToeTranslator extends Converter {

    public VoegZaakdocumentToeTranslator(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

	@Override
	public ZdsDocument load(String request) throws ResponseStatusException {
        return (ZdsEdcLk01) XmlUtils.getStUFObject(request, ZdsEdcLk01.class);
	}

	@Override
	public ZdsDocument execute(ZdsDocument document) throws ConverterException {
		var zdsEdcLk01 = (ZdsEdcLk01) document;
        this.getZaakService().voegZaakDocumentToe(zdsEdcLk01);
        var bv03 = new ZdsBv03(zdsEdcLk01.stuurgegevens);	
		return bv03;
	}    
}
