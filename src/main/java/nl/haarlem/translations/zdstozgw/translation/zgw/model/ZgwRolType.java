package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ZgwRolType {
/*
	"url":"https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/roltypen/55fa4bf9-8c77-4721-aa7f-ae4c9ab2b290",
	"zaaktype":"https://fieldlab.westeurope.cloudapp.azure.com/catalogi/api/v1/zaaktypen/8a0ecb65-c3fd-4110-8599-297f84236ae5",
	"omschrijving":"heeftAlsVerantwoordelijke",
	"omschrijvingGeneriek":"beslisser"
 */
	
	@SerializedName("url")
	@Expose
	public String url;
//	@SerializedName("uuid")
//	@Expose
//	public String uuid;
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
