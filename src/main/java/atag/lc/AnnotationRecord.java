package atag.lc;

import org.json.JSONObject;

import java.util.List;

public record AnnotationRecord(
        Integer id,
        String type,
        String text,
        UChar start,
        UChar end) {

    public AnnotationPositionRecord toAnnotationPositionRecord(List<UChar> fullText) {
        return new AnnotationPositionRecord(id, type, text, fullText.indexOf(start), fullText.indexOf(end));
    }
}
