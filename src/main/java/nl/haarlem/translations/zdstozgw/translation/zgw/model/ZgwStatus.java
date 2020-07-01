package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ZgwStatus {
    @Expose
    public String zaak;

    @Expose
    public String statustype;

    @SerializedName("datumStatusGezet")
    @Expose
    public String datumStatusGezet;

    @SerializedName("statustoelichting")
    @Expose
    public String statustoelichting;

    @Expose
    public String url;

    @Expose
    public String uuid;

}
