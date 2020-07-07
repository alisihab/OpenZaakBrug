package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.SpringContext;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.ZaakTranslator;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLa01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLv01_v2;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GeefZaakDetailsConverter extends Converter {

    //todo: merge zaakservice & zaaktranslator into zaakTranslationService
    private ZaakTranslator zaakTranslator;

    public GeefZaakDetailsConverter(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
        this.zaakTranslator = SpringContext.getBean(ZaakTranslator.class);
    }

    @Override
    public String convert(String request) {
        try {

            ZakLv01_v2 zakLv01 = (ZakLv01_v2) XmlUtils.getStUFObject(request, ZakLv01_v2.class);
            ZakLa01 zakLa01 = this.getZaakService().getZaakDetails(zakLv01);
            return XmlUtils.getSOAPMessageFromObject(zakLa01);

        } catch (Exception ex) {
            ex.printStackTrace();
            var f03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.F03();
            f03.setFaultString("Object was not saved");
            f03.setCode("StUF046");
            f03.setOmschrijving("Object niet opgeslagen");
            f03.setDetails(ex.getMessage());
            return f03.getSoapMessageAsString();
        }
    }
}
