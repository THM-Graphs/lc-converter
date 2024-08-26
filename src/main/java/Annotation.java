public class Annotation {

    public Integer id;
    public String type;
    public String text;
    public Integer startOff;
    public Integer endOff;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getStartOff() {
        return startOff;
    }

    public void setStartOff(Integer startOff) {
        this.startOff = startOff;
    }

    public Integer getEndOff() {
        return endOff;
    }

    public void setEndOff(Integer endOff) {
        this.endOff = endOff;
    }

    public boolean isLetterFlag() {
        return letterFlag;
    }

    public void setLetterFlag(boolean letterFlag) {
        this.letterFlag = letterFlag;
    }

    public boolean letterFlag;


    public Annotation(Integer id, String type, String text, Integer startOff, Integer endOff, boolean letterFlag) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.startOff = startOff;
        this.endOff = endOff;
        this.letterFlag = letterFlag;
    }




}
