package scripts.dax_api.api_lib.models;


import com.allatori.annotations.DoNotRename;
import com.google.gson.Gson;

import java.util.List;

@DoNotRename
public class PropertyStats {

    @DoNotRename
    private Period period;

    @DoNotRename
    private String propertyName;

    @DoNotRename
    private double total;

    @DoNotRename
    private List<DataSnapshot> dataSnapshots;

    public PropertyStats() {
    }

    public PropertyStats(Period period, String propertyName, double total, List<DataSnapshot> dataSnapshots) {
        this.period = period;
        this.propertyName = propertyName;
        this.total = total;
        this.dataSnapshots = dataSnapshots;
    }

    public Period getPeriod() {
        return period;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public double getTotal() {
        return total;
    }

    public List<DataSnapshot> getDataSnapshots() {
        return dataSnapshots;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
