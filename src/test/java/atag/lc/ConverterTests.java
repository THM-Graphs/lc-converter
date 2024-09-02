package atag.lc;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConverterTests {

    @Test
    void validateSimpleDocument() throws IOException {
        Converter cut = new Converter();
        String text = Files.readString(Paths.get(getClass().getClassLoader().getResource("New.txt").getPath()));
        cut.convert(text);
    }

    @Test
    void antlrTest() throws IOException {

        InputStream is = getClass().getClassLoader().getResourceAsStream("New.txt");
        LeidenConventionLexer lexer = new LeidenConventionLexer(CharStreams.fromStream(is));

        LeidenConventionParser parser = new LeidenConventionParser(new CommonTokenStream(lexer));
        LeidenConventionParser.BlockContext context = parser.block();
//        LeidenConventionVisitor visitor = new LeidenConvetionVisitor();
//        visitor.visit(context);

        System.out.printf("Parsed: %s\n", parser.block().toStringTree());
    }

    //TODO: test für überlappenden Annotationen
}
