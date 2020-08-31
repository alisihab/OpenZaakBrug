package nl.haarlem.translations.zdstozgw.config.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class Configuratie {
    @SerializedName("requestHandlerImplementation")
    @Expose
    public String requestHandlerImplementation = null;    
	@SerializedName("organisaties")
    @Expose    
    public List<Organisatie> organisaties = null;
	@SerializedName("geefZaakdetails")
    @Expose    
	public Service geefZaakdetails = null;
	@SerializedName("geefLijstZaakdocumenten")
    @Expose    	
	public Service geefLijstZaakdocumenten = null;
	@SerializedName("geefZaakdocumentLezen")
    @Expose    
	public Service geefZaakdocumentLezen = null;
	@SerializedName("zgwRolOmschrijving")
    @Expose
    public ZgwRolOmschrijving zgwRolOmschrijving = null;	
    @SerializedName("translations")
    @Expose
    public List<Translation> translations = null;
}