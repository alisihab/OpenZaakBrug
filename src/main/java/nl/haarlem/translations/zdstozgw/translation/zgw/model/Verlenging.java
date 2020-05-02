package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Verlenging {

	@SerializedName("reden")
	@Expose
	public String reden;
	@SerializedName("duur")
	@Expose
	public Object duur;

}
