package com.godwin.jsonparser.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    private JsonUtils() {}

    public static String formatJson(String jsonStr) throws JsonProcessingException {
        Object jsonObject = Holder.MAPPER.readValue(jsonStr, Object.class);
        return Holder.MAPPER.writer(Holder.DEFAULT_PRETTY_PRINTER).writeValueAsString(jsonObject);
    }

    public static String minifyJson(String jsonStr) throws JsonProcessingException {
        Object jsonObject = Holder.MAPPER.readValue(jsonStr, Object.class);
        return Holder.MAPPER.writeValueAsString(jsonObject);
    }

    public static void verifyJson(String jsonStr) throws JsonProcessingException {
        Holder.MAPPER.readValue(jsonStr, Object.class);
    }

    private static final class Holder {
        public static final ObjectMapper MAPPER = new ObjectMapper();
        public static final DefaultPrettyPrinter DEFAULT_PRETTY_PRINTER = new CustomPrettyPrinter();
    }

    private static final class CustomPrettyPrinter extends DefaultPrettyPrinter {

        private static final DefaultIndenter UNIX_LINE_FEED_INSTANCE = new DefaultIndenter("  ", "\n");

        public CustomPrettyPrinter() {
            super._objectFieldValueSeparatorWithSpaces = ": ";
            super._objectIndenter = UNIX_LINE_FEED_INSTANCE;
            super._arrayIndenter = UNIX_LINE_FEED_INSTANCE;
        }

        @Override
        public DefaultPrettyPrinter createInstance() {
            return new CustomPrettyPrinter();
        }
    }
}