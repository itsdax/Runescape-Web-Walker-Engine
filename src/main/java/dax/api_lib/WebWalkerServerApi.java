package dax.api_lib;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dax.api_lib.json.Json;
import dax.api_lib.json.JsonValue;
import dax.api_lib.json.ParseException;
import dax.api_lib.models.*;
import dax.api_lib.utils.IOHelper;
import dax.walker_engine.Loggable;
import org.tribot.api.ScriptCache;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class WebWalkerServerApi implements Loggable {

    private static final Gson gson = new Gson();

    public static WebWalkerServerApi getInstance() {
        return (WebWalkerServerApi) ScriptCache.get().computeIfAbsent("DaxWalker.WebWalkerServerApi", k -> new WebWalkerServerApi());
    }

    // This was originally https://api.dax.cloud
    private static final String WALKER_ENDPOINT = "https://walker.dax.cloud", TEST_ENDPOINT = "http://localhost:8080";

    private static final String
            GENERATE_PATH = "/walker/generatePath",
            GENERATE_BANK_PATH = "/walker/generateBankPath";


    private DaxCredentialsProvider daxCredentialsProvider;
    private HashMap<String, String> cache;
    private boolean isTestMode;

    private WebWalkerServerApi() {
        cache = new HashMap<>();
    }

    public void setDaxCredentialsProvider(DaxCredentialsProvider daxCredentialsProvider) {
        this.daxCredentialsProvider = daxCredentialsProvider;
    }

    public List<PathResult> getPaths(BulkPathRequest bulkPathRequest) {
        try {
            return parseResults(post(gson.toJson(bulkPathRequest), WALKER_ENDPOINT + "/walker/generatePaths"));
        } catch (IOException e) {
            getInstance().log("Is server down? Spam dax.");
            return Collections.singletonList(new PathResult(PathStatus.NO_RESPONSE_FROM_SERVER));
        }
    }

    public List<PathResult> getBankPaths(BulkBankPathRequest bulkBankPathRequest) {
        try {
            return parseResults(post(gson.toJson(bulkBankPathRequest), WALKER_ENDPOINT + "/walker/generateBankPaths"));
        } catch (IOException e) {
            getInstance().log("Is server down? Spam dax.");
            return Collections.singletonList(new PathResult(PathStatus.NO_RESPONSE_FROM_SERVER));
        }
    }

    public boolean isTestMode() {
        return isTestMode;
    }

    public void setTestMode(boolean testMode) {
        isTestMode = testMode;
    }

    private List<PathResult> parseResults(ServerResponse serverResponse) {
        if (!serverResponse.isSuccess()) {

            JsonValue jsonValue = null;
            try {
                jsonValue = Json.parse(serverResponse.getContents());
            } catch (Exception | Error e) {
                jsonValue = Json.NULL;
            }
            if (!jsonValue.isNull()) {
                getInstance().log("[Error] " + jsonValue.asObject().getString(
                        "message",
                        "Could not generate path: " + serverResponse.getContents()
                ));
            }

            switch (serverResponse.getCode()) {
                case 429:
                    return Collections.singletonList(new PathResult(PathStatus.RATE_LIMIT_EXCEEDED));
                case 400:
                case 401:
                case 404:
                    return Collections.singletonList(new PathResult(PathStatus.INVALID_CREDENTIALS));
            }

            if (serverResponse.getCode() >= 500) {
                log("Error: " + serverResponse.getCode() + ": " + serverResponse.getContents());
                log("Please send dax the debug. That could be found right after 'Generating path:'");
                return Collections.singletonList(new PathResult(PathStatus.BAD_RESPONSE_FROM_SERVER));
            }

        }

        try {
            return gson.fromJson(serverResponse.getContents(), new TypeToken<List<PathResult>>() {
            }.getType());
        } catch (JsonSyntaxException | ParseException | IllegalStateException e) {
            PathResult pathResult = new PathResult(PathStatus.UNKNOWN);
            log("Error: " + pathResult.getPathStatus());
            return Collections.singletonList(pathResult);
        }
    }

    private ServerResponse post(String json, String endpoint) throws IOException {
        getInstance().log("Generating path: " + json);
        if (cache.containsKey(json)) {
            return new ServerResponse(true, HttpURLConnection.HTTP_OK, cache.get(json.toString()));
        }

        URL myurl = new URL(endpoint);
        HttpURLConnection connection = (isTestMode ? (HttpURLConnection) myurl.openConnection() : (HttpsURLConnection) myurl.openConnection());
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(30000);

        connection.setRequestProperty("Method", "POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        IOHelper.appendAuth(connection, daxCredentialsProvider);

        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.write(json.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            return new ServerResponse(false, connection.getResponseCode(), IOHelper.readInputStream(connection.getErrorStream()));
        }

        try {
            String contents = IOHelper.readInputStream(connection.getInputStream());
            cache.put(json, contents);
            return new ServerResponse(true, HttpURLConnection.HTTP_OK, contents);
        } catch (IOException e) {
            return new ServerResponse(false, connection.getResponseCode(), IOHelper.readInputStream(connection.getErrorStream()));
        }
    }


    @Override
    public String getName() {
        return "DaxWalker";
    }


}
