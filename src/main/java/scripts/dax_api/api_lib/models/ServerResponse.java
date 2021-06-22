package scripts.dax_api.api_lib.models;

import com.allatori.annotations.DoNotRename;
import com.google.gson.Gson;

@DoNotRename
public class ServerResponse {

    @DoNotRename
    private boolean success;

    @DoNotRename
    private int code;

    @DoNotRename
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

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
