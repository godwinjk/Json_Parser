package com.godwin.jsonparser.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public class JsonUtils {

    private JsonUtils() {
    }

    public static String formatJson(String jsonStr) throws JsonProcessingException {
        ObjectMapper mapper = Holder.MAPPER;
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true);
        Object jsonObject = mapper.readValue(jsonStr, Object.class);
        return Holder.MAPPER.writer(Holder.DEFAULT_PRETTY_PRINTER).writeValueAsString(jsonObject);
    }

    public static Object getMap(String jsonStr) throws JsonProcessingException {
        ObjectMapper mapper = Holder.MAPPER;
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true);

        JsonNode node = mapper.readTree(jsonStr);

        if (node.isObject()) {
            // If it's a JSON object, map to a Map
            return mapper.convertValue(node, Map.class);
        } else if (node.isArray()) {
            // If it's a JSON array, map to a List of Maps
            return mapper.convertValue(node, List.class);
        } else {
            // Handle unexpected cases (e.g., primitive values)
            throw new IllegalArgumentException("Unsupported JSON structure");
        }
    }

    public static Object parseJson(String json, ObjectMapper objectMapper) throws Exception {
        // Parse the JSON into a JsonNode
        JsonNode node = objectMapper.readTree(json);

        if (node.isObject()) {
            // If it's a JSON object, map to a Map
            return objectMapper.convertValue(node, Map.class);
        } else if (node.isArray()) {
            // If it's a JSON array, map to a List of Maps
            return objectMapper.convertValue(node, List.class);
        } else {
            // Handle unexpected cases (e.g., primitive values)
            throw new IllegalArgumentException("Unsupported JSON structure");
        }
    }

    /**
     * Cleans up the JSON string by applying various cleanup methods.
     * For now, it only ensures missing opening and closing braces are fixed.
     * Other future methods that can be added are:
     * 1. Missing only opening brace
     * 2. Missing only closing brace
     * 2. Missing comma
     * 3. missing '"'
     */
    public static String cleanUpJsonString(String jsonString) {
        jsonString = jsonString.trim();

        // If it's already valid, return as is.
        if (isValidJson(jsonString)) {
            return jsonString;
        }

        //attempt to fix json

        // Check if the start or end braces is missing
        if (!jsonString.startsWith("{") || !jsonString.endsWith("}")) {
            String fixedJsonString = fixMissingBraces(jsonString);

            // Validate after adding missing braces
            if (isValidJson(fixedJsonString)) {
                return fixedJsonString;
            }
        }

        //Return the JSON in the original form
        return jsonString;
    }

    /**
     * Checks if the given JSON string is valid.
     * @param jsonString The JSON string to validate.
     * @return true if the JSON is valid, false otherwise.
     */
    private static boolean isValidJson(String jsonString) {
        try (JsonParser parser = new JsonFactory().createParser(jsonString)) {
            //noinspection StatementWithEmptyBody
            while (parser.nextToken() != null) {
                // If we can iterate through tokens then it's valid JSON
            }
            return true;
        } catch (Exception e) {
            return false; // Invalid JSON
        }
    }

    /**
     * Fixes missing opening and closing braces in the JSON string.
     * @param jsonString The JSON string to fix.
     * @return A properly wrapped JSON string.
     */
    private static String fixMissingBraces(String jsonString) {
        jsonString = jsonString.trim();

        int openBracesCount = 0;
        int closeBracesCount = 0;

        // Count `{` and `}` braces
        for (char ch : jsonString.toCharArray()) {
            if (ch == '{') openBracesCount++;
            if (ch == '}') closeBracesCount++;
        }

        if (openBracesCount == closeBracesCount && !jsonString.startsWith("{")) {
            jsonString = "{" + jsonString + "}";
        }

        return jsonString;
    }

/*    public static String minifyJson(String jsonStr) throws JsonProcessingException {
        Object jsonObject = Holder.MAPPER.readValue(jsonStr, Object.class);
        return Holder.MAPPER.writeValueAsString(jsonObject);
    }*/

/*    public static void verifyJson(String jsonStr) throws JsonProcessingException {
        Holder.MAPPER.readValue(jsonStr, Object.class);
    }*/

    private static final class Holder {
        public static final ObjectMapper MAPPER = new CustomMapper();
        public static final DefaultPrettyPrinter DEFAULT_PRETTY_PRINTER = new CustomPrettyPrinter();
    }

    private static final class CustomPrettyPrinter extends DefaultPrettyPrinter {

        private static final DefaultIndenter UNIX_LINE_FEED_INSTANCE = new DefaultIndenter("  ", "\n");

        public CustomPrettyPrinter() {
            super._objectFieldValueSeparatorWithSpaces = ":";
            super._objectIndenter = UNIX_LINE_FEED_INSTANCE;
            super._arrayIndenter = UNIX_LINE_FEED_INSTANCE;
            super._spacesInObjectEntries = true;
        }

        @Override
        public DefaultPrettyPrinter createInstance() {
            return new CustomPrettyPrinter();
        }
    }

    public static final class CustomMapper extends ObjectMapper {
        public CustomMapper() {
            configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
            configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    }
}