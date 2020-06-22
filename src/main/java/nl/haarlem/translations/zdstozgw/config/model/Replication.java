package nl.haarlem.translations.zdstozgw.config.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Replication {
    @SerializedName("enableZDS")
    @Expose
    public boolean enableZDS;
    @SerializedName("enableZGW")
    @Expose
    public boolean enableZGW;
    public ResponseType responseType;

    @Override
    public String toString() {
        return "Replication{" +
                "enableZDS=" + enableZDS +
                ", enableZGW=" + enableZGW +
                ", responseType=" + responseType +
                '}';
    }
}
