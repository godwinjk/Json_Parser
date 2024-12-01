package com.godwin.jsonparser.util;

import com.godwin.jsonparser.common.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class JsonDownloader {
    public static String getData(String inputString) {
        String jsonContent = "";
        try {
            // Create a URL object from the string URL
            Map<String, String> map = getUrlWihHeaders(inputString);
            String urlContent = new URL(map.get("url_god")).toString().replace("\r\n", "\n");
            URL downloadUrl = new URL(urlContent);
            HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
            connection.setRequestMethod("GET"); // Set the request method to GET
            connection.setConnectTimeout(5000); // Set connection timeout (in ms)
            connection.setReadTimeout(5000); // Set read timeout (in ms)

            if (map.size() > 1) {
                map.forEach((key, value) -> {
                    Logger.i("Key: " + key + ", Value: " + value);
                    if (!key.equals("url_god")) {
                        connection.addRequestProperty(key, value);
                    }
                });
            }

            // Get the input stream of the connection
            try (InputStream inputStream = connection.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                // Read the content and build the string
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n"); // Append the lines to the StringBuilder
                }

                jsonContent = response.toString();

            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.e(e.toString());
        }
        return jsonContent;
    }

    static Map<String, String> getUrlWihHeaders(String input) {
        String[] lines = input.split("\n");
        Map<String, String> map = new HashMap<>();
        map.putIfAbsent("url_god", lines[0]);
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];

            // Split the line by colon
            String[] parts = line.split(":");

            // Ensure there's a key and value before accessing them
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();

                // Print the key-value pair
                map.putIfAbsent(key, value);
            } else {
                Logger.i("Invalid format in line: " + line);
            }
        }
        return map;
    }
}
