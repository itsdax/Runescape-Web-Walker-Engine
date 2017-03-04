package scripts.webwalker_logic;

import org.tribot.api2007.types.RSTile;
import scripts.webwalker_logic.shared.GetPathResponseContainer;
import scripts.webwalker_logic.shared.PlayerInformation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;


class WebPathCore {

    private static final String HOST = "173.208.130.82", TEST = "localhost", PORT = "8080", DIRECTORY = "/web?";
    private static ArrayList<RSTile> lastCalledPath = null;
    private static HashMap<String, GetPathResponseContainer> cache = null;

    private static boolean local = false;

    public static void setLocal(boolean b){
        local = b;
    }

    private static String getServerURL(){
        return "http://" + (local ? TEST : HOST) + ":" + PORT;
    }

    static GetPathResponseContainer getPath(int x1, int y1, int z1, int x2, int y2, int z2, PlayerInformation playerInformation){
        try {
            String urlSafeParams = "x1=" + x1 + "&y1=" + y1 + "&z1=" + z1 + "&x2=" + x2 + "&y2=" + y2 + "&z2=" + z2;
            if (playerInformation != null){
                urlSafeParams += "&playerInfo=" + URLEncoder.encode(playerInformation.toString(), "UTF-8");
            }
            return getResponseAndParse(urlSafeParams);
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }
    }

    static GetPathResponseContainer getPathToBank(int x, int y, int z, PlayerInformation playerInformation){
        try {
            String urlSafeParams = "x1=" + x + "&y1=" + y + "&z1=" + z + "&bank=true";
            if (playerInformation != null){
                urlSafeParams += "&playerInfo=" + URLEncoder.encode(playerInformation.toString(), "UTF-8");
            }
            return getResponseAndParse(urlSafeParams);
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }
    }

    private static GetPathResponseContainer getResponseAndParse(String urlSafeParams){
        if (cache == null){
            cache = new HashMap<>();
        }
        GetPathResponseContainer cached = cache.get(urlSafeParams);
        if (cached != null){
            return cached;
        }

        String response = getResponse(DIRECTORY + urlSafeParams);
        GetPathResponseContainer getPathResponseContainer = parseResponse(response);
        if (getPathResponseContainer.getPath().size() > 0){
            cache.put(urlSafeParams, getPathResponseContainer);
        }
        return getPathResponseContainer;
    }

    private static GetPathResponseContainer parseResponse(String response){
        GetPathResponseContainer responseContainer = GetPathResponseContainer.fromJSONString(response);
        System.out.println("Response: " + responseContainer.getStatus() + " [" + responseContainer.getResponse() + "]");
        lastCalledPath = responseContainer.getRSTilePath();
        return responseContainer;
    }

    private static String getResponse(String input){
        try {
            String request = getServerURL() + input;
            URL url = new URL(request);
            System.out.println("GET: " + url.toString());
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(20000);
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                return bufferedReader.lines().collect(Collectors.joining());
            }
        } catch (ConnectException ignored){
            //server offline or unable to connect
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<RSTile> previousPath(){
        return lastCalledPath;
    }


}
