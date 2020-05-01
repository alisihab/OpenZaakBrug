package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;

public class ZgwStatus {
    @Expose
    public String zaak;

    @Expose
    public String statustype;

    @Expose
    public String datumStatusGezet;

    @Expose
    public String statustoelichting;

    @Expose
    public String url;

    @Expose
    public String uuid;

}
