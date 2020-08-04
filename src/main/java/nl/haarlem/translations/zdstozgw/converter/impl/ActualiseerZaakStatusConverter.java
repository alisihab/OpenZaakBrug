package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Bv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Fo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01ActualiseerZaakstatus;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class ActualiseerZaakStatusConverter extends Converter {

    public ActualiseerZaakStatusConverter(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String request) {
        ZakLk01ActualiseerZaakstatus zakLk01 = new ZakLk01ActualiseerZaakstatus();
        try {
            XmlUtils.getStUFObject(request, ZakLk01ActualiseerZaakstatus.class);
            this.getZaakService().actualiseerZaakstatus(zakLk01);
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
