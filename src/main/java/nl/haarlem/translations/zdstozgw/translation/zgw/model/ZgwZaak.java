package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ZgwZaak extends ZgwZaakPut {
	@SerializedName("url")
	@Expose
	public String url;
	@SerializedName("uuid")
	@Expose
	public String uuid;
	@SerializedName("einddatum")
	@Expose
	public String einddatum;
	@SerializedName("betalingsindicatieWeergave")
	@Expose
	public String betalingsindicatieWeergave;
	@SerializedName("deelzaken")
	@Expose
	public List<Object> deelzaken = null;
	@SerializedName("eigenschappen")
	@Expose
	public List<Object> eigenschappen = null;
	@SerializedName("status")
	@Expose
	public String status;
	@SerializedName("resultaat")
	@Expose
	public Object resultaat;
}
