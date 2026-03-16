
import edu.eci.tdse.server.QueryStringParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QueryStringParserTest {

    @Test
    void parsesSimpleParam() {
        Map<String, List<String>> result = QueryStringParser.parse("name=John");
        assertEquals(List.of("John"), result.get("name"));
    }

    @Test
    void parsesMultipleParams() {
        Map<String, List<String>> result = QueryStringParser.parse("name=John&age=30");
        assertEquals("John", result.get("name").get(0));
        assertEquals("30", result.get("age").get(0));
    }

    @Test
    void returnsEmptyMapForNull() {
        assertTrue(QueryStringParser.parse(null).isEmpty());
    }

    @Test
    void returnsEmptyMapForBlank() {
        assertTrue(QueryStringParser.parse("").isEmpty());
    }

    @Test
    void handlesEncodedValues() {
        Map<String, List<String>> result = QueryStringParser.parse("name=Hello+World");
        assertEquals("Hello World", result.get("name").get(0));
    }

    @Test
    void handlesMissingValue() {
        Map<String, List<String>> result = QueryStringParser.parse("flag");
        assertEquals("", result.get("flag").get(0));
    }
}