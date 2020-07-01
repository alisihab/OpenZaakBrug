package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ZgwKenmerk {

    @SerializedName("kenmerk")
    @Expose
    public String kenmerk;
    @SerializedName("bron")
    @Expose
    public String bron;
}
