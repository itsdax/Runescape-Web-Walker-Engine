package scripts.dax_api.api_lib.models;

import com.allatori.annotations.DoNotRename;
import scripts.dax_api.api_lib.json.JsonArray;
import scripts.dax_api.api_lib.json.JsonObject;

import java.util.Collection;
import java.util.Objects;

@DoNotRename
public class IntPair {


    @DoNotRename
    private int key;

    @DoNotRename
    private int value;

    public IntPair() {
    }

    public IntPair(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return this.key;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntPair intPair = (IntPair) o;
        return key == intPair.key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public static JsonArray toJsonArray(Collection<IntPair> intPairCollection) {
        JsonArray jsonValues = new JsonArray();
        for (IntPair intPair : intPairCollection) {
            jsonValues.add(new JsonObject().add(intPair.getKey() + " ", intPair.getValue()));
        }
        return jsonValues;
    }
}
