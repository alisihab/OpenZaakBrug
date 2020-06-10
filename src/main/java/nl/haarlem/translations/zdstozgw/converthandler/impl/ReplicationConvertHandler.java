package nl.haarlem.translations.zdstozgw.converthandler.impl;

import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converthandler.ConvertHandler;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class ReplicationConvertHandler extends ConvertHandler {

    private ZaakService zaakService;
    private Object object;

    public ReplicationConvertHandler(Converter converter, ZaakService zaakService, Object object) {
        super(converter);
        this.zaakService = zaakService;
        this.object = object;
    }

    @Override
    public String execute() {
        return this.converter.Convert(this.zaakService, this.object);
    }
}
