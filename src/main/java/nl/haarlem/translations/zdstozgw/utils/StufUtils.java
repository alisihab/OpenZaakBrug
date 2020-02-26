package nl.haarlem.translations.zdstozgw.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StufUtils {

    public static String getTijdstipBericht(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMddHHmmss");
        return now.format(formatter);
    }
}
