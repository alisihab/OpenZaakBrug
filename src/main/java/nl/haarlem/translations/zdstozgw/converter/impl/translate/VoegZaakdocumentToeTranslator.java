package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class VoegZaakdocumentToeTranslator extends Converter {

    public VoegZaakdocumentToeTranslator(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String action, String request) throws ConverterException {
        var zdsEdcLk01 = (ZdsEdcLk01) XmlUtils.getStUFObject(request, ZdsEdcLk01.class);
        this.getZaakService().voegZaakDocumentToe(zdsEdcLk01);
        var bv03 = new ZdsBv03(zdsEdcLk01.stuurgegevens);
        return XmlUtils.getSOAPMessageFromObject(bv03);
    }
}
