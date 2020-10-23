package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class ZgwAdres extends ZgwObject {
	@Expose
	public String aoaIdentificatie;
	@Expose
	public String wplWoonplaatsNaam;
	@Expose
	public String gorOpenbareRuimteNaam;
	@Expose
	public String aoaPostcode;
	@Expose
	public String aoaHuisnummer;
	@Expose
	public String aoaHuisletter;
	@Expose
	public String aoaHuisnummertoevoeging;
	@Expose
	public String inpLocatiebeschrijving;
}
