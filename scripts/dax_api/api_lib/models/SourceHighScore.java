package scripts.dax_api.api_lib.models;


import com.allatori.annotations.DoNotRename;
import com.google.gson.Gson;

import java.util.List;

@DoNotRename
public class SourceHighScore {

    @DoNotRename
    private String propertyName;

    @DoNotRename
    private Period period;

    @DoNotRename
    private List<String> sources;

    public String getPropertyName() {
        return propertyName;
    }

    public Period getPeriod() {
        return period;
    }

    public List<String> getSources() {
        return sources;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
