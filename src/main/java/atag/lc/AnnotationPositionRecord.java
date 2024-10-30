package atag.lc;

import java.util.List;

public record AnnotationPositionRecord(
        Integer id,
        String type,
        String text,
        int start,
        int end) {

    public AnnotationRecord toAnnotationRecord(List<UChar> characters) {
        return new AnnotationRecord(id, type, text, characters.get(start), characters.get(end));
    }
}
