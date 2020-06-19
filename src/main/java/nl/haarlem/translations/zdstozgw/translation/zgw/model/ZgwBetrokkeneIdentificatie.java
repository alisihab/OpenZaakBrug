package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ZgwBetrokkeneIdentificatie extends ZgwObject {
	
	// medewerker
	@Expose
	public String identificatie;
	@Expose
	public String achternaam;
	@Expose
	public String voorletters;
	@Expose
	public String voorvoegselAchternaam;

	// natuurlijk_persoon
	@Expose
	public String inpBsn;
	@Expose
	public String anpIdentificatie;
	@Expose
	public String inpA_nummer;
	@Expose
	public String inpgeslachtsnaam;
	@Expose
	public String geslachtsnaam;
	@Expose
	public String voorvoegselGeslachtsnaam;
	@Expose
	public String voornamen;
	@Expose
	public String geslachtsaanduiding;
	@Expose
	public String geboortedatum;
	@Expose
	public String verblijfsadres;
	@Expose
	public String subVerblijfBuitenland;
}
