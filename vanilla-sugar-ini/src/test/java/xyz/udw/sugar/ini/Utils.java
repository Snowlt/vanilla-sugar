package xyz.udw.sugar.ini;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Utils {

    static InputStream getInputStream(String name) {
        InputStream inputStream = Utils.class.getResourceAsStream("/" + name);
        assertNotNull(inputStream);
        return inputStream;
    }

    @SafeVarargs
    static <T> List<T> asList(T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

    static String loadAsString(String name) throws IOException {
        InputStream inputStream = getInputStream(name);
        try (
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader)
        ) {
            return bufferedReader.lines().collect(Collectors.joining("\n"));
        }
    }
}
