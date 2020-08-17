package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class CreeerZaakConverter extends Converter {

    public CreeerZaakConverter(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String action, String request) {
        ZdsZakLk01 zdsZakLk01 = new ZdsZakLk01();
        try {
            zdsZakLk01 = (ZdsZakLk01) XmlUtils.getStUFObject(request, ZdsZakLk01.class);
            this.getZaakService().creeerZaak(zdsZakLk01);
            var bv03 = new ZdsBv03(zdsZakLk01.zdsStuurgegevens);
            return XmlUtils.getSOAPMessageFromObject(bv03, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            var fo03 = new ZdsFo03(zdsZakLk01.zdsStuurgegevens);
            fo03.body = new ZdsFo03.Body(ex);
            return XmlUtils.getSOAPMessageFromObject(fo03, true);
        }
    }
}