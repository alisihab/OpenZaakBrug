package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ZgwStatus {
    @SerializedName("zaak")
    @Expose
    public String zaak;

    @SerializedName("statustype")
    @Expose
    public String statustype;

    @SerializedName("datumStatusGezet")
    @Expose
    public String datumStatusGezet;

    @SerializedName("statustoelichting")
    @Expose
    public String statustoelichting;

    @SerializedName("url")
    @Expose
    public String url;

    @SerializedName("uuid")
    @Expose
    public String uuid;

}
