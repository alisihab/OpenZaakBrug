package nl.haarlem.translations.zdstozgw.config.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ZaakType {

    @SerializedName("code")
    @Expose
    public String code;
    @SerializedName("zaakType")
    @Expose
    public String zaakType;
    @SerializedName("zaakTypeOmschrijving")
    @Expose
    public String zaakTypeOmschrijving;
    @SerializedName("ingangsdatumObject")
    @Expose
    public String ingangsdatumObject;
    @SerializedName("initiatorRolTypeUrl")
    @Expose
    public String initiatorRolTypeUrl;
    @Expose
    public String[] statustypen;
}
