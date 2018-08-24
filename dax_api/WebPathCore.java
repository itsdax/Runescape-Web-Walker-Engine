package scripts.dax_api;

import org.tribot.api2007.types.RSTile;
import scripts.dax_api.shared.GetPathResponseContainer;
import scripts.dax_api.shared.PlayerInformation;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;


class WebPathCore {

    private static final String HOST = "webwalker.dax.cloud", TEST = "localhost:8080", DIRECTORY = "/web?";
    private static ArrayList<RSTile> lastCalledPath = null;
    private static HashMap<String, GetPathResponseContainer> cache = null;

    private static String apiKey = "dc9bce99-f696-4f57-8e9a-50c75d6695f3", secretKey = "46F7149B15027D0C";

    private static boolean local = false;

    public static void setLocal(boolean b){
        local = b;
    }

    private static String getServerURL(){
        return "http://" + (local ? TEST : HOST);
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
        try {
            urlSafeParams += ("&apiKey=" + URLEncoder.encode(apiKey, "UTF-8") + "&token=" + URLEncoder.encode(generateAuthToken(), "UTF-8"));
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return GetPathResponseContainer.CLIENT_ERROR;
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

    private static String generateAuthToken(){
//        return encrypt(apiKey, secretKey, pingTime() + "");
        return encrypt(apiKey, secretKey, System.currentTimeMillis() + "");
    }

    public static long pingTime(){
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(getServerURL() + "/timing").openStream()))){
            String time = bufferedReader.readLine();
            System.out.println("Ping time result: " + time);
            return Long.parseLong(time);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static void setAuth(String apiKey, String secretKey){
        WebPathCore.apiKey = apiKey;
        WebPathCore.secretKey = secretKey;
    }

    private static String encrypt(String key, String initVector, String value) {
        key = key.substring(0, 16);
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return DatatypeConverter.printBase64Binary(cipher.doFinal(value.getBytes()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ArrayList<RSTile> previousPath(){
        return lastCalledPath;
    }


}
