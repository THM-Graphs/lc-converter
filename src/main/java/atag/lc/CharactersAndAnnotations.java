package atag.lc;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public record CharactersAndAnnotations(
        List<UChar> text,
        List<AnnotationRecord> annotations
) {
    public CharactersAndAnnotations(AnnotationRecord annotation) {
        this(new LinkedList<>(), List.of(annotation));
    }

    public CharactersAndAnnotations(List<AnnotationRecord> annotations) {
        this(new LinkedList<>(), annotations);
    }

    public CharactersAndAnnotations(String textAsString) {
        this(textAsString.chars().mapToObj(UChar::new).collect(Collectors.toCollection(LinkedList::new)), new ArrayList<>());
    }

    public void addAnnotation(AnnotationRecord annotation) {
        annotations.add(annotation);
    }

    public void addAnnotation(AnnotationPositionRecord apr) {
        annotations.add(new AnnotationRecord(apr.id(), apr.type(), apr.text(), text.get(apr.start()), text.get(apr.end())));
    }

    public String asString() {
        return text.stream().map(String::valueOf).collect(Collectors.joining());
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("text", asString());
        json.put("annotations", new JSONArray(annotations.stream().map(a -> a.toAnnotationPositionRecord(text())).map(AnnotationPositionRecord::toJson).toList()));
        return json;
    }
}
