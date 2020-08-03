package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Bv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Fo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01CreeerZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class CreeerZaakConverter extends Converter {

    public CreeerZaakConverter(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String request) {
        ZakLk01CreeerZaak zakLk01CreeerZaak = new ZakLk01CreeerZaak();
        try {
            zakLk01CreeerZaak = (ZakLk01CreeerZaak) XmlUtils.getStUFObject(request, ZakLk01CreeerZaak.class);
            this.getZaakService().creeerZaak(zakLk01CreeerZaak);
            var bv03 = new Bv03(zakLk01CreeerZaak.stuurgegevens);
            return XmlUtils.getSOAPMessageFromObject(bv03, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            var fo03 = new Fo03(zakLk01CreeerZaak.stuurgegevens);
            fo03.body = new Fo03.Body(ex);
            return XmlUtils.getSOAPMessageFromObject(fo03, true);
        }
    }
}