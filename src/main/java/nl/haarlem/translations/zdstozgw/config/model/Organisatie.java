package nl.haarlem.translations.zdstozgw.config.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Organisatie {

    @SerializedName("gemeenteCode")
    @Expose
    public String gemeenteCode;
    @SerializedName("RSIN")
    @Expose
    public String rSIN;

}
