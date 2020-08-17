package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01ActualiseerZaakstatus;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class ActualiseerZaakStatusConverter extends Converter {

    public ActualiseerZaakStatusConverter(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String action, String request) throws ConverterException {
        var zakLk01 = (ZdsZakLk01ActualiseerZaakstatus) XmlUtils.getStUFObject(request, ZdsZakLk01ActualiseerZaakstatus.class);
        this.getZaakService().actualiseerZaakstatus(zakLk01);
        var bv03 = new ZdsBv03(zakLk01.zdsStuurgegevens);
        return XmlUtils.getSOAPMessageFromObject(bv03);
    }
}
