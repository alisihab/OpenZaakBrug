package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsParameters;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLa01GeefZaakDetails;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GeefZaakDetailsConverter extends Converter {

    public GeefZaakDetailsConverter(Translation translation, ZaakService zaakService) {
        super(translation, zaakService);
    }

    @Override
    public String convert(String action, String request) throws ConverterException {
        var zdsZakLv01 = (ZdsZakLv01) XmlUtils.getStUFObject(request, ZdsZakLv01.class);
        ZdsZakLa01GeefZaakDetails zdsZakLa01GeefZaakDetails = this.getZaakService().getZaakDetails(zdsZakLv01);
        zdsZakLa01GeefZaakDetails.zdsParameters = new ZdsParameters(zdsZakLv01.zdsParameters);
        return XmlUtils.getSOAPMessageFromObject(zdsZakLa01GeefZaakDetails);
    }
}
