package atag.lc;

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
}
