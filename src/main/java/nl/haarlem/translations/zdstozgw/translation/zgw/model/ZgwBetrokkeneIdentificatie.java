package nl.haarlem.translations.zdstozgw.translation.zgw.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ZgwBetrokkeneIdentificatie {
    @SerializedName("inpBsn")
    @Expose
    public String inpBsn;

    @SerializedName("anpIdentificatie")
    @Expose
    public String anpIdentificatie;

    @SerializedName("inpA_nummer")
    @Expose
    public String inpA;

    @SerializedName("geslachtsnaam")
    @Expose
    public String geslachtsnaam;

    @SerializedName("achternaam")
    @Expose
    public String achternaam;

    @SerializedName("voorvoegselGeslachtsnaam")
    @Expose
    public String voorvoegselGeslachtsnaam;

    @SerializedName("voorletters")
    @Expose
    public String voorletters;

    @SerializedName("voornamen")
    @Expose
    public String voornamen;

    @SerializedName("geslachtsaanduiding")
    @Expose
    public String geslachtsaanduiding;

    @SerializedName("geboortedatum")
    @Expose
    public String geboortedatum;

    @SerializedName("verblijfsadres")
    @Expose
    public ZgwAdres verblijfsadres;

    @SerializedName("sub")
    @Expose
    public String sub;

    @SerializedName("isGehuisvestIn")
    @Expose
    public String isGehuisvestIn;

    @SerializedName("naam")
    @Expose
    public String naam;
}
