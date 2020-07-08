package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ZgwBasicZaak extends ZgwObject {
	@SerializedName("identificatie")
	@Expose
	public String identificatie;
	@SerializedName("bronorganisatie")
	@Expose
	public String bronorganisatie;
	@SerializedName("omschrijving")
	@Expose
	public String omschrijving;
	@SerializedName("toelichting")
	@Expose
	public String toelichting;
	@SerializedName("zaaktype")
	@Expose
	public String zaaktype;
	@SerializedName("registratiedatum")
	@Expose
	public String registratiedatum;
	@SerializedName("verantwoordelijkeOrganisatie")
	@Expose
	public String verantwoordelijkeOrganisatie;
	@SerializedName("startdatum")
	@Expose
	public String startdatum;
	@SerializedName("einddatum")
	@Expose
	public String einddatum;
	@SerializedName("einddatumGepland")
	@Expose
	public String einddatumGepland;
	@SerializedName("uiterlijkeEinddatumAfdoening")
	@Expose
	public String uiterlijkeEinddatumAfdoening;
	@SerializedName("publicatiedatum")
	@Expose
	public String publicatiedatum;
	@SerializedName("communicatiekanaal")
	@Expose
	public String communicatiekanaal;
	@SerializedName("opschorting")
	@Expose
	public Opschorting opschorting;	
}
