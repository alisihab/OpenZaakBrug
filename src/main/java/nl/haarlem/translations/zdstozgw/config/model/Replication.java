package nl.haarlem.translations.zdstozgw.config.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Replication {
	
	@SerializedName("geefZaakdetails")
	@Expose    
	public Service geefZaakdetails = null;
	
	@SerializedName("geefLijstZaakdocumenten")
    @Expose    	
	public Service geefLijstZaakdocumenten = null;
	
	@SerializedName("geefZaakdocumentLezen")
    @Expose    
	public Service geefZaakdocumentLezen = null;

}
