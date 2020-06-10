package nl.haarlem.translations.zdstozgw.converthandler.impl;

import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converthandler.ConvertHandler;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class BasicConvertHandler extends ConvertHandler {

    private ZaakService zaakService;

    public BasicConvertHandler(Converter converter, ZaakService zaakService) {
        super(converter);
        this.zaakService = zaakService;
    }

    @Override
    public String execute() {
        return this.converter.Convert(this.zaakService, null);
    }
}
