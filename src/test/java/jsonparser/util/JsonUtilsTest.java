package jsonparser.util;

import com.godwin.jsonparser.util.JsonUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonUtilsTest {

    @Test
    public void testValidJsonRemainsUnchanged() {
        String json = "{\"first_Name\": \"John\", \"last_Name\": \"Doe\",\"Age\": 20}";
        assertEquals(json, JsonUtils.cleanUpJsonString(json));
    }

    @Test
    public void testMissingBothBraces() {
        String json = "\"first_Name\": \"John\", \"last_Name\": \"Doe\",\"Age\": 20";
        String expected = "{\"first_Name\": \"John\", \"last_Name\": \"Doe\",\"Age\": 20}";
        assertEquals(expected, JsonUtils.cleanUpJsonString(json));
    }

    //    A Brace is present at the closing, but it's not the closing brace
    @Test
    public void testMissingBothBraces_where_aClosingBraceIsPresent() {
        String json = "\"first_Name\":\"John\",\"last_Name\":\"Doe\",\"Age\":20,\"department\":{\"name\":\"sales\"}";
        String expected = "{\"first_Name\":\"John\",\"last_Name\":\"Doe\",\"Age\":20,\"department\":{\"name\":\"sales\"}}";
        assertEquals(expected, JsonUtils.cleanUpJsonString(json));
    }

}
