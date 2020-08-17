package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class UpdateZaakTranslator extends Converter {

    public UpdateZaakTranslator(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String action, String request) throws ConverterException {
        var zdsZakLk01 = (ZdsZakLk01) XmlUtils.getStUFObject(request, ZdsZakLk01.class);
        this.getZaakService().updateZaak(zdsZakLk01);
        var bv03 = new ZdsBv03(zdsZakLk01.zdsStuurgegevens);
        return XmlUtils.getSOAPMessageFromObject(bv03);
    }
}
