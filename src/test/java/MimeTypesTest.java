import edu.eci.tdse.server.MimeTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MimeTypesTest {

    @Test
    void htmlFile() { assertEquals("text/html", MimeTypes.fromFilename("index.html")); }

    @Test
    void pngFile() { assertEquals("image/png", MimeTypes.fromFilename("photo.png")); }

    @Test
    void cssFile() { assertEquals("text/css", MimeTypes.fromFilename("style.css")); }

    @Test
    void jsFile() { assertEquals("application/javascript", MimeTypes.fromFilename("app.js")); }

    @Test
    void unknownExtension() { assertEquals("application/octet-stream", MimeTypes.fromFilename("data.xyz")); }

    @Test
    void nullFilename() { assertEquals("application/octet-stream", MimeTypes.fromFilename(null)); }
}