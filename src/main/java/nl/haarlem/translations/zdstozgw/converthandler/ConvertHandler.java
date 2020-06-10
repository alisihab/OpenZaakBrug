package nl.haarlem.translations.zdstozgw.converthandler;

import nl.haarlem.translations.zdstozgw.converter.Converter;

public abstract class ConvertHandler {

    protected Converter converter;

    public ConvertHandler(Converter converter){
        this.converter = converter;
    }

    public abstract <T> T execute();
}
