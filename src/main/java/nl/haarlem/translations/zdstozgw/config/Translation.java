package nl.haarlem.translations.zdstozgw.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Translation {
    @SerializedName("translation")
    @Expose
    public String translation;
    @SerializedName("soapaction")
    @Expose
    public String soapaction;
    @SerializedName("requestcontains")
    @Expose
    public String requestcontains;
    @SerializedName("template")
    @Expose
    public String template;
    @SerializedName("implementation")
    @Expose
    public String implementation;
    @SerializedName("legacyservice")
    @Expose
    public String legacyservice;
}
