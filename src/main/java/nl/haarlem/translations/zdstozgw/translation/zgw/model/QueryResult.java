package nl.haarlem.translations.zdstozgw.translation.zgw.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class QueryResult<T> {

    @SerializedName("count")
    @Expose
    public Integer count;
    @SerializedName("next")
    @Expose
    public Object next;
    @SerializedName("previous")
    @Expose
    public Object previous;
    @SerializedName("results")
    @Expose
    public List<T> results = null;

}
