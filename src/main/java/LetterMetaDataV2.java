public class LetterMetaDataV2 {

    public String FolStart;
    public String FolEnd;
    public String Inscriptio;
    public String Incipit;
    public String Explicit;
    public Integer Briefnummer;


    public LetterMetaDataV2(Integer briefnummer, String folStart, String folEnd, String inscriptio, String incipit, String explicit) {
        FolStart = folStart;
        FolEnd = folEnd;
        Inscriptio = inscriptio;
        Incipit = incipit;
        Explicit = explicit;
        Briefnummer = briefnummer;
    }

    public String getFolStart() {
        return FolStart;
    }

    public void setFolStart(String folStart) {
        FolStart = folStart;
    }

    public String getFolEnd() {
        return FolEnd;
    }

    public void setFolEnd(String folEnd) {
        FolEnd = folEnd;
    }



    public String getInscriptio() {
        return Inscriptio;
    }

    public void setInscriptio(String inscriptio) {
        Inscriptio = inscriptio;
    }

    public String getIncipit() {
        return Incipit;
    }

    public void setIncipit(String incipit) {
        Incipit = incipit;
    }

    public String getExplicit() {
        return Explicit;
    }

    public void setExplicit(String explicit) {
        Explicit = explicit;
    }



    public Integer getBriefnummer() {
        return Briefnummer;
    }

    public void setBriefnummer(Integer briefnummer) {
        Briefnummer = briefnummer;
    }


}
