package nl.haarlem.translations.zdstozgw.translation.zgw.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class RolNPS {

    @SerializedName("zaak")
    @Expose
    public String zaak;
    @SerializedName("betrokkeneType")
    @Expose
    public String betrokkeneType;
    @SerializedName("roltype")
    @Expose
    public String roltype;
    @SerializedName("roltoelichting")
    @Expose
    public String roltoelichting;
    @SerializedName("betrokkeneIdentificatie")
    @Expose
    public BetrokkeneIdentificatieNPS betrokkeneIdentificatieNPS;

}
