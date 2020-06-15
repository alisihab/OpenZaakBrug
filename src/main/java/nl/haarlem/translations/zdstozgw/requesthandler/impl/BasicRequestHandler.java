package nl.haarlem.translations.zdstozgw.requesthandler.impl;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class BasicRequestHandler extends RequestHandler {


    @Autowired
    public BasicRequestHandler(Converter converter, ConfigService configService) {
        super(converter, configService);
    }

    @Override
    public String execute(String request) {
        return this.converter.convert(request);
    }
}
