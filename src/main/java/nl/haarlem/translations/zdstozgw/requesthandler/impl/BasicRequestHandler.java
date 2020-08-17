package nl.haarlem.translations.zdstozgw.requesthandler.impl;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class BasicRequestHandler extends RequestHandler {


    @Autowired
    public BasicRequestHandler(Converter converter, ConfigService configService) {
        super(converter, configService);
    }

    @Override
    public String execute(String path, String soapAction, String request) throws ConverterException {
        return this.converter.convert(soapAction, request);
    }
}