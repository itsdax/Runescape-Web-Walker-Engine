package scripts.dax_api.api_lib.models;


import com.allatori.annotations.DoNotRename;

@DoNotRename
public class DataLogRequest {

    @DoNotRename
    private String user;

    @DoNotRename
    private String source;

    @DoNotRename
    private String propertyName;

    @DoNotRename
    private double value;

    public DataLogRequest(String user, String source, String propertyName, double value) {
        this.user = user;
        this.source = source;
        this.propertyName = propertyName;
        this.value = value;
    }

    public String getUser() {
        return user;
    }

    public String getSource() {
        return source;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public double getValue() {
        return value;
    }
}
