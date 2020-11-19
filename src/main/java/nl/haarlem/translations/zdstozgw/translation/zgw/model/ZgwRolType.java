package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ZgwRolType extends ZgwObject {
	@SerializedName("zaaktype")
	@Expose
	public String zaaktype;
	@SerializedName("omschrijving")
	@Expose
	public String omschrijving;
	@SerializedName("omschrijvingGeneriek")
	@Expose
	public String omschrijvingGeneriek;
}
