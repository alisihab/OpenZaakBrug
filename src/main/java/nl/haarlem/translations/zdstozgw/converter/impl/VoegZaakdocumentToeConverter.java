package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class VoegZaakdocumentToeConverter extends Converter {

    public VoegZaakdocumentToeConverter(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String action, String request) {
        ZdsEdcLk01 zdsEdcLk01 = new ZdsEdcLk01();
        try {
            zdsEdcLk01 = (ZdsEdcLk01) XmlUtils.getStUFObject(request, ZdsEdcLk01.class);
            this.getZaakService().voegZaakDocumentToe(zdsEdcLk01);
            var bv03 = new ZdsBv03(zdsEdcLk01.zdsStuurgegevens);
            return XmlUtils.getSOAPMessageFromObject(bv03, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            var fo03 = new ZdsFo03(zdsEdcLk01.zdsStuurgegevens);
            fo03.body = new ZdsFo03.Body(ex);
            return XmlUtils.getSOAPMessageFromObject(fo03, true);
        }
    }
}
