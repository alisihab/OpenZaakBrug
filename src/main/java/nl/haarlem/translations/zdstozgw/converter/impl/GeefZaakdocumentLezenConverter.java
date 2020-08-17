package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLa01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsParameters;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GeefZaakdocumentLezenConverter extends Converter {

    public GeefZaakdocumentLezenConverter(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String action, String request) {
        ZdsEdcLv01 zdsEdcLv01 = new ZdsEdcLv01();
        try {
            zdsEdcLv01 = (ZdsEdcLv01) XmlUtils.getStUFObject(request, ZdsEdcLv01.class);
            ZdsEdcLa01 zdsEdcLa01 = this.getZaakService().getZaakDocumentLezen(zdsEdcLv01);
            zdsEdcLa01.zdsParameters = new ZdsParameters(zdsEdcLv01.zdsParameters);
            return XmlUtils.getSOAPMessageFromObject(zdsEdcLa01, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            var fo03 = new ZdsFo03(zdsEdcLv01.zdsStuurgegevens);
            fo03.body = new ZdsFo03.Body(ex);
            return XmlUtils.getSOAPMessageFromObject(fo03, true);
        }
    }

}

