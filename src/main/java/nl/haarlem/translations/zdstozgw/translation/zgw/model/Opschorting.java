package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Opschorting {

    @SerializedName("indicatie")
    @Expose
    public Boolean indicatie;
    @SerializedName("reden")
    @Expose
    public String reden;

}
