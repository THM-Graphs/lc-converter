package atag.lc;

import org.json.JSONObject;

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

    public JSONObject toJson() {
        return new JSONObject()
                .put("id", id)
                .put("type", type)
                .put("text", text)
                .put("start", start)
                .put("end", end);
    }
}
