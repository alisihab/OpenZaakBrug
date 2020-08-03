package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Bv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Fo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01UpdateZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class UpdateZaakConverter extends Converter {

    public UpdateZaakConverter(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String request) {
        ZakLk01UpdateZaak zakLk01UpdateZaak = new ZakLk01UpdateZaak();
        try {
            zakLk01UpdateZaak = (ZakLk01UpdateZaak) XmlUtils.getStUFObject(request, ZakLk01UpdateZaak.class);
            this.getZaakService().updateZaak(zakLk01UpdateZaak);
            var bv03 = new Bv03(zakLk01UpdateZaak.stuurgegevens);
            return XmlUtils.getSOAPMessageFromObject(bv03, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            var fo03 = new Fo03(zakLk01UpdateZaak.stuurgegevens);
            fo03.body = new Fo03.Body(ex);
            return XmlUtils.getSOAPMessageFromObject(fo03, true);
        }
    }
}
