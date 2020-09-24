package nl.haarlem.translations.zdstozgw.config.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Service {
	@SerializedName("soapaction")
	@Expose
	public String soapaction;
	@SerializedName("url")
	@Expose
	public String url;
}
