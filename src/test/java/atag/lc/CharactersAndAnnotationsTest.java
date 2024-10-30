package atag.lc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class CharactersAndAnnotationsTest {

    @Test
    void testConstructor() {
        CharactersAndAnnotations charactersAndAnnotations = new CharactersAndAnnotations("abc");
        assertEquals("abc", charactersAndAnnotations.asString());
        assertThat(charactersAndAnnotations.text())
                .hasSize(3)
                .extracting(UChar::getCharacter)
                .containsExactly('a', 'b', 'c');
    }

    @Test
    void testSameCharacter() {
        CharactersAndAnnotations charactersAndAnnotations = new CharactersAndAnnotations("aba");
        assertEquals("aba", charactersAndAnnotations.asString());
        assertThat(charactersAndAnnotations.text())
                .hasSize(3)
                .extracting(UChar::getCharacter)
                .containsExactly('a', 'b', 'a');
    }
}