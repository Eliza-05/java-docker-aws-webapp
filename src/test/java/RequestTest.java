import edu.eci.tdse.server.HttpRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {

    @Test
    void getParamReturnsCorrectValue() {
        HttpRequest req = new HttpRequest("GET", "/hello",
                Map.of("name", List.of("Eliza")),
                Map.of(), "");
        assertEquals("Eliza", req.getParam("name"));
    }

    @Test
    void getParamReturnsNullWhenMissing() {
        HttpRequest req = new HttpRequest("GET", "/hello",
                Map.of(), Map.of(), "");
        assertNull(req.getParam("name"));
    }

    @Test
    void getParamReturnsFirstValueWhenMultiple() {
        HttpRequest req = new HttpRequest("GET", "/hello",
                Map.of("name", List.of("Eliza", "Juan")),
                Map.of(), "");
        assertEquals("Eliza", req.getParam("name"));
    }

    @Test
    void methodAndPathAreCorrect() {
        HttpRequest req = new HttpRequest("GET", "/pi",
                Map.of(), Map.of(), "");
        assertEquals("GET", req.method());
        assertEquals("/pi", req.path());
    }

    @Test
    void bodyIsCorrect() {
        HttpRequest req = new HttpRequest("POST", "/data",
                Map.of(), Map.of(), "hello body");
        assertEquals("hello body", req.body());
    }
}