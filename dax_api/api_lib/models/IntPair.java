package scripts.dax_api.api_lib.models;

import org.tribot.api.General;
import scripts.dax_api.api_lib.json.JsonArray;
import scripts.dax_api.api_lib.json.JsonObject;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class IntPair {
    private int key;
    private int value;

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

    public static JsonArray toJsonArray(Collection<IntPair> intPairCollection) {
        JsonArray jsonValues = new JsonArray();
        for (IntPair intPair : intPairCollection) {
            jsonValues.add(new JsonObject().add(intPair.getKey() + " ", intPair.getValue()));
        }
        return jsonValues;
    }
}
