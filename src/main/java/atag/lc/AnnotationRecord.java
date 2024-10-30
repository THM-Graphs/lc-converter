package atag.lc;

public record AnnotationRecord(
        Integer id,
        String type,
        String text,
        UChar start,
        UChar end) {
}
