package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsParameters;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLa01LijstZaakdocumenten;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GeefLijstZaakdocumentenTranslator extends Converter {

    public GeefLijstZaakdocumentenTranslator(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String action, String request) throws ConverterException {
        var zdsZakLv01 = (ZdsZakLv01) XmlUtils.getStUFObject(request, ZdsZakLv01.class);
        ZdsZakLa01LijstZaakdocumenten zdsZakLa01LijstZaakdocumenten = this.getZaakService().geefLijstZaakdocumenten(zdsZakLv01);
        zdsZakLa01LijstZaakdocumenten.parameters = new ZdsParameters(zdsZakLv01.parameters);
        return XmlUtils.getSOAPMessageFromObject(zdsZakLa01LijstZaakdocumenten);
    }
}
