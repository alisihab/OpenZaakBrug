package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ZgwZaakInformatieObject {
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("uuid")
    @Expose
    public String uuid;
    @SerializedName("informatieobject")
    @Expose
    public String informatieobject;
    @SerializedName("zaak")
    @Expose
    public String zaak;
    @SerializedName("aardRelatieWeergave")
    @Expose
    public String aardRelatieWeergave;
    @SerializedName("titel")
    @Expose
    public String titel;
    @SerializedName("beschrijving")
    @Expose
    public String beschrijving;
    @SerializedName("registratiedatum")
    @Expose
    public String registratiedatum;
}
