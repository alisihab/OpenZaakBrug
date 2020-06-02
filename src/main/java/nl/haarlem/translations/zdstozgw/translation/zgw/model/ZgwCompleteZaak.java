package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ZgwCompleteZaak extends ZgwBasicZaak {
	@SerializedName("productenOfDiensten")
	@Expose
	public List<Object> productenOfDiensten = null;
	@SerializedName("vertrouwelijkheidaanduiding")
	@Expose
	public String vertrouwelijkheidaanduiding;
	@SerializedName("betalingsindicatie")
	@Expose
	public String betalingsindicatie;
	@SerializedName("betalingsindicatieWeergave")
	@Expose
	public String betalingsindicatieWeergave;
	@SerializedName("laatsteBetaaldatum")
	@Expose
	public Object laatsteBetaaldatum;
	@SerializedName("zaakgeometrie")
	@Expose
	public Object zaakgeometrie;
	@SerializedName("verlenging")
	@Expose
	public Verlenging verlenging;
	@SerializedName("opschorting")
	@Expose
	public Opschorting opschorting;
	@SerializedName("selectielijstklasse")
	@Expose
	public String selectielijstklasse;
	@SerializedName("hoofdzaak")
	@Expose
	public ZgwCompleteZaak hoofdzaak;
	@SerializedName("deelzaken")
	@Expose
	public List<ZgwCompleteZaak> deelzaken = null;
	@SerializedName("relevanteAndereZaken")
	@Expose
	public List<ZgwCompleteZaak> relevanteAndereZaken = null;
	@SerializedName("eigenschappen")
	@Expose
	public List<Object> eigenschappen = null;
	@SerializedName("status")
	@Expose
	public Object status;
	@SerializedName("kenmerken")
	@Expose
	public List<Object> kenmerken = null;
	@SerializedName("archiefnominatie")
	@Expose
	public String archiefnominatie;
	@SerializedName("archiefstatus")
	@Expose
	public String archiefstatus;
	@SerializedName("archiefactiedatum")
	@Expose
	public String archiefactiedatum;
	@SerializedName("resultaat")
	@Expose
	public Object resultaat;
}