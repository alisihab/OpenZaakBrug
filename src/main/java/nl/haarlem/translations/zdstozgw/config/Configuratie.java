package nl.haarlem.translations.zdstozgw.config;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

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
    @SerializedName("translations")
    @Expose    
    public List<Translation> translations = null;
}
