package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ZwgZaakInformatieObject {
	@SerializedName("informatieobject")
	@Expose
	public String informatieobject;

	@SerializedName("zaak")
	@Expose
	public String zaak;

	@SerializedName("titel")
	@Expose
	public String titel;

	@SerializedName("beschrijving")
	@Expose
	public String beschrijving;
}
