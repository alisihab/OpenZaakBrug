package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ZgwStatusType {

	@SerializedName("url")
	@Expose
	public String url;

	@SerializedName("omschrijving")
	@Expose
	public String omschrijving;

	@SerializedName("omschrijvingGeneriek")
	@Expose
	public String omschrijvingGeneriek;

	@SerializedName("statustekst")
	@Expose
	public String statustekst;

	@SerializedName("zaaktype")
	@Expose
	public String zaaktype;

	@SerializedName("volgnummer")
	@Expose
	public int volgnummer;

	@SerializedName("isEindstatus")
	@Expose
	public String isEindstatus;

	@SerializedName("informeren")
	@Expose
	public String informeren;
}
