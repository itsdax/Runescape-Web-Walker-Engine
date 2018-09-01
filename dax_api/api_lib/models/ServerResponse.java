package scripts.dax_api.api_lib.models;

import com.allatori.annotations.DoNotRename;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import scripts.dax_api.api_lib.json.JsonValue;

@DoNotRename
public class ServerResponse {
    private boolean success;
    private int code;
    private String contents;

    public ServerResponse(boolean success, int code, String contents) {
        this.success = success;
        this.code = code;
        this.contents = contents;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCode() {
        return code;
    }

    public String getContents() {
        return contents;
    }

    public String getErrorMessage() {
        JsonElement jsonElement = new JsonParser().parse(getContents());
        if (jsonElement.isJsonNull() || (jsonElement = jsonElement.getAsJsonObject().get("message")).isJsonNull()) {
            return "No error message...";
        }
        return jsonElement.getAsString();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
