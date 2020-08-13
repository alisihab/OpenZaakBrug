package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Bv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Fo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class CreeerZaakConverter extends Converter {

    public CreeerZaakConverter(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String request) {
        ZakLk01 zakLk01 = new ZakLk01();
        try {
            zakLk01 = (ZakLk01) XmlUtils.getStUFObject(request, ZakLk01.class);
            this.getZaakService().creeerZaak(zakLk01);
            var bv03 = new Bv03(zakLk01.stuurgegevens);
            return XmlUtils.getSOAPMessageFromObject(bv03, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            var fo03 = new Fo03(zakLk01.stuurgegevens);
            fo03.body = new Fo03.Body(ex);
            return XmlUtils.getSOAPMessageFromObject(fo03, true);
        }
    }
}