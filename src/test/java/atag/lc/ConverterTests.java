package atag.lc;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConverterTests {

    @Test
    void validateSimpleDocument() throws IOException {
        Converter cut = new Converter();
        String text = Files.readString(Paths.get(getClass().getClassLoader().getResource("New.txt").getPath()));
        cut.convert(text);
    }
}
