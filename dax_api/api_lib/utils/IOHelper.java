package scripts.dax_api.api_lib.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class IOHelper {

    public static String readInputStream(InputStream inputStream) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return bufferedReader.lines().collect(Collectors.joining());
        } catch (IOException e) {
            return null;
        }
    }
}
