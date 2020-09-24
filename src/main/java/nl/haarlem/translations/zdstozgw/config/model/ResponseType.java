package nl.haarlem.translations.zdstozgw.config.model;

import com.google.gson.annotations.SerializedName;

public enum ResponseType {
	@SerializedName("zds")
	ZDS, @SerializedName("zgw")
	ZGW
}
