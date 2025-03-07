package jsonparser.util;

import com.godwin.jsonparser.util.JsonUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonUtilsTest {

    @Test
    public void testValidJson_remainsUnchanged() {
        String json = "{\"first_Name\": \"John\", \"last_Name\": \"Doe\",\"Age\": 20}";
        assertEquals(json, JsonUtils.cleanUpJsonString(json));
    }

    @Test
    public void testValidJson_containsBraceInElement_remainsUnchanged() {
        String json = "{\"first_Name\": \"Joh}}n\", \"last{_Name\": \"Doe\",\"Age\": 20}";
        assertEquals(json, JsonUtils.cleanUpJsonString(json));
    }

    @Test
    public void testMissingBothBraces() {
        String json = "\"first_Name\": \"John\", \"last_Name\": \"Doe\",\"Age\": 20";
        String expected = "{\"first_Name\": \"John\", \"last_Name\": \"Doe\",\"Age\": 20}";
        assertEquals(expected, JsonUtils.cleanUpJsonString(json));
    }

    @Test
    public void testMissingBothBraces_containsBraceInElement() {
        String json = "\"first_N{ame\": \"John\", \"last_N{{ame\": \"Do}e\",\"Age\": 20";
        String expected = "{\"first_N{ame\": \"John\", \"last_N{{ame\": \"Do}e\",\"Age\": 20}";
        assertEquals(expected, JsonUtils.cleanUpJsonString(json));
    }

    //    A Brace is present at the closing, but it's not the closing brace
    @Test
    public void testMissingBothBraces_where_aClosingBraceIsPresent() {
        String json = "\"first_Name\":\"John\",\"last_Name\":\"Doe\",\"Age\":20,\"department\":{\"name\":\"sales\"}";
        String expected = "{\"first_Name\":\"John\",\"last_Name\":\"Doe\",\"Age\":20,\"department\":{\"name\":\"sales\"}}";
        assertEquals(expected, JsonUtils.cleanUpJsonString(json));
    }

    //    A Brace is present at the closing, but it's not the closing brace
    @Test
    public void testMissingBothBraces_containsBraceInElement_where_aClosingBraceIsPresent() {
        String json = "\"first_Name\":\"John\",\"last_Name\":\"Doe}}\",\"Age{\":20,\"department\":{\"name\":\"sales\"}";
        String expected = "{\"first_Name\":\"John\",\"last_Name\":\"Doe}}\",\"Age{\":20,\"department\":{\"name\":\"sales\"}}";
        assertEquals(expected, JsonUtils.cleanUpJsonString(json));
    }

}
