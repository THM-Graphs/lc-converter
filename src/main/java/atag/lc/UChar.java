package atag.lc;

/**
 * Implementing a unique character class that does not follow equal semantics of the standard character class
 * Intentionally no `record` is used.
 */
public class UChar {

    private final char character;

    public UChar(char character) {
        this.character = character;
    }

    public UChar(int i) {
        this.character = (char) i;
    }

    public char getCharacter() {
        return character;
    }

    @Override
    public String toString() {
        return String.valueOf(character);
    }
}
