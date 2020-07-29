package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ZgwObject {
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("uuid")
    @Expose
    public String uuid;

}
