package nl.haarlem.translations.zdstozgw.config.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Translation {
    @SerializedName("translation")
    @Expose
    public String translation;
    @SerializedName("path")
    @Expose
    public String path;
    @SerializedName("soapaction")
    @Expose
    public String soapAction;
    @SerializedName("applicatie")
    @Expose
    public String applicatie;
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