package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ZgwVerlenging {

	@SerializedName("reden")
	@Expose
	public String reden;
	@SerializedName("duur")
	@Expose
	public String duur;

}
