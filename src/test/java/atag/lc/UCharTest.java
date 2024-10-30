package atag.lc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UCharTest {

    @Test
    void equality() {
        UChar uChar = new UChar('a');
        UChar uChar2 = new UChar('a');
        assertNotEquals(uChar, uChar2);
    }

}