package atag.lc;

import java.util.List;

public record AnnotationRecord(
        Integer id,
        String type,
        String text,
        UChar start,
        UChar end) {

    public AnnotationPositionRecord toAnnotationPositionRecord(List<UChar> fullText) {
        int startIndex = fullText.indexOf(start);
        if (startIndex == -1) {
            startIndex = fullText.indexOf(start.getPrevious());
        }

        int endIndex = fullText.indexOf(end);
        if (endIndex == -1) {
            endIndex = fullText.indexOf(end.getNext());
        }
        return new AnnotationPositionRecord(id, type, text, startIndex, endIndex);
    }
}
