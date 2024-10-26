package com.godwin.jsonparser.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonUtils {

    private JsonUtils() {
    }

    public static String formatJson(String jsonStr) throws JsonProcessingException {
        ObjectMapper mapper = Holder.MAPPER;
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS,true);
        mapper.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS,true);
        Object jsonObject = mapper.readValue(jsonStr, Object.class);
        return Holder.MAPPER.writer(Holder.DEFAULT_PRETTY_PRINTER).writeValueAsString(jsonObject);
    }

    public static Map<String, Object> getMap(String jsonStr) throws JsonProcessingException {
        return Holder.MAPPER.readValue(jsonStr, new TypeReference<>() {
        });
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