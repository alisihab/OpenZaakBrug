package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZgwResultaat {
	@SerializedName("zaak")
	@Expose	
	public String zaak;
	
	@SerializedName("resultaattype")
	@Expose		
	public String resultaattype;

	@SerializedName("toelichting")
	@Expose		
	public String toelichting;
	
}
