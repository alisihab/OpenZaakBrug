package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ZgwInformatieObjectType {
    
    @Expose
    public String url;
    
    @Expose
    public String uuid;

    @Expose
    public String omschrijving;
}

