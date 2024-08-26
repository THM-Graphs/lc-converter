package atag.lc;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Converter {

    private final Map<Integer, LetterMetaData> letterMetaDataList = new HashMap<>();
    private final List<JSONObject> annotationDbList = new ArrayList<>();

    private final Map<Integer, Annotation> annotationIncompleteList = new HashMap<>();
    private final Map<Integer, Annotation> annotationCompleteList = new HashMap<>();

    private static final List<String> annotations = List.of(
            //add annotation, die detektieren werden sollen
            "|||",
        "|",
        "#",
        "(",

        "[t[",
        "]t]",

        "[ru[",
        "]ru]",

        "[sl[",
        "]sl]",

        "<",
        ">",

        "[$[",
        "]$]",

    //     "[rm[",
    //    "]rm]",

        "[m[",
        "]m]",

        "[c[",
        "]c]",

        "[ra[",
        "]ra]",

        "[del[",
        "]del]",
    //del


    //repitition


    //lacuna?!?


    //unlesbar....


            "[t[", "]t]",
            "[ru[", "]ru]",
            "[sl[", "]sl]",
            "<", ">",
            "[$[", "]$]",
            "[rm[", "]rm]",
            "[m[", "]m]",
            "[c[", "]c]",
            "[ra[", "]ra]",
            "[del[", "]del]",
            "#",
            "|||",
            "|",
            "("
    );

    int annoId = 1;
    int colNr = 0;

    private static StringBuilder atomTextBuilder = null;
    private static SortedMap<Integer, String> sortedAnnotationList = new TreeMap<>();

    private static int indexCount = 0;

    //MetadatenLetter....
    private static String letterNumber = "0";

    //Offset für Briefwechsel
    private static Integer offSetLetter = 0;
    private static boolean letterChangeLines = false;
    private static boolean letterChangeColumns = false;
    private static boolean columnDeleteBlank = false;
    private static boolean columnEndLetter = false;



    //Infos für Annotationen...
    private static Integer deleteOff = 0;


    public void convert(String text) {
        sortedAnnotationList = detectAllAnnotation(text);

        atomTextBuilder = new StringBuilder(text);
        navigateAllAnnotations(sortedAnnotationList);
    }


    public static void main(String[] args) {


//-------------------------------------------Converter--------------------------------------------------
        //1. Text lesen... und convert to string builder...
        Converter converter = new Converter();

        String atomText = readAtomLetters();
        System.out.println(atomText);

        //2. 5er lines eliminieren... ist glaub ich nicht mehr notwendig
        // atomTextBuilder = remove5Lines(atomTextBuilder);
        // System.out.println(atomTextBuilder);

        //-------------------------------------------------------------------------------------------------------------------
        //3. Alle Annotationen erkennen....

        sortedAnnotationList = converter.detectAllAnnotation(atomText);
        System.out.println(sortedAnnotationList);


        //5. Nun alle Annotationen durchgehen
        converter.navigateAllAnnotations(sortedAnnotationList);

    }

    //"MERGE (e:Text { Guid: "25398404-5221-407d-b1a8-3a0f804601e3" }) ON CREATE SET e = {"Type": "Body",  "CurrentVersion": 0,  "Value": "Test",  "UserGuid": "fb067f75-a121-47c1-8767-99271c75cfc0",  "Name": "Test2",  "DateAddedUTC": "2021-01-12T15:01:49.6069889+00:00",  "IsDeleted": false,  "DisplayName": "Test2",  "Guid": "25398404-5221-407d-b1a8-3a0f804601e3"} ON MATCH SET e = {"Type": "Body",  "CurrentVersion": 0,  "Value": "Test",  "UserGuid": "fb067f75-a121-47c1-8767-99271c75cfc0",  "Name": "Test2",  "DateAddedUTC": "2021-01-12T15:01:49.6069889+00:00",  "IsDeleted": false,  "DisplayName": "Test2",  "Guid": "25398404-5221-407d-b1a8-3a0f804601e3"}"


    private void navigateAllAnnotations(SortedMap<Integer, String> sortedAnnotationList) {

        List<Integer> annotationsStartOffSets = sortedAnnotationList.keySet().stream().toList();

        //ersten spaltenwechsel....

        Integer tempIdStart = annoId++;
        Annotation annoStart = new Annotation(tempIdStart, "|||", "", 0, null, false);
        annotationIncompleteList.put(tempIdStart, annoStart);
        colNr++;

        //alle annotationen durchgehen....
        for (int i=0; i<annotationsStartOffSets.size();i++)
        {
            String annotationType = sortedAnnotationList.get(annotationsStartOffSets.get(i));

            switch (annotationType) {
                case "[t[", "]t]" -> {
                    int startOffSet = annotationsStartOffSets.get(i) - deleteOff;
                    int nextCount = i + 1;
                    if (nextCount >= annotationsStartOffSets.size())
                        nextCount = i;

                    if (annotationType.equals("[t[")) {
                        //TODO incomplete an setzen.....
                        Integer tempId = annoId++;
                        Annotation anno = new Annotation(tempId, annotationType, "", startOffSet, null, false);
                        annotationIncompleteList.put(tempId, anno);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 3);
                    } else if (annotationType.equals("]t]")) {
                        checkIncompleteListComplex("[t[", startOffSet);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 3);
                    }

                }
                case "[ru[", "]ru]" -> {
                    int startOffSet = annotationsStartOffSets.get(i) - deleteOff;
                    int nextCount = i + 1;
                    if (nextCount >= annotationsStartOffSets.size())
                        nextCount = i;

                    if (annotationType.equals("[ru[")) {
                        //TODO incomplete an setzen.....
                        Integer tempId = annoId++;
                        Annotation anno = new Annotation(tempId, annotationType, "", startOffSet, null, false);
                        annotationIncompleteList.put(tempId, anno);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 4);
                    } else if (annotationType.equals("]ru]")) {
                        checkIncompleteListComplex("[ru[", startOffSet);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 4);
                    }

                }
                case "[sl[", "]sl]" -> {
                    int startOffSet = annotationsStartOffSets.get(i) - deleteOff;
                    int nextCount = i + 1;
                    if (nextCount >= annotationsStartOffSets.size())
                        nextCount = i;

                    if (annotationType.equals("[sl[")) {
                        //TODO incomplete an setzen.....
                        Integer tempId = annoId++;
                        Annotation anno = new Annotation(tempId, annotationType, "", startOffSet, null, false);
                        annotationIncompleteList.put(tempId, anno);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 4);
                    } else if (annotationType.equals("]sl]")) {
                        checkIncompleteListComplex("[sl[", startOffSet);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 4);
                    }

                }
                case "<", ">" -> {
                    //  System.out.println("wahoooooooooooo");
                    int startOffSet = annotationsStartOffSets.get(i) - deleteOff;
                    int nextCount = i + 1;
                    if (nextCount >= annotationsStartOffSets.size())
                        nextCount = i;

                    if (annotationType.equals("<")) {
                        //TODO incomplete an setzen.....
                        Integer tempId = annoId++;
                        Annotation anno = new Annotation(tempId, annotationType, "", startOffSet, null, false);
                        annotationIncompleteList.put(tempId, anno);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 1);
                    } else if (annotationType.equals(">")) {
                        checkIncompleteListComplex("<", startOffSet);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 1);
                    }

                }
                case "[$[", "]$]" -> {
                    //  System.out.println("wahoooooooooooo");
                    int startOffSet = annotationsStartOffSets.get(i) - deleteOff;
                    int nextCount = i + 1;
                    if (nextCount >= annotationsStartOffSets.size())
                        nextCount = i;

                    if (annotationType.equals("[$[")) {
                        //TODO incomplete an setzen.....
                        Integer tempId = annoId++;
                        Annotation anno = new Annotation(tempId, annotationType, "", startOffSet, null, false);
                        annotationIncompleteList.put(tempId, anno);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 3);
                    } else if (annotationType.equals("]$]")) {
                        checkIncompleteListComplex("[$[", startOffSet);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 3);
                    }

                }
                case "[rm[", "]rm]" -> {
                    //  System.out.println("wahoooooooooooo");
                    int startOffSet = annotationsStartOffSets.get(i) - deleteOff;
                    int nextCount = i + 1;
                    if (nextCount >= annotationsStartOffSets.size())
                        nextCount = i;

                    if (annotationType.equals("[rm[")) {
                        //TODO incomplete an setzen.....
                        Integer tempId = annoId++;
                        Annotation anno = new Annotation(tempId, annotationType, "", startOffSet, null, false);
                        annotationIncompleteList.put(tempId, anno);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 4);
                    } else if (annotationType.equals("]rm]")) {
                        checkIncompleteListComplex("[rm[", startOffSet);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 4);
                    }

                }
                case "[m[", "]m]" -> {
                    //  System.out.println("wahoooooooooooo");
                    int startOffSet = annotationsStartOffSets.get(i) - deleteOff;
                    int nextCount = i + 1;
                    if (nextCount >= annotationsStartOffSets.size())
                        nextCount = i;

                    if (annotationType.equals("[m[")) {
                        //TODO incomplete an setzen.....
                        Integer tempId = annoId++;
                        Annotation anno = new Annotation(tempId, annotationType, "", startOffSet, null, false);
                        annotationIncompleteList.put(tempId, anno);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 3);
                    } else if (annotationType.equals("]m]")) {
                        checkIncompleteListComplex("[m[", startOffSet);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 3);
                    }

                }
                case "[c[", "]c]" -> {
                    //  System.out.println("wahoooooooooooo");
                    int startOffSet = annotationsStartOffSets.get(i) - deleteOff;
                    int nextCount = i + 1;
                    if (nextCount >= annotationsStartOffSets.size())
                        nextCount = i;

                    if (annotationType.equals("[c[")) {
                        //TODO incomplete an setzen.....
                        Integer tempId = annoId++;
                        Annotation anno = new Annotation(tempId, annotationType, "", startOffSet, null, false);
                        annotationIncompleteList.put(tempId, anno);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 3);
                    } else if (annotationType.equals("]c]")) {
                        checkIncompleteListComplex("[c[", startOffSet);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 3);
                    }

                }
                case "[ra[", "]ra]" -> {
                    //  System.out.println("wahoooooooooooo");
                    int startOffSet = annotationsStartOffSets.get(i) - deleteOff;
                    int nextCount = i + 1;
                    if (nextCount >= annotationsStartOffSets.size())
                        nextCount = i;

                    if (annotationType.equals("[ra[")) {
                        //TODO incomplete an setzen.....
                        Integer tempId = annoId++;
                        Annotation anno = new Annotation(tempId, annotationType, "", startOffSet, null, false);
                        annotationIncompleteList.put(tempId, anno);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 4);
                    } else if (annotationType.equals("]ra]")) {
                        checkIncompleteListComplex("[ra[", startOffSet);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 4);
                    }

                }
                case "[del[", "]del]" -> {
                    //  System.out.println("wahoooooooooooo");
                    int startOffSet = annotationsStartOffSets.get(i) - deleteOff;
                    int nextCount = i + 1;
                    if (nextCount >= annotationsStartOffSets.size())
                        nextCount = i;

                    if (annotationType.equals("[del[")) {
                        //TODO incomplete an setzen.....
                        Integer tempId = annoId++;
                        Annotation anno = new Annotation(tempId, annotationType, "", startOffSet, null, false);
                        annotationIncompleteList.put(tempId, anno);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 5);
                    } else if (annotationType.equals("]del]")) {
                        checkIncompleteListComplex("[del[", startOffSet);
                        atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                        deleteOff = (Integer) (deleteOff + 5);
                    }

                }
                default -> {
                    int startOffSet = annotationsStartOffSets.get(i) - deleteOff;
                    int endOffSet = findEnd(startOffSet, annotationType);

                    int nextCount = i + 1;
                    if (nextCount >= annotationsStartOffSets.size())
                        nextCount = i;

                    if (endOffSet <= (annotationsStartOffSets.get(nextCount) - deleteOff)) {

                        if (annotationType.equals("#")) {

                            //   System.out.println(letterNumber);
                            if (letterNumber.equals("END"))
                                System.exit(0);

                            if (Integer.parseInt(letterNumber) >= 1) {
                                savePreviousLetter(startOffSet, annoId, colNr);
                                annotationCompleteList.clear();
                            }
                            letterNumber = extractAnnotationInformation(annotationType, startOffSet, endOffSet + 1);
                            atomTextBuilder.replace(startOffSet, endOffSet + 1, "");
                            deleteOff = (Integer) (deleteOff + letterNumber.length() + 2);
                            //hier neu setzen wenn col


                        } else if (annotationType.equals("|||")) {
                            colNr++;

                            System.out.println("Kommen wir hier her");
                            checkIncompleteList("|", startOffSet + 1, endOffSet);
                            checkIncompleteList("|||", startOffSet, endOffSet);
                            atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                            deleteOff = (Integer) (deleteOff + annotationType.length() + 1);

                        } else if (annotationType.equals("|")) {


                            checkIncompleteList(annotationType, startOffSet, endOffSet);

                            String lineText = extractAnnotationInformation(annotationType, startOffSet, endOffSet + 1);
                            Integer tempId = annoId++;
                            Annotation anno = new Annotation(tempId, annotationType, lineText, startOffSet, (Integer) (endOffSet - 1), false);
                            annotationCompleteList.put(tempId, anno);
                            atomTextBuilder.replace(startOffSet, startOffSet + 1, "");
                            atomTextBuilder.replace(endOffSet - 1, endOffSet, "");
                            deleteOff = (Integer) (deleteOff + 2);
                        } else if (annotationType.equals("(")) {
                            String erweiterungText = extractAnnotationInformation(annotationType, startOffSet, (Integer) (endOffSet + 1));

                            Integer tempId = annoId++;
                            Annotation anno = new Annotation(tempId, annotationType, erweiterungText, startOffSet, (Integer) (endOffSet - 2), false);
                            annotationCompleteList.put(tempId, anno);
                            //erstes und 2es...
                            atomTextBuilder.replace(startOffSet, startOffSet + 1, "");
                            atomTextBuilder.replace(endOffSet - 1, endOffSet, "");

                            deleteOff = (Integer) (deleteOff + 2);
                        }

                    }
                    //Hier wenn inside atag.lc.Annotation...
                    else {
                        Integer tempId;

                        if (annotationType.equals("|||")) {
                            colNr++;

                            if (colNr % 2 != 0) {
                                //col hinzufügen...
                                tempId = annoId++;
                                System.out.println("AUF");

                                if (startOffSet - 1 > 0) {

                                    if (!atomTextBuilder.substring(startOffSet + annotationType.length(), startOffSet + annotationType.length() + 1).equals(" ")) {
                                        columnDeleteBlank = true;
                                    }
                                    if (atomTextBuilder.substring(startOffSet + annotationType.length(), startOffSet + annotationType.length() + annotationType.length()).contains("#")) {
                                        System.out.println(letterNumber + "  #.....");
                                        columnDeleteBlank = false;
                                        columnEndLetter = true;
                                    }
                                } else {
                                    String lineText = atomTextBuilder.substring(startOffSet, startOffSet + annotationType.length() + 1);
                                    //System.out.println("auf: " + lineText);
                                }

                                Annotation anno = null;
                                Annotation annoLine = null;
                                if (columnDeleteBlank) {
                                    //Spalten
                                    anno = new Annotation(tempId, annotationType, null, (Integer) (startOffSet - 1), null, true);
                                    annotationIncompleteList.put(tempId, anno);
                                    //Lines
                                    tempId = annoId++;
                                    checkIncompleteList("|", startOffSet - 1, endOffSet - 1);
                                    annoLine = new Annotation(tempId, "|", null, (Integer) (startOffSet - 1), null, true);
                                    annotationIncompleteList.put(tempId, annoLine);
                                } else {
                                    //nur machen, wenn columnEndLetter nicht endet... ansonsten das am anfang des nächstewn briefs machen...
                                    if (!columnEndLetter) {
                                        //Spalten
                                        anno = new Annotation(tempId, annotationType, null, (Integer) (startOffSet - 1), null, true);
                                        annotationIncompleteList.put(tempId, anno);
                                        //Lines
                                        tempId = annoId++;
                                        checkIncompleteList("|", startOffSet, endOffSet - 1);
                                        annoLine = new Annotation(tempId, "|", null, startOffSet, null, true);
                                        annotationIncompleteList.put(tempId, annoLine);
                                    }
                                }


                            } else {

                                //  System.out.println("ZU");
                                System.out.println(atomTextBuilder.substring(startOffSet - 1, startOffSet + annotationType.length() + annotationType.length() + 1));
                                if (!atomTextBuilder.substring(startOffSet - 1, startOffSet).equals(" ")) {
                                    String lineText = atomTextBuilder.substring(startOffSet - 1, startOffSet + annotationType.length() + 1);
                                    //       System.out.println("zu: " + lineText);
                                    columnDeleteBlank = true;

                                }

                                if (columnDeleteBlank) {
                                    checkIncompleteList(annotationType, startOffSet - 1, endOffSet + 1);
                                    columnDeleteBlank = false;
                                } else
                                    checkIncompleteList(annotationType, startOffSet, endOffSet);
                            }
                            //hier ansetzen....
                            //text + delete offset
                            if (!columnDeleteBlank) {
                                atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                                deleteOff = (Integer) (deleteOff + annotationType.length());
                            } else {
                                atomTextBuilder.replace(startOffSet - 1, startOffSet + annotationType.length(), "");
                                deleteOff = (Integer) (deleteOff + annotationType.length() + 1);
                                // System.out.println("del....");
                                columnDeleteBlank = false;
                            }

                        } else {
                            checkIncompleteList(annotationType, startOffSet, endOffSet);
                            tempId = annoId++;
                            Annotation anno = new Annotation(tempId, annotationType, null, startOffSet, null, true);

                            annotationIncompleteList.put(tempId, anno);
                            atomTextBuilder.replace(startOffSet, startOffSet + annotationType.length(), "");
                            deleteOff = (Integer) (deleteOff + annotationType.length());
                        }

                    }
                }
            }

        }
    }


    private void savePreviousLetter(Integer startOffSetNextLetter, int annoId, int colNr) {
        //create UUID für Brief....
        //String userUuid = "fb067f75-a121-47c1-8767-99271c75cfc0";
        //String codexSection = "f5f6deaa-8356-4c97-931a-14e0b02f08b2";
        System.out.println("safe later");

        JSONObject letter = new JSONObject();

        //1.Text in json packen + in DB speichern.....
        String uuidText = java.util.UUID.randomUUID().toString();
        letter.put("text", atomTextBuilder.substring(offSetLetter,startOffSetNextLetter));
        //public void executeAddText(String Guid, String TextValue, String Name, String TextType, String DataAddedUTC) {
        //  DB.executeAddText(uuidText,atomTextBuilder.substring(offSetLetter,startOffSetNextLetter),"R"+letterNumber, "CodexLetter", Instant.now().toString().replace("00Z", "+00:00"));

        //1.2 Relation zur Sektion erstellen....
        String uuidTextToSection = java.util.UUID.randomUUID().toString();
        //public void executeLinkTextToSection(String TextUid, String RelationUid, String SectionUid) {
        //  DB.executeLinkTextToSection(uuidText,uuidTextToSection);


        //1.3 StandoffpropertyKnoten erzeugen für Text....
        //((String Guid, String PropertyType, String DataAddedUTC, String DataModifiedUTC,
        //                                       String UserGuid, String folStart, String folEnd, String absender, String empfaenger, String inscriptio, String incipit, String explicit, String vanAcker,
        //                                       String briefthemen, String nfol){
        //generate UUid...
        String uuidPropertyMeta = java.util.UUID.randomUUID().toString();
        LetterMetaData metaInfos = letterMetaDataList.get(Optional.of(Integer.parseInt(letterNumber)));

        //TODO hier müssen wir zaubern...
     /*   DB.executeAddMetaProperty(uuidPropertyMeta,"metaInfoLetter",Instant.now().toString().replace("00Z", "+00:00"),
                Instant.now().toString().replace("00Z", "+00:00"), metaInfos.FolStart,metaInfos.FolEnd,
                metaInfos.Absender,metaInfos.Empfaenger,metaInfos.Inscriptio, metaInfos.Incipit,metaInfos.Explicit, metaInfos.Absender2, metaInfos.Empfaenger2,
                metaInfos.Absender1Ort, metaInfos.Absender1Rolle, metaInfos.Absender2Ort, metaInfos.Absender2Rolle, metaInfos.Empfaenger1Ort, metaInfos.Empfaenger1Rolle,
                metaInfos.Empfaenger2Ort, metaInfos.Empfaenger2Rolle);
*/
        indexCount++;

        //1.4 Relation zu knoten erzuegen ...
        //Property relation abspeichern.....
        String uuidPropertyMetaToText = java.util.UUID.randomUUID().toString();
        //     DB.executeLinkPropertyToTextMeta(uuidText,uuidPropertyMetaToText,uuidPropertyMeta);


        //2.annotationen/properties...
        JSONArray properties = new JSONArray();

        for(Map.Entry<Integer, Annotation> annotations:annotationCompleteList.entrySet())
        {
            JSONObject annotation = new JSONObject();
            annotation.put("index",indexCount);
            //    annotation.put("guid","null");

            if(annotations.getValue().type.equals("|"))
            {
                annotation.put("type","leiden/line");
                //      System.out.println("line: "+annotations.getValue().text);
            }
            else if(annotations.getValue().type.equals("("))
            {
                annotation.put("type","leiden/expansion");
                //     System.out.println("Erweiterung: "+annotations.getValue().text);
            }
            else if(annotations.getValue().type.equals("|||"))
            {
                annotation.put("type","leiden/column");
                //   System.out.println("Spalte: "+annotations.getValue().text);
            }
            else if(annotations.getValue().type.equals("[t["))
            {
                annotation.put("type","leiden/titulus");
                //   System.out.println("Spalte: "+annotations.getValue().text);
            }
            else if(annotations.getValue().type.equals("[ru["))
            {
                annotation.put("type","leiden/emphasis");
                //   System.out.println("Spalte: "+annotations.getValue().text);
            }
            else if(annotations.getValue().type.equals("[sl["))
            {
                annotation.put("type","leiden/supralineam");
                //   System.out.println("Spalte: "+annotations.getValue().text);
            }
            else if(annotations.getValue().type.equals("<"))
            {
                annotation.put("type","leiden/sic");
                //     System.out.println("sic....: "+annotations.getValue().text);
            }
            else if(annotations.getValue().type.equals("[$["))
            {
                annotation.put("type","leiden/transposition");
                //     System.out.println("sic....: "+annotations.getValue().text);
            }
            else if(annotations.getValue().type.equals("[rm["))
            {
                annotation.put("type","leiden/recensi");
                //     System.out.println("sic....: "+annotations.getValue().text);
            }
            else if(annotations.getValue().type.equals("[m["))
            {
                annotation.put("type","leiden/marginalia");
            }
            else if(annotations.getValue().type.equals("[c["))
            {
                annotation.put("type","leiden/correction");
            }
            else if(annotations.getValue().type.equals("[ra["))
            {
                annotation.put("type","leiden/rewritten");
            }
            else if(annotations.getValue().type.equals("[del["))
            {
                annotation.put("type","leiden/striked-out");
            }


            annotation.put("layer","null");

            if(!annotations.getValue().type.equals("|||"))
                annotation.put("text",annotations.getValue().text);
            else
                annotation.put("text","");

            // annotation.put("text",annotations.getValue().text);
            annotation.put("value","null");
            annotation.put("startIndex",annotations.getValue().startOff-offSetLetter);
            annotation.put("endIndex",annotations.getValue().endOff-offSetLetter);


            JSONObject attribute = new JSONObject();
            attribute.put("letterFlag",annotations.getValue().letterFlag);
            // attributes.put(attribute);

            annotation.put("attributes",attribute);
            annotation.put("isZeroPoint",false);
            annotation.put("isDeleted",false);
            annotation.put("userGuid","b60459a9-e172-4280-b252-d5b2db2514ac");

            properties.put(annotation);
            annotationDbList.add(annotation);

            indexCount++;
        }

        //3. incompletelist...
        ArrayList<Integer> removeList = new ArrayList<Integer>();
        for(Map.Entry<Integer, Annotation> annotations:annotationIncompleteList.entrySet())
        {
            JSONObject annotation = new JSONObject();
            annotation.put("index",indexCount);
            //     annotation.put("guid","null");

            if(annotations.getValue().type.equals("|"))
            {
                annotation.put("type","leiden/line");
                letterChangeLines = true;
                //    System.out.println("line: "+annotations.getValue().text);
            }
            else if(annotations.getValue().type.equals("("))
            {
                annotation.put("type","leiden/expansion");
                //  System.out.println("Erweiterung: "+annotations.getValue().text);
            }
            else if(annotations.getValue().type.equals("|||"))
            {
                letterChangeColumns = true;
                annotation.put("type","leiden/column");
            }

            annotation.put("layer","null");

            if(!annotations.getValue().type.equals("|||")) {
                annotation.put("text", atomTextBuilder.substring(annotations.getValue().startOff, startOffSetNextLetter - 1));
                //   System.out.println(atomTextBuilder.substring(annotations.getValue().startOff, startOffSetNextLetter -1));
            }
            else
                annotation.put("text","");

            //    System.out.println(annotations.getValue().startOff);
            //     System.out.println(startOffSetNextLetter - 1);

            //    System.out.println(atomTextBuilder.substring(annotations.getValue().startOff,startOffSetNextLetter-1));
            annotation.put("value","null");
            annotation.put("startIndex",annotations.getValue().startOff-offSetLetter);
            //letterende nehmen....
            System.out.println("LetterEnde: " + (startOffSetNextLetter-1-offSetLetter));
            annotation.put("endIndex",startOffSetNextLetter-1-offSetLetter-1);


            JSONObject attribute = new JSONObject();
            attribute.put("letterFlag",true);
            //   attributes.put(attribute);

            annotation.put("attributes",attribute);
            annotation.put("isZeroPoint",false);
            annotation.put("isDeleted",false);
            annotation.put("userGuid","b60459a9-e172-4280-b252-d5b2db2514ac");

            if(annotations.getValue().startOff != (startOffSetNextLetter - 1)) {
                properties.put(annotation);
                annotationDbList.add(annotation);
                indexCount++;
                annotations.getValue().setLetterFlag(true);
                annotations.getValue().setStartOff(Integer.valueOf(startOffSetNextLetter));
            }
            //wenn gleich löschen und nicht speichern....
            else {
                //   System.out.println("wenn gleich löschen und nicht speichern");
                removeList.add(annotations.getKey());
                //     annotationIncompleteList.remove(annotations.getKey());
            }
        }
        for(Integer removeId : removeList)
        {
            annotationIncompleteList.remove(removeId);
        }

        //check if | ist drinnen oder nicht....
        boolean containsLine = false;
        boolean containsCol = false;
        for(Map.Entry<Integer, Annotation> tempInc : annotationIncompleteList.entrySet())
        {
            //  System.out.println(tempInc.getValue().getType());
            if(tempInc.getValue().getType().equals("|"))
                containsLine = true;

            if(tempInc.getValue().getType().equals("|||"))
                containsCol = true;
        }
        //wenn keine line mehr drinnen ist auf anfang setzen...
        if(containsLine == false)
        {
            Integer tempId = annoId++;
            Annotation annoLine = new Annotation(tempId, "|", null, startOffSetNextLetter, null, true);
            annotationIncompleteList.put(tempId, annoLine);
        }
        if(containsCol == false && columnEndLetter == false)
        {

            System.out.println("ohjeeee COl checken...");
            Integer tempId = annoId++;
            Annotation annoLine = new Annotation(tempId, "|||", null, startOffSetNextLetter, null, true);
            annotationIncompleteList.put(tempId, annoLine);
            colNr++;
        }
        if( columnEndLetter  == true)
        {
            System.out.println("Spalte am Ende vom Brief.......");
            Integer tempId = annoId++;
            Annotation annoLine = new Annotation(tempId, "|||", null, startOffSetNextLetter, null, true);
            annotationIncompleteList.put(tempId, annoLine);
            columnEndLetter = false;
            // colNr++;
        }

        //4 alles zusammenbauen...
        System.out.println("text json..."+letterNumber);
        letter.put("properties",properties);
        //letter.put(properties);
        //  System.out.println(letter.toString());

        //4...properties zu Text in die DB speichern....
        for(JSONObject tempAnno : annotationDbList)
        {

            // atag.lc.Annotation tempAnno2 = (atag.lc.Annotation) tempAnno;
            String uuidProperty = java.util.UUID.randomUUID().toString();

            properties.get(0);
            //        System.out.println("AnnoIndex: "+tempAnno.getInt("index"));
            // System.out.println("AnnoAttributes....: "+ tempAnno.get("attributes"));
            JSONObject attri = (JSONObject) tempAnno.get("attributes");
            //Property abspeichern....
//            System.out.println(attri.length());

            System.out.println(tempAnno);
            tempAnno.getInt("index");
            tempAnno.getString("text");
            tempAnno.getString("type");
            tempAnno.getInt("startIndex");
            tempAnno.getInt("endIndex");
            Instant.now().toString().replace("00Z", "+00:00");
            Instant.now().toString().replace("00Z", "+00:00");
            attri.getBoolean("letterFlag");

         /*  DB.executeAddProperties(uuidProperty, tempAnno.getInt("index"),tempAnno.getString("text"),tempAnno.getString("type"),tempAnno.getInt("startIndex"),
                    tempAnno.getInt("endIndex"), Instant.now().toString().replace("00Z", "+00:00"),Instant.now().toString().replace("00Z", "+00:00")
                   ,attri.getBoolean("letterFlag"));

            //Property relation abspeichern.....
            String uuidPropertyToText = java.util.UUID.randomUUID().toString();
           DB.executeLinkPropertyToText(uuidText,uuidPropertyToText,uuidProperty);

*/

            properties.get(0);
        }

        annotationDbList.clear();

        //5
        PrintWriter pWriter = null;
        try {
            //Todo name setzen...
            String briefName = "Brief_Json_Text_New_"+letterNumber;
            pWriter = new PrintWriter(new BufferedWriter(new FileWriter("out/"+ briefName +".txt")));
            pWriter.println(letter.toString());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (pWriter != null){
                pWriter.flush();
                pWriter.close();
            }
        }

        //am Ende offSetLetter setzen...
        offSetLetter = startOffSetNextLetter;

    }

    private static String extractAnnotationInformation(String annotationType, Integer annoStart, int annoEnd) {
        String annoText ="";
        if(annotationType.equals("#"))
        {

            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(atomTextBuilder.substring(annoStart,annoEnd));
            while(m.find()) {
                annoText= m.group();
            }
        }
        else  if(annotationType.equals("("))
        {
            annoText = atomTextBuilder.substring(annoStart+1, annoEnd-1);
        }
        else  if(annotationType.equals("|"))
        {
            annoText = atomTextBuilder.substring(annoStart+1, annoEnd-1);
        }

        //System.out.println(annoText);

        return annoText;
    }


    private void checkIncompleteListComplex(String annotationType, Integer endOffSet) {
        Integer deleteIndex= (Integer) 0;
        if(annotationType.equals("[t["))
        {
            for(Map.Entry<Integer, Annotation> tempAnno : annotationIncompleteList.entrySet()) {
                if (tempAnno.getValue().type.equals("[t[")) {
                    Annotation anno = tempAnno.getValue();
                    anno.text = atomTextBuilder.substring(anno.startOff, endOffSet);
                    //   System.out.println(anno.text);
                    anno.endOff = (Integer) (endOffSet-1);
                    annotationCompleteList.put(anno.id,anno);
                    deleteIndex = tempAnno.getKey();
                    break;
                }
            }
        }
        else if(annotationType.equals("[ru["))
        {
            for(Map.Entry<Integer, Annotation> tempAnno : annotationIncompleteList.entrySet()) {
                if (tempAnno.getValue().type.equals("[ru[")) {
                    Annotation anno = tempAnno.getValue();
                    anno.text = atomTextBuilder.substring(anno.startOff, endOffSet);
                    //    System.out.println(anno.text);
                    anno.endOff = (Integer) (endOffSet-1);
                    annotationCompleteList.put(anno.id,anno);
                    deleteIndex = tempAnno.getKey();
                    break;
                }
            }
        }
        else if(annotationType.equals("[sl["))
        {
            for(Map.Entry<Integer, Annotation> tempAnno : annotationIncompleteList.entrySet()) {
                if (tempAnno.getValue().type.equals("[sl[")) {
                    Annotation anno = tempAnno.getValue();
                    anno.text = atomTextBuilder.substring(anno.startOff, endOffSet);
                    //     System.out.println(anno.text);
                    anno.endOff = (Integer) (endOffSet-1);
                    annotationCompleteList.put(anno.id,anno);
                    deleteIndex = tempAnno.getKey();
                    break;
                }
            }
        }
        else if(annotationType.equals("<"))
        {
            for(Map.Entry<Integer, Annotation> tempAnno : annotationIncompleteList.entrySet()) {
                if (tempAnno.getValue().type.equals("<")) {
                    Annotation anno = tempAnno.getValue();
                    anno.text = atomTextBuilder.substring(anno.startOff, endOffSet);
                    // System.out.println(anno.text);
                    anno.endOff = (Integer) (endOffSet-1);
                    annotationCompleteList.put(anno.id,anno);
                    deleteIndex = tempAnno.getKey();
                    break;
                }
            }
        }
        else if(annotationType.equals("[$["))
        {
            for(Map.Entry<Integer, Annotation> tempAnno : annotationIncompleteList.entrySet()) {
                if (tempAnno.getValue().type.equals("[$[")) {
                    Annotation anno = tempAnno.getValue();
                    anno.text = atomTextBuilder.substring(anno.startOff, endOffSet);
                    System.out.println(anno.text);
                    anno.endOff = (Integer) (endOffSet-1);
                    annotationCompleteList.put(anno.id,anno);
                    deleteIndex = tempAnno.getKey();
                    break;
                }
            }
        }
        else if(annotationType.equals("[rm["))
        {
            for(Map.Entry<Integer, Annotation> tempAnno : annotationIncompleteList.entrySet()) {
                if (tempAnno.getValue().type.equals("[rm[")) {
                    Annotation anno = tempAnno.getValue();
                    anno.text = atomTextBuilder.substring(anno.startOff, endOffSet);
                    System.out.println(anno.text);
                    anno.endOff = (Integer) (endOffSet-1);
                    annotationCompleteList.put(anno.id,anno);
                    deleteIndex = tempAnno.getKey();
                    break;
                }
            }
        }
        else if(annotationType.equals("[m["))
        {
            for(Map.Entry<Integer, Annotation> tempAnno : annotationIncompleteList.entrySet()) {
                if (tempAnno.getValue().type.equals("[m[")) {
                    Annotation anno = tempAnno.getValue();
                    anno.text = atomTextBuilder.substring(anno.startOff, endOffSet);
                    System.out.println(anno.text);
                    anno.endOff = (Integer) (endOffSet-1);
                    annotationCompleteList.put(anno.id,anno);
                    deleteIndex = tempAnno.getKey();
                    break;
                }
            }
        }
        else if(annotationType.equals("[c["))
        {
            for(Map.Entry<Integer, Annotation> tempAnno : annotationIncompleteList.entrySet()) {
                if (tempAnno.getValue().type.equals("[c[")) {
                    Annotation anno = tempAnno.getValue();
                    anno.text = atomTextBuilder.substring(anno.startOff, endOffSet);
                    System.out.println(anno.text);
                    anno.endOff = (Integer) (endOffSet-1);
                    annotationCompleteList.put(anno.id,anno);
                    deleteIndex = tempAnno.getKey();
                    break;
                }
            }
        }
        else if(annotationType.equals("[ra["))
        {
            for(Map.Entry<Integer, Annotation> tempAnno : annotationIncompleteList.entrySet()) {
                if (tempAnno.getValue().type.equals("[ra[")) {
                    Annotation anno = tempAnno.getValue();
                    anno.text = atomTextBuilder.substring(anno.startOff, endOffSet);
                    System.out.println(anno.text);
                    anno.endOff = (Integer) (endOffSet-1);
                    annotationCompleteList.put(anno.id,anno);
                    deleteIndex = tempAnno.getKey();
                    break;
                }
            }
        }
        else if(annotationType.equals("[del["))
        {
            for(Map.Entry<Integer, Annotation> tempAnno : annotationIncompleteList.entrySet()) {
                if (tempAnno.getValue().type.equals("[del[")) {
                    Annotation anno = tempAnno.getValue();
                    anno.text = atomTextBuilder.substring(anno.startOff, endOffSet);
                    System.out.println(anno.text);
                    anno.endOff = (Integer) (endOffSet-1);
                    annotationCompleteList.put(anno.id,anno);
                    deleteIndex = tempAnno.getKey();
                    break;
                }
            }
        }
        annotationIncompleteList.remove(deleteIndex);
    }

    private void checkIncompleteList(String annotationType, int startOffSetNextAnno, int endOffSetNextAnno) {
        int deleteIndex=0;
        // System.out.println(annotationIncompleteList);
        if(annotationType.equals("|"))
        {
            for(Map.Entry<Integer, Annotation> tempAnno : annotationIncompleteList.entrySet())
            {
                if(tempAnno.getValue().type.equals("|"))
                {
                    Annotation anno = tempAnno.getValue();
                    anno.text = atomTextBuilder.substring(anno.startOff,startOffSetNextAnno);
                    if(letterChangeLines)
                    {
                        anno.letterFlag = true;
                        letterChangeLines = false;
                    }
                    else
                        anno.letterFlag = false;

                    anno.endOff = (Integer) (startOffSetNextAnno-1);
                    annotationCompleteList.put(anno.id,anno);
                    deleteIndex = tempAnno.getKey();
                    break;
                }
            }
            //  annotationIncompleteList.remove(deleteIndex);

            //     annotationIncompleteList.remove(deleteIndex);
        }
        else if (annotationType.equals("|||"))
        {
            for(Map.Entry<Integer, Annotation> tempAnno : annotationIncompleteList.entrySet())
            {
                if(tempAnno.getValue().type.equals("|||"))
                {
                    Annotation anno = tempAnno.getValue();
                    anno.text = atomTextBuilder.substring(anno.startOff,startOffSetNextAnno);

                    if(letterChangeColumns)
                    {
                        anno.letterFlag = true;
                        letterChangeColumns = false;
                    }
                    else
                        anno.letterFlag = false;

                    anno.endOff = (Integer) startOffSetNextAnno;
                    annotationCompleteList.put(anno.id,anno);
                    deleteIndex = tempAnno.getKey();
                    break;

                }
            }
            //   annotationIncompleteList.remove(deleteIndex);
        }
        annotationIncompleteList.remove((Integer) deleteIndex);
    }

    private static int findEnd(Integer startOff, String annotationType) {
        //  System.out.println("searchend...: "+annotationType);
        int endOffSet = 0;
        if(annotationType.equals("|||"))
        {
            endOffSet = atomTextBuilder.indexOf("|||",startOff+annotationType.length());
        }
        else if (annotationType.equals("|"))
        {
            endOffSet = atomTextBuilder.indexOf("|",startOff+annotationType.length());
        }
        else if (annotationType.equals("("))
        {
            endOffSet = atomTextBuilder.indexOf(")",startOff+annotationType.length());
        }
        else if(annotationType.equals("#"))
            endOffSet = atomTextBuilder.indexOf("#",startOff+annotationType.length());

        return endOffSet;
    }

    private SortedMap<Integer, String> detectAllAnnotation(String atomText) {
        Map<Integer, String> annotationList = new HashMap<>();
        int searchStartIndex = (Integer) 0;
        int index = (Integer) 0;
        for(int i = 0; i <annotations.size();i++ )
        {
            index = (Integer) 0;
            searchStartIndex = (Integer) 0;
            while (index != -1) {

                //index = atomTextBuilder.indexOf(annotations.get(i), searchStartIndex);  // Slight improvement

                index = (Integer) atomText.indexOf(annotations.get(i), searchStartIndex);

                if (index != -1) {
                    if(annotations.get(i).equals("|")) {
                        if(index!=0)
                            if (atomText.charAt(index - 1) != '|' && atomText.charAt(index + 2) != '|') {
                                annotationList.put(index, annotations.get(i));
                            }
                    }
                    else {
                        //    System.out.println(atomTextBuilder.substring(index,atomTextBuilder.length()));
                        annotationList.put(index,annotations.get(i));
                    }
                    // if(index==0)
                    searchStartIndex = (Integer) (index + annotations.get(i).length());
                }
            }
        }

        SortedMap<Integer, String> sortedAnnotationList = new TreeMap<Integer, String>();
        sortedAnnotationList.putAll(annotationList);

        //welche Annotationen sollen gelöscht werden?!?
        ArrayList<Integer> indexDeleteList = new ArrayList<Integer>();
        boolean deleteFlag=false;
        for(Map.Entry<Integer,String> entry : sortedAnnotationList.entrySet()) {
            if(deleteFlag == true)
            {
                indexDeleteList.add(entry.getKey());
                deleteFlag = false;
            }
            else if(entry.getValue().equals("#"))
            {
                deleteFlag = true;
            }

        }
        //  System.out.println(indexDeleteList);

        for(Integer id : indexDeleteList)
            sortedAnnotationList.remove(id);

        return sortedAnnotationList;
    }

    private static StringBuilder remove5Lines(StringBuilder atomTextBuilder) {
        int index = 0;
        int searchIndex = 3;
        int wordL = 0;
        while (index != -1) {
            // System.out.println(index);
            index = atomTextBuilder.indexOf("||", searchIndex );  // Slight improvement
            if (index != -1)
            {
                if(index==0){
                    System.out.println("test");
                }
                else if(atomTextBuilder.charAt(index-1) != '|' && atomTextBuilder.charAt(index+2) != '|') {
                    atomTextBuilder.replace(index, index + 2, "|");
                }
                searchIndex = index + 2;

            }
        }

        return atomTextBuilder;
    }

    public static String readAtomLetters(){

        try {
            String text = "";
            File myObj = new File("C:\\markusJ\\Converter\\src\\converter_data//New.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                text += myReader.nextLine();

            }
            myReader.close();
            return text;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }
}





