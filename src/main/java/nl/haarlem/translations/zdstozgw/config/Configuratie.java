package nl.haarlem.translations.zdstozgw.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class Configuratie {

    @SerializedName("zaakTypes")
    @Expose
    public List<ZaakType> zaakTypes = null;
    @SerializedName("organisaties")
    @Expose
    public List<Organisatie> organisaties = null;
    @SerializedName("documentTypes")
    @Expose
    public List<DocumentType> documentTypes = null;
}
