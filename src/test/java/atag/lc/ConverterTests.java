package atag.lc;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConverterTests {

    @Test
    void validateSimpleDocument() throws IOException {
        Converter cut = new Converter();
        String text = Files.readString(Paths.get(getClass().getClassLoader().getResource("New.txt").getPath()));
        cut.convert(text);
    }

    void antlrTest(String lcText, String atagText, int numberOfAnnotations, AnnotationPositionRecord... annotationRecords) {
        try {
//        InputStream is = getClass().getClassLoader().getResourceAsStream("New.txt");
            InputStream is = new ByteArrayInputStream(lcText.getBytes());
            LeidenConventionLexer lexer = new LeidenConventionLexer(CharStreams.fromStream(is));

            LeidenConventionParser parser = new LeidenConventionParser(new CommonTokenStream(lexer));
            LeidenConventionParser.DocContext context = parser.doc();

            AtagVisitor visitor = new AtagVisitor();

            CharactersAndAnnotations result = visitor.visit(context);
            assertEquals(atagText, result.asString());
            assertThat(result.annotations())
                    .hasSize(numberOfAnnotations)
                    .containsOnly(Arrays.stream(annotationRecords).map(annotationPositionRecord -> annotationPositionRecord.toAnnotationRecord(result.text())).toArray(AnnotationRecord[]::new));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void noAnnotation() {
        antlrTest("abc", "abc", 0);
    }

    @Test
    void simpleAnnotation() {
        antlrTest("[ru[abc]ru]", "abc", 1,
                new AnnotationPositionRecord(null, "rubricated", "abc", 0, 2)
        );
    }

    @Test
    void addition() {
        antlrTest("abc[rm[def]rm] ijk", "abcdef ijk", 1,
                new AnnotationPositionRecord(null, "addition", "def", 3, 5)
        );
    }

    @Test
    @Disabled("overlapping is not allowed")
    void overlapping() {
        antlrTest("abc [ru[def[rm[ijk]ru]]rm]", "abc ", 0);
    }

    @Test
    void nested() {
        antlrTest("[$[[t[[ru[It(em) ad Eundem]ru]]t]]$]", "Item ad Eundem", 3,
                new AnnotationPositionRecord(null, "expansion", "em", 2, 3),
                new AnnotationPositionRecord(null, "head", "Item ad Eundem", 0, 13),
                new AnnotationPositionRecord(null, "rubricated", "Item ad Eundem", 0, 13)
        );
    }

    @Test
    void realTextHildegard() throws IOException {
        String text = new String(getClass().getClassLoader().getResourceAsStream("Hildegard/fixed/ok/Wr044-59va.txt").readAllBytes(), StandardCharsets.UTF_8);
        antlrTest(text, "abc", 0);
    }
}
