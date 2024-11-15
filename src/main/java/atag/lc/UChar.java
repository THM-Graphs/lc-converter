package atag.lc;

/**
 * Implementing a unique character class that does not follow equal semantics of the standard character class
 * Intentionally no `record` is used.
 */
public class UChar {

    private static UChar lastCreated = null;

    private final char character;
    private final UChar previous;
    private UChar next = null;

    public UChar(char character) {
        this.character = character;

        if (lastCreated != null) {
            lastCreated.setNext(this);
        }
        this.previous = lastCreated;
        lastCreated = this;
    }

    public UChar(int i) {
        this( (char) i);
    }

    public char getCharacter() {
        return character;
    }

    public UChar getPrevious() {
        return previous;
    }

    public UChar getNext() {
        return next;
    }

    public void setNext(UChar next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return String.valueOf(character);
    }
}
