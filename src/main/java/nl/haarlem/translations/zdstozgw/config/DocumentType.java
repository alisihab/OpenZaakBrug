package nl.haarlem.translations.zdstozgw.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class DocumentType {

    @SerializedName("documentType")
    @Expose
    public String documentType;
    @SerializedName("omschrijving")
    @Expose
    public String omschrijving;
}
