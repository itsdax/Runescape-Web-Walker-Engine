package scripts.dax_api.api_lib;

import org.tribot.api.General;
import scripts.dax_api.api_lib.json.Json;
import scripts.dax_api.api_lib.json.JsonObject;
import scripts.dax_api.api_lib.json.JsonParser;
import scripts.dax_api.api_lib.json.ParseException;
import scripts.dax_api.api_lib.models.*;
import scripts.dax_api.api_lib.utils.IOHelper;
import scripts.dax_api.walker_engine.Loggable;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class WebWalkerServerApi implements Loggable {

    private static WebWalkerServerApi webWalkerServerApi;

    public static WebWalkerServerApi getInstance() {
        return webWalkerServerApi != null ? webWalkerServerApi : (webWalkerServerApi = new WebWalkerServerApi());
    }

    private static final String WALKER_ENDPOINT = "https://api.dax.cloud", TEST_ENDPOINT = "http://localhost:8080";

    private static final String
            GENERATE_PATH = "/walker/generatePath",
            GENERATE_BANK_PATH = "/walker/generateBankPath";


    private DaxCredentialsProvider daxCredentialsProvider;
    private HashMap<String, String> cache;
    private boolean isTestMode;

    private WebWalkerServerApi() {
        cache = new HashMap<>();
    }

    public DaxCredentialsProvider getDaxCredentialsProvider() {
        return daxCredentialsProvider;
    }

    public void setDaxCredentialsProvider(DaxCredentialsProvider daxCredentialsProvider) {
        this.daxCredentialsProvider = daxCredentialsProvider;
    }

    public PathResult getPath(Point3D start, Point3D end, PlayerDetails playerDetails) {
        JsonObject pathRequest = new JsonObject()
                .add("start", start.toJson())
                .add("end", end.toJson());

        if (playerDetails != null) {
            pathRequest.add("player", playerDetails.toJson());
        }

        try {
            return parseResult(post(pathRequest, (isTestMode ? TEST_ENDPOINT : WALKER_ENDPOINT) + GENERATE_PATH));
        } catch (IOException e) {
            return new PathResult(PathStatus.NO_RESPONSE_FROM_SERVER);
        }

    }

    public PathResult getBankPath(Point3D start, Bank bank, PlayerDetails playerDetails) {
        JsonObject pathRequest = new JsonObject().add("start", start.toJson());

        if (bank != null) {
            pathRequest.add("bank", bank.toString());
        }

        if (playerDetails != null) {
            pathRequest.add("player", playerDetails.toJson());
        }

        try {
            return parseResult(post(pathRequest, (isTestMode ? TEST_ENDPOINT : WALKER_ENDPOINT) + GENERATE_BANK_PATH));
        } catch (IOException e) {
            return new PathResult(PathStatus.NO_RESPONSE_FROM_SERVER);
        }
    }

    public boolean isTestMode() {
        return isTestMode;
    }

    public void setTestMode(boolean testMode) {
        isTestMode = testMode;
    }

    private PathResult parseResult(ServerResponse serverResponse) {
        if (!serverResponse.isSuccess()) {
            General.println("[Error] " + Json.parse(serverResponse.getContents()).asObject().getString(
                    "message",
                    "Could not generate path: " + serverResponse.getContents()
            ));
            switch (serverResponse.getCode()) {
                case 429:
                    return new PathResult(PathStatus.RATE_LIMIT_EXCEEDED);
                case 400:
                case 401:
                case 404:
                    return new PathResult(PathStatus.INVALID_CREDENTIALS);
            }
        }

        PathResult pathResult;
        JsonObject jsonObject;
        try {
            jsonObject = Json.parse(serverResponse.getContents()).asObject();
        } catch (ParseException e) {
            pathResult = new PathResult(PathStatus.UNKNOWN);
            log("Error: " + pathResult.getPathStatus());
            return pathResult;
        }

        pathResult = PathResult.fromJson(jsonObject);
        log("Response: " + pathResult.getPathStatus()  + " Cost: " + pathResult.getCost());
        return pathResult;
    }

    private ServerResponse post(JsonObject jsonObject, String endpoint) throws IOException {
        getInstance().log("Generating path: " + jsonObject.toString());
        if (cache.containsKey(jsonObject.toString())) {
            return new ServerResponse(true, HttpURLConnection.HTTP_OK, cache.get(jsonObject.toString()));
        }

        URL myurl = new URL(endpoint);
        HttpURLConnection connection = (isTestMode ? (HttpURLConnection) myurl.openConnection() : (HttpsURLConnection) myurl.openConnection());
        connection.setDoOutput(true);
        connection.setDoInput(true);

        connection.setRequestProperty("Method", "POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        appendAuth(connection);

        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.write(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            return new ServerResponse(false, connection.getResponseCode(), IOHelper.readInputStream(connection.getErrorStream()));
        }

        String contents = IOHelper.readInputStream(connection.getInputStream());
        cache.put(jsonObject.toString(), contents);
        return new ServerResponse(true, HttpURLConnection.HTTP_OK, contents);
    }

    private void appendAuth(HttpURLConnection connection) {
        if (daxCredentialsProvider != null && daxCredentialsProvider.getDaxCredentials() != null) {
            DaxCredentials daxCredentials = daxCredentialsProvider.getDaxCredentials();
            connection.setRequestProperty("key", daxCredentials.getApiKey());
            connection.setRequestProperty("secret", daxCredentials.getSecretKey());
        }
    }

    @Override
    public String getName() {
        return "DaxWalker";
    }

    private class ServerResponse {
        private boolean success;
        private int code;
        private String contents;

        private ServerResponse(boolean success, int code, String contents) {
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
    }

}
