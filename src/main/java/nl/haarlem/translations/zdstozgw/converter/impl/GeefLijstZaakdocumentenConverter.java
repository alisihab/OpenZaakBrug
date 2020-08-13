package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Fo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Parameters;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLa01LijstZaakdocumenten;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GeefLijstZaakdocumentenConverter extends Converter {

    public GeefLijstZaakdocumentenConverter(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String request) {
        ZakLv01 zakLv01 = new ZakLv01();
        try {
            zakLv01 = (ZakLv01) XmlUtils.getStUFObject(request, ZakLv01.class);
            ZakLa01LijstZaakdocumenten zakLa01LijstZaakdocumenten = this.getZaakService().geefLijstZaakdocumenten(zakLv01);
            zakLa01LijstZaakdocumenten.parameters = new Parameters(zakLv01.parameters);
            return XmlUtils.getSOAPMessageFromObject(zakLa01LijstZaakdocumenten, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            var fo03 = new Fo03(zakLv01.stuurgegevens);
            fo03.body = new Fo03.Body(ex);
            return XmlUtils.getSOAPMessageFromObject(fo03, true);
        }
    }
}
