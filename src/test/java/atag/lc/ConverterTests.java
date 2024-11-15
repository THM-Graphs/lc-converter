package atag.lc;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.assertj.core.api.ListAssert;
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

    CharactersAndAnnotations antlrTest(String lcText, String atagText, int numberOfAnnotations, AnnotationPositionRecord... annotationRecords) {
//        InputStream is = getClass().getClassLoader().getResourceAsStream("New.txt");
        CharactersAndAnnotations result = getCharactersAndAnnotations(lcText);
        assertEquals(atagText, result.asString());
        ListAssert<AnnotationRecord> annotationRecordListAssert = assertThat(result.annotations())
                .hasSize(numberOfAnnotations);
        if (annotationRecords.length > 0) {
            annotationRecordListAssert
                    .containsOnly(Arrays.stream(annotationRecords).map(annotationPositionRecord -> annotationPositionRecord.toAnnotationRecord(result.text())).toArray(AnnotationRecord[]::new));
        }
        return result;
    }

    private CharactersAndAnnotations getCharactersAndAnnotations(String lcText) {
        try {
            InputStream is = new ByteArrayInputStream(lcText.getBytes());
            LeidenConventionLexer lexer = new LeidenConventionLexer(CharStreams.fromStream(is));
            LeidenConventionParser parser = new LeidenConventionParser(new CommonTokenStream(lexer));
            LeidenConventionParser.DocContext context = parser.doc();

            return new AtagVisitor().visit(context);
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
                new AnnotationPositionRecord(null, "emphasised", "abc", 0, 2)
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
                new AnnotationPositionRecord(null, "emphasised", "Item ad Eundem", 0, 13)
        );
    }

    @Test
    void marginNote() {
        antlrTest("[m[?]m] abc", "? abc", 1,
                new AnnotationPositionRecord(null, "marginNote", "?", 0, 0)
        );
    }

    @Test
    void realTextHildegard() throws IOException {
        String text = new String(getClass().getClassLoader().getResourceAsStream("Hildegard/fixed/ok/Wr044-59va.txt").readAllBytes(), StandardCharsets.UTF_8);
        CharactersAndAnnotations result = antlrTest(text, "  Item ad Eundem *Titel beginnt noch in letzter Zeile des vorherigen Briefs*? O tu qui in persona illa es. que a deo et non  ab hominibus est! quoniam deus omnium  rector. hominibus dat. ut in uice sua  sint. Vnde et tu prouide qualiter in uice christi  sis. In uisione enim uidi quasi solem  nimio ardore suo in lutum plenum uermium fulgere. qui pre gaudio estus se  erigebant. sed tandem feruorem caloris  nequaquam sufferentes se deponendo  absconderent! unde et lutum illud magnum fetorem emisit. Uidi etiam quod in hortum sol fulgebat. in quo rose et lilia  ac omnia genera pigmentorum crescebant!  et ubi per calorem solis flores floruerunt.   et pigmenta in radicibus suis multiplicata  sunt ac dulcissimum odorem dederunt. ita  quod plurimi homines suauissimo odore isto repleti de horto isto quasi de paradyso  gaudium habuerunt. Et audiui uocem  de sursum dicentem tibi. Propone tibi o homo  utrum eligere uelis in predicto horto delitiarum mere. aut in fetente stercore cum uermibus iacere! et utrum altum templum cum turribus bene ornatum. per cuius  fenestras oculi columbarum aspitiuntur.  an paruum habitaculum stramine  obtectum. in quo uix rusticus locum cum suis  habere poterit! salubrius te condeceat.  Lutum uero cum uermibus est prima radix originalis peccati. que  per consilium antiqui  serpentis exorta est. quam natura uirginitatis suffocauit! quando filius dei ex  Maria uirgine natus est. in quo hortus omnium uirtutum surrexit. quem et  episcopales personae imitari debent. Ipsos  etiam excelsum templum per altam doctrinam  episcopalis officij ascendere decet! quemadmodum et columba oculis suis in altum aspicit.  et non secundum oculos accipitis. id est non secundum  mres huius seculi facere debent. qui  uulnera quidem fatiunt. que oleo non unxerunt. Excute etiam te de rusticalibus  moribus auaricie. Ita scilicet ne plus  congreges quam habeas! quia auaricia  semper pauper et egena est. nullo gaudium egeni habet cui sua sufficiunt. auariciam ergo  ut stipulam sparge. Et conculca. quoniam omnes honestos moresfol. 60radissipat. uelut tinea uestimentum demollitur.  ipsaque semper mendicat. et uelut paruum habitaculum rusticie est. quod locum non habet. ubi  honestos mores conseruet. Iuxta hoc habitaculum sicut tumulus terre iacet quem uermes fodiendo evertunt. significans quod  plurium episcopi qui per rectam doctrinam  mentes hominum elevare deberent. cogitationes suas in thesauros suos ponunt!  nec uerba que alijs dicant. aut quibus se ipsos  refitiant adtendunt. O pater in ueritate iibi dico. quod omnia uerba hec in uera uisione uidi et audiui! Et propter uissionem et petitionem tuam e scripsi. Non ergo super hijs  mireris. sed omnem uitam tuam a puericia tua usque nunc considera. Nomen quoque tuum  muta. scilicet ut agnus de lupo fias. quia  lupus agnum libenter rapit! Et esto in epulis peregrini filij qui peccata sua donfitendo ad patrem suum  cucuuit dicens. pater paccaui in elum et coram  te! super quo omnes chori angelorum gaudebant.  mirantes quod post nequiciam peccatorum suorum  deus tantam gratiam illi contulit. Itaque adiunge te floribus et pigmentis. quatinus propter. dulcem  odorem tuum populus gaudeat! quoniam *Buchstabe a supra lineam getilgt* honestum  et utilem pastorem habeat. et ut  etiam uocem domini audre merearis. Euge serue bone  et fidelis. intra in gaudium domini tuj. ", 273);
        result.annotations().stream().filter(annotationRecord -> annotationRecord.type().equals("deleted"))
                .forEach(annotationRecord -> {
                    AnnotationPositionRecord annotationPositionRecord = annotationRecord.toAnnotationPositionRecord(result.text());
                    assertEquals(1286, annotationPositionRecord.start());
                    assertEquals(1287, annotationPositionRecord.end());
                });
    }

    @Test
    void json() throws IOException {
        String text = new String(getClass().getClassLoader().getResourceAsStream("Hildegard/fixed/ok/Wr044-59va.txt").readAllBytes(), StandardCharsets.UTF_8);
        CharactersAndAnnotations charactersAndAnnotations = getCharactersAndAnnotations(text);
        System.out.println(charactersAndAnnotations.toJson());
    }

    @Test
    void deleted() {
        antlrTest("temp|ta[del[n]del]t", "temptat", 1);
    }
}
