package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class ZgwZaak {

    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("uuid")
    @Expose
    public String uuid;
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
    public Object hoofdzaak;
    @SerializedName("deelzaken")
    @Expose
    public List<Object> deelzaken = null;
    @SerializedName("relevanteAndereZaken")
    @Expose
    public List<Object> relevanteAndereZaken = null;
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
