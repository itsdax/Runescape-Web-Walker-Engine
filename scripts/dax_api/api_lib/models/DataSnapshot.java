package scripts.dax_api.api_lib.models;


import com.allatori.annotations.DoNotRename;

@DoNotRename
public class DataSnapshot {

    @DoNotRename
    private String timeStamp;

    @DoNotRename
    private double total;

    public DataSnapshot() {
    }

    public DataSnapshot(String timeStamp, double total) {
        this.timeStamp = timeStamp;
        this.total = total;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public double getTotal() {
        return total;
    }
}
