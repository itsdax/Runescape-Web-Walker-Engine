package scripts.dax_api.api_lib;

import org.tribot.api.General;
import scripts.dax_api.api_lib.json.Json;
import scripts.dax_api.api_lib.json.JsonObject;
import scripts.dax_api.api_lib.json.JsonParser;
import scripts.dax_api.api_lib.json.ParseException;
import scripts.dax_api.api_lib.models.*;
import scripts.dax_api.api_lib.utils.IOHelper;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebWalkerServerApi {

    private static WebWalkerServerApi webWalkerServerApi;

    public static WebWalkerServerApi getInstance() {
        return webWalkerServerApi != null ? webWalkerServerApi : (webWalkerServerApi = new WebWalkerServerApi());
    }

    private static final String WALKER_ENDPOINT = "https://api.dax.cloud";

    private static final String
            GENERATE_PATH = "/walker/generatePath",
            GENERATE_BANK_PATH = "/walker/generateBankPath";


    private DaxCredentialsProvider daxCredentialsProvider;

    private WebWalkerServerApi() {

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
            return parseResult(post(pathRequest, WALKER_ENDPOINT + GENERATE_PATH));
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
            return parseResult(post(pathRequest, WALKER_ENDPOINT + GENERATE_BANK_PATH));
        } catch (IOException e) {
            return new PathResult(PathStatus.NO_RESPONSE_FROM_SERVER);
        }
    }

    private PathResult parseResult(ServerResponse serverResponse) {
        if (serverResponse.getCode() != 200) {
            General.println("[Error] " + Json.parse(serverResponse.getContents()).asObject().getString(
                    "message",
                    "Could not generate path: " + serverResponse.getContents()
            ));
        }
        if (!serverResponse.isSuccess()) {
            switch (serverResponse.getCode()) {
                case 429:
                    return new PathResult(PathStatus.RATE_LIMIT_EXCEEDED);
                case 400:
                case 401:
                case 404:
                    return new PathResult(PathStatus.INVALID_CREDENTIALS);
            }
        }

        JsonObject jsonObject;
        try {
            jsonObject = Json.parse(serverResponse.getContents()).asObject();
        } catch (ParseException e) {
            return new PathResult(PathStatus.UNKNOWN);
        }

        return PathResult.fromJson(jsonObject);
    }

    private ServerResponse post(JsonObject jsonObject, String endpoint) throws IOException {
        URL myurl = new URL(endpoint);
        HttpsURLConnection connection = (HttpsURLConnection) myurl.openConnection();
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

        return new ServerResponse(true, HttpURLConnection.HTTP_OK, IOHelper.readInputStream(connection.getInputStream()));
    }

    private void appendAuth(HttpsURLConnection connection) {
        if (daxCredentialsProvider != null && daxCredentialsProvider.getDaxCredentials() != null) {
            DaxCredentials daxCredentials = daxCredentialsProvider.getDaxCredentials();
            connection.setRequestProperty("key", daxCredentials.getApiKey());
            connection.setRequestProperty("secret", daxCredentials.getSecretKey());
        }
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
