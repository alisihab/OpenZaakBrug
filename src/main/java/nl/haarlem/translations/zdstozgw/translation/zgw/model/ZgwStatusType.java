package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;

public class ZgwStatusType {

    @Expose
    public String url;

    @Expose
    public String omschrijving;

    @Expose
    public String omschrijvingGeneriek;

    @Expose
    public String statustekst;

    @Expose
    public String zaaktype;

    @Expose
    public int volgnummer;

    @Expose
    public String isEindstatus;

    @Expose
    public String informeren;
}
