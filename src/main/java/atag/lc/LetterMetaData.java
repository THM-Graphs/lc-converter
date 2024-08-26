package atag.lc;

public class LetterMetaData {

    public String FolStart;
    public String FolEnd;
    public String Absender;
    public String Absender2;
    public String Empfaenger;
    public String Empfaenger2;
    public String Inscriptio;
    public String Incipit;
    public String Explicit;
    public Integer Briefnummer;
    public String Absender1Rolle;
    public String Absender1Ort;
    public String Absender2Rolle;
    public String Absender2Ort;
    public String Empfaenger1Rolle;
    public String Empfaenger1Ort;
    public String Empfaenger2Rolle;
    public String Empfaenger2Ort;

    public LetterMetaData(Integer briefnummer,String folStart, String folEnd, String absender, String empfaenger, String inscriptio, String incipit, String explicit, String absender2, String empfaenger2,
                          String absender1Ort, String absender1Rolle, String absender2Ort, String absender2Rolle, String empfaenger1Ort, String empfaenger1Rolle, String empfaenger2Ort, String empfaenger2Rolle) {
        FolStart = folStart;
        FolEnd = folEnd;
        Absender = absender;
        Absender2 = absender2;
        Empfaenger = empfaenger;
        Empfaenger2 = empfaenger2;
        Inscriptio = inscriptio;
        Incipit = incipit;
        Explicit = explicit;
        Briefnummer = briefnummer;
        Absender1Rolle = absender1Rolle;
        Absender1Ort = absender1Ort;
        Absender2Rolle = absender2Rolle;
        Absender2Ort = absender2Ort;
        Empfaenger1Rolle = empfaenger1Rolle;
        Empfaenger1Ort = empfaenger1Ort;
        Empfaenger2Rolle = empfaenger2Rolle;
        Empfaenger2Ort = empfaenger2Ort;

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

    public String getAbsender() {
        return Absender;
    }

    public void setAbsender(String absender) {
        Absender = absender;
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

    public String getEmpfaenger() {
        return Empfaenger;
    }

    public void setEmpfaenger(String empfaenger) {
        Empfaenger = empfaenger;
    }

    public Integer getBriefnummer() {
        return Briefnummer;
    }

    public void setBriefnummer(Integer briefnummer) {
        Briefnummer = briefnummer;
    }


    public String getAbsender2() {
        return Absender2;
    }

    public void setAbsender2(String absender2) {
        Absender2 = absender2;
    }

    public String getEmpfaenger2() {
        return Empfaenger2;
    }

    public void setEmpfaenger2(String empfaenger2) {
        Empfaenger2 = empfaenger2;
    }

    public String getAbsender1Rolle() {
        return Absender1Rolle;
    }

    public void setAbsender1Rolle(String absender1Rolle) {
        Absender1Rolle = absender1Rolle;
    }

    public String getAbsender1Ort() {
        return Absender1Ort;
    }

    public void setAbsender1Ort(String absender1Ort) {
        Absender1Ort = absender1Ort;
    }

    public String getAbsender2Rolle() {
        return Absender2Rolle;
    }

    public void setAbsender2Rolle(String absender2Rolle) {
        Absender2Rolle = absender2Rolle;
    }

    public String getAbsender2Ort() {
        return Absender2Ort;
    }

    public void setAbsender2Ort(String absender2Ort) {
        Absender2Ort = absender2Ort;
    }

    public String getEmpfaenger1Rolle() {
        return Empfaenger1Rolle;
    }

    public void setEmpfaenger1Rolle(String empfaenger1Rolle) {
        Empfaenger1Rolle = empfaenger1Rolle;
    }

    public String getEmpfaenger1Ort() {
        return Empfaenger1Ort;
    }

    public void setEmpfaenger1Ort(String empfaenger1Ort) {
        Empfaenger1Ort = empfaenger1Ort;
    }

    public String getEmpfaenger2Rolle() {
        return Empfaenger2Rolle;
    }

    public void setEmpfaenger2Rolle(String empfaenger2Rolle) {
        Empfaenger2Rolle = empfaenger2Rolle;
    }

    public String getEmpfaenger2Ort() {
        return Empfaenger2Ort;
    }

    public void setEmpfaenger2Ort(String empfaenger2Ort) {
        Empfaenger2Ort = empfaenger2Ort;
    }
}
