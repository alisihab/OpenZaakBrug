package nl.haarlem.translations.zdstozgw.config.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Translation {
    @SerializedName("translation")
    @Expose
    public String translation;
    @SerializedName("soapAction")
    @Expose
    public String soapaction;
    @SerializedName("applicatie")
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