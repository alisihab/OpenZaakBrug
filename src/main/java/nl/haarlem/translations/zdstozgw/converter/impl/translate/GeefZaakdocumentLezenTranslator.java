package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLa01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsParameters;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GeefZaakdocumentLezenTranslator extends Converter {

    public GeefZaakdocumentLezenTranslator(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String action, String request) throws ConverterException {
        var zdsEdcLv01 = (ZdsEdcLv01) XmlUtils.getStUFObject(request, ZdsEdcLv01.class);
        ZdsEdcLa01 zdsEdcLa01 = this.getZaakService().getZaakDocumentLezen(zdsEdcLv01);
        zdsEdcLa01.parameters = new ZdsParameters(zdsEdcLv01.parameters);
        return XmlUtils.getSOAPMessageFromObject(zdsEdcLa01);
    }

}

