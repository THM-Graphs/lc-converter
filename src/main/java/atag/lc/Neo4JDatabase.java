package atag.lc;

import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.*;
import org.neo4j.driver.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.*;
import org.neo4j.driver.Record;

public class Neo4JDatabase implements AutoCloseable {

    private String tagStart = "<u class='";
    private String tagEnd = "'>";
    private String closingTag = "</u>";

    //für Entities.... brauchen wir noch mehr layer? lassen wir in der ersten Version erstmal außen vor....
    private String tagStartLayer2 = "<s class='";
    private String closingTagLayer2 = "</s>";
    private String tagStartLayer3 = "<i class='";
    private String closingTagLayer3 = "</i>";


    private String tagStartCol = "<span class='";
    private String closingTagCol = "</u>";

    private String tagStartLine = "<span class='";
    private String closingTagLine = "</span>";

    private Integer textPosition = 0;

    //feste Uuids...
    private String WikidataProvider = "5cbfe806-d6bb-4341-b1ff-c701a9bd65d5";
    private String UserUuid = "fb067f75-a121-47c1-8767-99271c75cfc0";
    private String CodexSection = "f5f6deaa-8356-4c97-931a-14e0b02f08b2";

    private String CodexSectionM = "28e2a69a-1656-4b20-a99c-ac71cdbc8216";
    private String CodexSectionWR = "da58e34c-8eef-4f87-8f00-2458c1e023aa";
    private String CodexSectionW = "c658988e-045c-463a-bd55-d12751c7dc0c";
    private String CodexSectionZ = "01d3b20b-1a9f-4d49-9853-295b4b49ca99";
    private String CodexSectionB = "a8b910dd-0514-4080-a180-614a81ea062a";

    private HashMap<Integer,Value> standofProperties = new HashMap<Integer, Value>();

    private Driver driver;

    private StringBuilder htmlString =  new StringBuilder();;

    public Neo4JDatabase() {
        System.out.println("created Neo4jInstanz...");
        System.out.println(Instant.now().toString().replace("00Z", "+00:00"));
    }

    public void connectDb(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
        System.out.println(driver);
    }

    public void printResults(List<Record> results) {
        for (Record record : results) {
            Value rec = record.get("n");
            System.out.print("Record --> [");
            for (String key : rec.keys()) {
                System.out.print(key + " : " + rec.get(key) + ", ");
            }
            System.out.println("]");

            //TODO: Need to get all Relationship of this particular node
        }
    }

    public void RepairTextValue() {
        try (Session session = driver.session()) {

            Result result = session.run("MATCH (n:Text) where n.DisplayName starts with 'M' and n.TextType = 'CodexLetter' RETURN n;");

        /*Result result = session.run("MATCH (n:Regesta) WHERE n.identifier STARTS WITH 'RI II,4' " +
                "AND n.isoStartDate > date('1002-01-01') AND n.isoEndDate < date('1021-12-31') AND n.latLong IS NOT NULL RETURN n;");*/


            while (result.hasNext()) {
                Record tempR = result.next();

                List<Pair<String, Value>> values = tempR.fields();
                for (Pair<String, Value> nameValue : values) {
                    Value value = nameValue.value();

                 //   System.out.println(value.get("Value").asString());

                    System.out.println(value.get("DisplayName").asString());

                    //Todo....
                    String text = value.get("Value").asString();
                    String cleanedText = "";
                    for(int j = 0;j<text.length();j++)
                    {
                        if(Character.codePointAt(text, j) == 160)
                            cleanedText += " ";
                        else
                            cleanedText += text.charAt(j);

                    }
                   // System.out.println(cleanedText);
                    String exe = "MATCH (n:Text) where n.Name = \"" + value.get("DisplayName").asString() + "\"  set n.Value = \"" + cleanedText + "\" RETURN n;";

                    System.out.println("Textupdate...." + exe);

                    try (Session session2 = driver.session()) {
                        session2.writeTransaction(tx -> tx.run(exe));
                    }

                }
            }

        }

    }

    public void RepairTextValueLong() {
        try (Session session = driver.session()) {

            System.out.println("start....");
            Result result = session.run("MATCH (n:Text) where n.name starts with 'R' and n.type = 'variant' RETURN n;");

        /*Result result = session.run("MATCH (n:Regesta) WHERE n.identifier STARTS WITH 'RI II,4' " +
                "AND n.isoStartDate > date('1002-01-01') AND n.isoEndDate < date('1021-12-31') AND n.latLong IS NOT NULL RETURN n;");*/


            while (result.hasNext()) {
                Record tempR = result.next();

                List<Pair<String, Value>> values = tempR.fields();
                for (Pair<String, Value> nameValue : values) {
                    Value value = nameValue.value();

                    //   System.out.println(value.get("Value").asString());

                    System.out.println(value.get("name").asString());

                    //Todo....
                    String text = value.get("text").asString();
                    String cleanedText = "";
                    for(int j = 0;j<text.length();j++)
                    {
                        if(Character.codePointAt(text, j) == 160)
                            cleanedText += " ";
                        else
                            cleanedText += text.charAt(j);

                    }
                    // System.out.println(cleanedText);
                    String exe = "MATCH (n:Text) where n.name = \"" + value.get("name").asString() + "\"  set n.text = \"" + cleanedText + "\" RETURN n;";

                    System.out.println("Textupdate...." + exe);

                    try (Session session2 = driver.session()) {
                        session2.writeTransaction(tx -> tx.run(exe));
                    }

                }
            }

        }

    }



    public void executeGetTextValue() {
        try (Session session = driver.session()) {

            Result result = session.run("MATCH (n:Text) where n.DisplayName starts with 'M' and n.TextType = 'CodexLetter' RETURN n;");

        /*Result result = session.run("MATCH (n:Regesta) WHERE n.identifier STARTS WITH 'RI II,4' " +
                "AND n.isoStartDate > date('1002-01-01') AND n.isoEndDate < date('1021-12-31') AND n.latLong IS NOT NULL RETURN n;");*/


            while (result.hasNext()) {
                Record tempR = result.next();

                List<Pair<String, Value>> values = tempR.fields();
                for (Pair<String, Value> nameValue : values) {
                    Value value = nameValue.value();

                    System.out.println(value.get("Value").asString());

                    System.out.println(value.get("DisplayName").asString());

                    PrintWriter pWriter = null;
                    try {
                        //Todo name setzen...
                        String briefName = value.get("DisplayName").asString();
                        pWriter = new PrintWriter(new BufferedWriter(new FileWriter("C://Markus//ConverterV2//src//data//" + briefName + ".txt")));
                        //value.get("Value").asString().replaceAll("( )+", " ");
                        pWriter.println(value.get("Value").asString().replaceAll("\\s{2,}", " ").trim());
                    //    pWriter.println(value.get("Value").asString().trim());
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    } finally {
                        if (pWriter != null) {
                            pWriter.flush();
                            pWriter.close();
                        }
                    }

                }
            }

        }


    }


    public void executeWithoutReturn(String query) {
        try (Session session = driver.session()) {
            String greeting = session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    Result result = tx.run(query);
                    return result.single().get(0).asString();
                }
            });
            System.out.println(greeting);
        }
    }

    public void executeAddText(String Guid, String TextValue, String Name, String TextType, String DataAddedUTC) {

        //Eigenen Typ eingeführt....
        // TextType = "CodexLetter";
        System.out.println(TextType);
        String exe = "MERGE (e:Text { Guid: \"" + Guid + "\" }) " +
                "ON CREATE SET e = {CurrentVersion: 0,   Value: \"" + TextValue + "\",  UserGuid: \"" + UserUuid + "\",  TextType: \"" + TextType + "\", " +
                "Name: \"" + Name + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataAddedUTC + "\",  IsDeleted: false,  DisplayName: \"" + Name + "\",  Guid: \"" + Guid + "\"}";

        System.out.println("Text add...." + exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }


    public void executeLinkTextToSectionM(String TextUid, String RelationUid) {

        //Codex Section ID...
        //  SectionUid = "f5f6deaa-8356-4c97-931a-14e0b02f08b2";

        String exe = "MATCH (x:Text { Guid: \"" + TextUid + "\" }), " +
                "(y:Section { Guid: \"" + CodexSectionM + "\" }) " +
                "MERGE (x)-[r:text_in_section {Guid: \"" + RelationUid + "\"}]->(y)";
        // "CREATE UNIQUE (x)-[r:text_in_section {Guid: \""+RelationUid+"\"}]->(y)";

   /*     String exe =  "MATCH (x:Text { Guid: \""+TextUid+"\" }), " +
                "(y:Section { Guid: \""+SectionUid+"\" }) " +
                " UNIQUE (x)-[r:text_in_section {Guid: \""+RelationUid+"\"}]->(y)";*/

        System.out.println("Section add...." + exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }

    public void executeLinkTextToSectionW(String TextUid, String RelationUid) {

        //Codex Section ID...
        //  SectionUid = "f5f6deaa-8356-4c97-931a-14e0b02f08b2";

        String exe = "MATCH (x:Text { Guid: \"" + TextUid + "\" }), " +
                "(y:Section { Guid: \"" + CodexSectionW + "\" }) " +
                "MERGE (x)-[r:text_in_section {Guid: \"" + RelationUid + "\"}]->(y)";
        // "CREATE UNIQUE (x)-[r:text_in_section {Guid: \""+RelationUid+"\"}]->(y)";

   /*     String exe =  "MATCH (x:Text { Guid: \""+TextUid+"\" }), " +
                "(y:Section { Guid: \""+SectionUid+"\" }) " +
                " UNIQUE (x)-[r:text_in_section {Guid: \""+RelationUid+"\"}]->(y)";*/

        System.out.println("Section add...." + exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }

    public void executeLinkTextToSectionWR(String TextUid, String RelationUid) {

        //Codex Section ID...
        //  SectionUid = "f5f6deaa-8356-4c97-931a-14e0b02f08b2";

        String exe = "MATCH (x:Text { Guid: \"" + TextUid + "\" }), " +
                "(y:Section { Guid: \"" + CodexSectionWR + "\" }) " +
                "MERGE (x)-[r:text_in_section {Guid: \"" + RelationUid + "\"}]->(y)";
        // "CREATE UNIQUE (x)-[r:text_in_section {Guid: \""+RelationUid+"\"}]->(y)";

   /*     String exe =  "MATCH (x:Text { Guid: \""+TextUid+"\" }), " +
                "(y:Section { Guid: \""+SectionUid+"\" }) " +
                " UNIQUE (x)-[r:text_in_section {Guid: \""+RelationUid+"\"}]->(y)";*/

        System.out.println("Section add...." + exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }

    public void executeLinkTextToSectionZ(String TextUid, String RelationUid) {

        //Codex Section ID...
        //  SectionUid = "f5f6deaa-8356-4c97-931a-14e0b02f08b2";

        String exe = "MATCH (x:Text { Guid: \"" + TextUid + "\" }), " +
                "(y:Section { Guid: \"" + CodexSectionZ + "\" }) " +
                "MERGE (x)-[r:text_in_section {Guid: \"" + RelationUid + "\"}]->(y)";
        // "CREATE UNIQUE (x)-[r:text_in_section {Guid: \""+RelationUid+"\"}]->(y)";

   /*     String exe =  "MATCH (x:Text { Guid: \""+TextUid+"\" }), " +
                "(y:Section { Guid: \""+SectionUid+"\" }) " +
                " UNIQUE (x)-[r:text_in_section {Guid: \""+RelationUid+"\"}]->(y)";*/

        System.out.println("Section add...." + exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }

    public void executeLinkTextToSectionB(String TextUid, String RelationUid) {

        //Codex Section ID...
        //  SectionUid = "f5f6deaa-8356-4c97-931a-14e0b02f08b2";

        String exe = "MATCH (x:Text { Guid: \"" + TextUid + "\" }), " +
                "(y:Section { Guid: \"" + CodexSectionB + "\" }) " +
                "MERGE (x)-[r:text_in_section {Guid: \"" + RelationUid + "\"}]->(y)";
        // "CREATE UNIQUE (x)-[r:text_in_section {Guid: \""+RelationUid+"\"}]->(y)";

   /*     String exe =  "MATCH (x:Text { Guid: \""+TextUid+"\" }), " +
                "(y:Section { Guid: \""+SectionUid+"\" }) " +
                " UNIQUE (x)-[r:text_in_section {Guid: \""+RelationUid+"\"}]->(y)";*/

        System.out.println("Section add...." + exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }


    public void executeLinkTextToSection(String TextUid, String RelationUid) {

        //Codex Section ID...
        //  SectionUid = "f5f6deaa-8356-4c97-931a-14e0b02f08b2";

        String exe = "MATCH (x:Text { Guid: \"" + TextUid + "\" }), " +
                "(y:Section { Guid: \"" + CodexSection + "\" }) " +
                "MERGE (x)-[r:text_in_section {Guid: \"" + RelationUid + "\"}]->(y)";
        // "CREATE UNIQUE (x)-[r:text_in_section {Guid: \""+RelationUid+"\"}]->(y)";

   /*     String exe =  "MATCH (x:Text { Guid: \""+TextUid+"\" }), " +
                "(y:Section { Guid: \""+SectionUid+"\" }) " +
                " UNIQUE (x)-[r:text_in_section {Guid: \""+RelationUid+"\"}]->(y)";*/

        System.out.println("Section add...." + exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }




    public void executeAddMetaPropertyWithoutCommunication(String PropertyUuid, String PropertyType, String DataAddedUTC, String DataModifiedUTC,
                                                           String FolStart, String FolEnd, String Absender, String Empfaenger, String Inscriptio,
                                                           String Incipit, String Explicit, String Absender2, String Empfaenger2
            , String Absender1Ort, String Absender1Rolle, String Absender2Ort, String Absender2Rolle,
                                                           String Empfaenger1Ort, String Empfaenger1Rolle, String Empfaenger2Ort, String Empfaenger2Rolle) {


        String exe = "MERGE (e:PropertyMeta { Guid: \"" + PropertyUuid + "\" }) " +
                "ON CREATE SET e = {Type: \"" + PropertyType + "\", UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataModifiedUTC + "\", " +
                "Guid: \"" + PropertyUuid + "\"," + " FolioStart: \"" + FolStart + "\", FolioEnd: \"" + FolEnd + "\",Inscriptio: \"" + Inscriptio + "\", Incipit: \"" + Incipit + "\"," +
                "UserGuid: \"" + UserUuid + "\", IsDeleted: false, Explicit: \"" + Explicit + "\", " +
                "Absender1Place: \"" + Absender1Ort + "\", Absender1Role: \"" + Absender1Rolle + "\"," +
                "Absender2Place: \"" + Absender2Ort + "\", Absender2Role: \"" + Absender2Rolle + "\"," +
                "Receiver1Place: \"" + Empfaenger1Ort + "\", Receiver1Role: \"" + Empfaenger1Rolle + "\"," +
                "Receiver2Place: \"" + Empfaenger2Ort + "\", Receiver2Role: \"" + Empfaenger2Rolle + "\" } ";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

    }


    public void executeAddMetaProperty(String PropertyUuid, String PropertyType, String DataAddedUTC, String DataModifiedUTC,
                                       String FolStart, String FolEnd, String Absender, String Empfaenger, String Inscriptio,
                                       String Incipit, String Explicit, String Absender2, String Empfaenger2
            , String Absender1Ort, String Absender1Rolle, String Absender2Ort, String Absender2Rolle,
                                       String Empfaenger1Ort, String Empfaenger1Rolle, String Empfaenger2Ort, String Empfaenger2Rolle) {


        String exe = "MERGE (e:PropertyMeta { Guid: \"" + PropertyUuid + "\" }) " +
                "ON CREATE SET e = {Type: \"" + PropertyType + "\", UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataModifiedUTC + "\", " +
                "Guid: \"" + PropertyUuid + "\"," + " FolioStart: \"" + FolStart + "\", FolioEnd: \"" + FolEnd + "\",Inscriptio: \"" + Inscriptio + "\", Incipit: \"" + Incipit + "\"," +
                "UserGuid: \"" + UserUuid + "\", IsDeleted: false, Explicit: \"" + Explicit + "\", " +
                "Absender1Place: \"" + Absender1Ort + "\", Absender1Role: \"" + Absender1Rolle + "\"," +
                "Absender2Place: \"" + Absender2Ort + "\", Absender2Role: \"" + Absender2Rolle + "\"," +
                "Receiver1Place: \"" + Empfaenger1Ort + "\", Receiver1Role: \"" + Empfaenger1Rolle + "\"," +
                "Receiver2Place: \"" + Empfaenger2Ort + "\", Receiver2Role: \"" + Empfaenger2Rolle + "\" } ";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

        createCommunicationForMetaSend(PropertyUuid, Absender, Absender2);
        createCommunicationForMetaReceive(PropertyUuid, Empfaenger, Empfaenger2);

    }

    private void createCommunicationForMetaReceive(String PropertyUuid, String Empfaenger, String Empfaenger2) {
        //create new id
        String uuidCommuicationReceiveKnoten = java.util.UUID.randomUUID().toString();
        String DataAddedUTC = Instant.now().toString().replace("00Z", "+00:00");
        String type = "Receive";

        String exe = "MERGE (e:Communication { Guid: \"" + uuidCommuicationReceiveKnoten + "\" }) " +
                "ON CREATE SET e = {UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataAddedUTC + "\", " +
                "Guid: \"" + uuidCommuicationReceiveKnoten + "\",IsDeleted: false, CommunicationType: \"" + type + "\", UserGuid: \"" + UserUuid + "\"} ";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

        //relation von meta knoten zu communication
        createLinkMetaToReceiveKnoten(PropertyUuid, uuidCommuicationReceiveKnoten);

        //relation von Akteur zu communication
        createLinkAkteurToCommunicationReceive(Empfaenger, Empfaenger2, uuidCommuicationReceiveKnoten);
    }

    private void createLinkAkteurToCommunicationReceive(String Empfaenger, String Empfaenger2, String uuidCommuicationReceiveKnoten) {

        String uuidAkteurToCommunication = java.util.UUID.randomUUID().toString();

        String exe = "MATCH (x:Communication { Guid: \"" + uuidCommuicationReceiveKnoten + "\" }), " +
                "(y:Entity { Guid: \"" + Empfaenger + "\" }) " +
                "MERGE (x)-[r:has_receiver {Guid: \"" + uuidAkteurToCommunication + "\"}]->(y)";
        //  "CREATE UNIQUE (x)-[r:has_receiver {Guid: \""+uuidAkteurToCommunication+"\"}]->(y)";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

        if (Empfaenger2 != null) {
            String uuidAkteurToCommunication2 = java.util.UUID.randomUUID().toString();

            String exe2 = "MATCH (x:Communication { Guid: \"" + uuidCommuicationReceiveKnoten + "\" }), " +
                    "(y:Entity { Guid: \"" + Empfaenger2 + "\" }) " +
                    "MERGE (x)-[r:has_receiver {Guid: \"" + uuidAkteurToCommunication2 + "\"}]->(y)";
            // "CREATE UNIQUE (x)-[r:has_receiver {Guid: \""+uuidAkteurToCommunication2+"\"}]->(y)";

            System.out.println(exe2);
            try (Session session = driver.session()) {
                session.writeTransaction(tx -> tx.run(exe2));
            }
        }

    }

    private void createLinkMetaToReceiveKnoten(String propertyUuid, String uuidCommuicationReceiveKnoten) {

        String uuidCommuicationReceivedLink = java.util.UUID.randomUUID().toString();

        String exe = "MATCH (x:PropertyMeta { Guid: \"" + propertyUuid + "\" }), " +
                "(y:Communication { Guid: \"" + uuidCommuicationReceiveKnoten + "\" }) " +
                "MERGE (x)-[r:communication_received {Guid: \"" + uuidCommuicationReceivedLink + "\"}]->(y)";
        // "CREATE UNIQUE (x)-[r:communication_received {Guid: \""+uuidCommuicationReceivedLink+"\"}]->(y)";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

    }

    private void createCommunicationForMetaSend(String PropertyUuid, String Absender, String Absender2) {

        String uuidCommuicationSendKnoten = java.util.UUID.randomUUID().toString();
        String DataAddedUTC = Instant.now().toString().replace("00Z", "+00:00");
        String type = "Send";

        String exe = "MERGE (e:Communication { Guid: \"" + uuidCommuicationSendKnoten + "\" }) " +
                "ON CREATE SET e = {UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataAddedUTC + "\", " +
                "Guid: \"" + uuidCommuicationSendKnoten + "\",IsDeleted: false, CommunicationType: \"" + type + "\", UserGuid: \"" + UserUuid + "\"} ";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }


        createLinkMetaToSendKnoten(PropertyUuid, uuidCommuicationSendKnoten);


        createLinkAkteurToCommunicationSend(Absender, Absender2, uuidCommuicationSendKnoten);

    }

    private void createLinkAkteurToCommunicationSend(String Absender, String Absender2, String uuidCommuicationSendKnoten) {

        String uuidAkteurToCommunication = java.util.UUID.randomUUID().toString();

        String exe = "MATCH (x:Communication { Guid: \"" + uuidCommuicationSendKnoten + "\" }), " +
                "(y:Entity { Guid: \"" + Absender + "\" }) " +
                "MERGE (x)-[r:has_sender {Guid: \"" + uuidAkteurToCommunication + "\"}]->(y)";
        //  "CREATE UNIQUE (x)-[r:has_sender {Guid: \""+uuidAkteurToCommunication+"\"}]->(y)";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

        if (Absender2 != null) {
            String uuidAkteurToCommunication2 = java.util.UUID.randomUUID().toString();
            String exe2 = "MATCH (x:Communication { Guid: \"" + uuidCommuicationSendKnoten + "\" }), " +
                    "(y:Entity { Guid: \"" + Absender2 + "\" }) " +
                    "MERGE (x)-[r:has_sender {Guid: \"" + uuidAkteurToCommunication2 + "\"}]->(y)";
            // "CREATE UNIQUE (x)-[r:has_sender {Guid: \""+uuidAkteurToCommunication2+"\"}]->(y)";

            System.out.println(exe2);
            try (Session session = driver.session()) {
                session.writeTransaction(tx -> tx.run(exe2));
            }
        }


    }

    private void createLinkMetaToSendKnoten(String propertyUuid, String uuidCommuicationSendKnoten) {

        String uuidCommuicationSendLink = java.util.UUID.randomUUID().toString();

        String exe = "MATCH (x:PropertyMeta { Guid: \"" + propertyUuid + "\" }), " +
                "(y:Communication { Guid: \"" + uuidCommuicationSendKnoten + "\" }) " +
                "MERGE (x)-[r:communication_sent {Guid: \"" + uuidCommuicationSendLink + "\"}]->(y)";
        // "CREATE UNIQUE (x)-[r:communication_sent {Guid: \""+uuidCommuicationSendLink+"\"}]->(y)";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

    }

    public void executeLinkPropertyToTextMeta(String TextUid, String RelationUid, String PropertyUid) {

        String exe = "MATCH (x:Text { Guid: \"" + TextUid + "\" }), " +
                "(y:PropertyMeta { Guid: \"" + PropertyUid + "\" }) " +
                "MERGE (x)-[r:text_has_property_meta {Guid: \"" + RelationUid + "\"}]->(y)";
        //"CREATE UNIQUE (x)-[r:text_has_property_meta {Guid: \""+RelationUid+"\"}]->(y)";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

    }


    public void executeLinkPropertyToText(String TextUid, String RelationUid, String PropertyUid) {

        String exe = "MATCH (x:Text { Guid: \"" + TextUid + "\" }), " +
                "(y:StandoffProperty { Guid: \"" + PropertyUid + "\" }) " +
                "MERGE(x)-[r:text_has_standoff_property {Guid: \"" + RelationUid + "\"}]->(y)";
        // "CREATE UNIQUE (x)-[r:text_has_standoff_property {Guid: \""+RelationUid+"\"}]->(y)";


        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }


    public void createRelationSameAs(String PersonUid, String RelationUid, String NormdataId) {

        String exe = "MATCH (x:Entity { Guid: \"" + PersonUid + "\" }), " +
                "(y:Normdata { Guid: \"" + NormdataId + "\" }) " +
                "MERGE (x)-[r:same_as {Guid: \"" + RelationUid + "\"}]->(y)";
        //"CREATE UNIQUE (x)-[r:same_as {Guid: \""+RelationUid+"\"}]->(y)";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

    }

    public void createRelationSameAsActor(String Uiud, String RelationUid, String NormdataId) {

        String exe = "MATCH (x:ActorRole { Guid: \"" + Uiud + "\" }), " +
                "(y:Normdata { Guid: \"" + NormdataId + "\" }) " +
                "MERGE (x)-[r:same_as {Guid: \"" + RelationUid + "\"}]->(y)";
        // "CREATE UNIQUE (x)-[r:same_as {Guid: \""+RelationUid+"\"}]->(y)";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

    }


    public void createRelationIsFromProvider(String NormdataId, String RelationUid) {

        String exe = "MATCH (x:Normdata { Guid: \"" + NormdataId + "\" }), " +
                "(y:WikidataProvider { Guid: \"" + WikidataProvider + "\" }) " +
                "MERGE (x)-[r:is_from_provider {Guid: \"" + RelationUid + "\"}]->(y)";
        // "CREATE UNIQUE (x)-[r:is_from_provider {Guid: \""+RelationUid+"\"}]->(y)";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

    }

    public void createNormadataForPerson(String DataAddedUTC, String PersonUuid, String WikiId) {

        String uuidKnotenNormData = java.util.UUID.randomUUID().toString();


        String exe = "MERGE (e:Normdata { Guid: \"" + uuidKnotenNormData + "\" }) " +
                "ON CREATE SET e = { UserGuid: \"" + UserUuid + "\",  DateAddedUTC: \"" + DataAddedUTC + "\", Value: \"" + WikiId + "\",  " + "IsDeleted: false,  " + "Guid: \"" + uuidKnotenNormData + "\"}";

        System.out.println(exe);

        //create Knoten Normdata --> 2 Links ziehen

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

        //Dann Kante mit same-as
        String uuidRelationSameAs = java.util.UUID.randomUUID().toString();
        createRelationSameAs(PersonUuid, uuidRelationSameAs, uuidKnotenNormData);

        //Wenn Value == null ---> dann kein Link ziehen....
        //dann Kante is_from_provider
        if (WikiId == null) {
            System.out.println(WikiId);
        } else {
            String uuidRelationIsFromProvider = java.util.UUID.randomUUID().toString();
            createRelationIsFromProvider(uuidKnotenNormData, uuidRelationIsFromProvider);
        }

    }
    //

    public void executeAddPerson(String PersonGuid, String Description, String Name, String WikiId, String DataAddedUTC, String Type) {

        Description = "";

        String exe = "MERGE (e:Entity { Guid: \"" + PersonGuid + "\" }) " +
                "ON CREATE SET e = {  QuickAdd: true,  EntityType: \"Akteur\",  EntityDescription: \"" + Description + "\",  " +
                "AkteurType: \"" + Type + "\",  UserGuid: \"" + UserUuid + "\", Label: \"" + Name + "\", "
                + "DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataAddedUTC + "\",  " + "IsDeleted: false,  " + "Guid: \"" + PersonGuid + "\"}";


        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
        //create Knoten Normdata --> 2 Links ziehen
        createNormadataForPerson(DataAddedUTC, PersonGuid, WikiId);

    }

    public void executeAddActorRole(String RoleUuid, String Name, String WikiId, String DataAddedUTC) {
        String Description = "";

        String exe = "MERGE (e:AkteurRole { Guid: \"" + RoleUuid + "\" }) " +
                "ON CREATE SET e = {  QuickAdd: true,  EntityType: \"Role\",  Description: \"" + Description + "\",  Label: \"" + Name + "\", " +
                "UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataAddedUTC + "\",  " + "IsDeleted: false,  Guid: \"" + RoleUuid + "\"}";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

        createNormadataForActorRole(DataAddedUTC, RoleUuid, WikiId);

    }

    private void createNormadataForActorRole(String DataAddedUTC, String RoleUuid, String WikiId) {

        String uuidKnotenNormData = java.util.UUID.randomUUID().toString();


        String exe = "MERGE (e:Normdata { Guid: \"" + uuidKnotenNormData + "\" }) " +
                "ON CREATE SET e = { UserGuid: \"" + UserUuid + "\",  DateAddedUTC: \"" + DataAddedUTC + "\", Value: \"" + WikiId + "\",  " + "IsDeleted: false,  " + "Guid: \"" + uuidKnotenNormData + "\"}";

        System.out.println(exe);

        //create Knoten Normdata --> 2 Links ziehen

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

        //Dann Kante mit same-as
        String uuidRelationSameAs = java.util.UUID.randomUUID().toString();
        createRelationSameAsActor(RoleUuid, uuidRelationSameAs, uuidKnotenNormData);

        //Wenn Value == null ---> dann kein Link ziehen....
        //dann Kante is_from_provider
        if (WikiId == null) {
            System.out.println(WikiId);
        } else {
            String uuidRelationIsFromProvider = java.util.UUID.randomUUID().toString();
            createRelationIsFromProvider(uuidKnotenNormData, uuidRelationIsFromProvider);
        }

    }

    private void createNormadataForEvent(String DataAddedUTC, String EventUuid, String WikiId) {

        String uuidKnotenNormData = java.util.UUID.randomUUID().toString();


        String exe = "MERGE (e:Normdata { Guid: \"" + uuidKnotenNormData + "\" }) " +
                "ON CREATE SET e = { UserGuid: \"" + UserUuid + "\",  DateAddedUTC: \"" + DataAddedUTC + "\", Value: \"" + WikiId + "\",  " + "IsDeleted: false,  " + "Guid: \"" + uuidKnotenNormData + "\"}";

        System.out.println(exe);

        //create Knoten Normdata --> 2 Links ziehen

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

        //Dann Kante mit same-as
        String uuidRelationSameAs = java.util.UUID.randomUUID().toString();
        createRelationSameAs(EventUuid, uuidRelationSameAs, uuidKnotenNormData);

        //Wenn Value == null ---> dann kein Link ziehen....
        //dann Kante is_from_provider
        if (WikiId == null) {
            System.out.println(WikiId);
        } else {
            String uuidRelationIsFromProvider = java.util.UUID.randomUUID().toString();
            createRelationIsFromProvider(uuidKnotenNormData, uuidRelationIsFromProvider);
        }

    }


    public void executeAddEvent(String EventUuid, String Name, String WikiId, String DataAddedUTC, String StartEvent, String EndEvent, String Description) {
        Description = "";
        String exe = "MERGE (e:Entity { Guid: \"" + EventUuid + "\" }) " +
                "ON CREATE SET e = {  QuickAdd: true,  EntityType: \"Event\",  EntityDescription: \"" + Description + "\",  Label: \"" + Name + "\", " +
                "UserGuid: \"" + UserUuid + "\", StartEvent: \"" + StartEvent + "\", EndEvent: \"" + EndEvent + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataAddedUTC + "\",  " + "IsDeleted: false,  Guid: \"" + EventUuid + "\"}";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
        //create Knoten Normdata --> 2 Links ziehen
        createNormadataForEvent(DataAddedUTC, EventUuid, WikiId);

    }

    //DB.executeAddRef(Uuid, Name, WikiId, Instant.now().toString().replace("00Z", "+00:00"),Author,BibPrint,BibURL,ExternalLink);
    public void executeAddRef(String RefUuid, String Name, String WikiId, String DataAddedUTC, String Author, String BibPrint, String BibURL, String ExternalLink) {
        String Description = "";

        String exe = "MERGE (e:Entity { Guid: \"" + RefUuid + "\" }) " +
                "ON CREATE SET e = {  QuickAdd: true,  EntityType: \"Reference\", ExternalLink: \"" + ExternalLink + "\" , BibURL: \"" + BibURL + "\", " +
                "BibPrint: \"" + BibPrint + "\", Author: \"" + Author + "\", EntityDescription: \"" + Description + "\",  Label: \"" + Name + "\", " +
                "UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataAddedUTC + "\",  " + "IsDeleted: false,  Guid: \"" + RefUuid + "\"}";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
        //create Knoten Normdata --> 2 Links ziehen
        createNormadataForRef(DataAddedUTC, RefUuid, WikiId);

    }

    private void createNormadataForRef(String DataAddedUTC, String RefUuid, String WikiId) {

        String uuidKnotenNormData = java.util.UUID.randomUUID().toString();


        String exe = "MERGE (e:Normdata { Guid: \"" + uuidKnotenNormData + "\" }) " +
                "ON CREATE SET e = { UserGuid: \"" + UserUuid + "\",  DateAddedUTC: \"" + DataAddedUTC + "\", Value: \"" + WikiId + "\",  " + "IsDeleted: false,  " + "Guid: \"" + uuidKnotenNormData + "\"}";

        System.out.println(exe);

        //create Knoten Normdata --> 2 Links ziehen

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

        //Dann Kante mit same-as
        String uuidRelationSameAs = java.util.UUID.randomUUID().toString();
        createRelationSameAs(RefUuid, uuidRelationSameAs, uuidKnotenNormData);

        //Wenn Value == null ---> dann kein Link ziehen....
        //dann Kante is_from_provider
        if (WikiId == null) {
            System.out.println(WikiId);
        } else {
            String uuidRelationIsFromProvider = java.util.UUID.randomUUID().toString();
            createRelationIsFromProvider(uuidKnotenNormData, uuidRelationIsFromProvider);
        }

    }


    // DB.executeAddOrt(Uuid, Description, Name, WikiId, Instant.now().toString().replace("00Z", "+00:00"));
    public void executeAddOrt(String PlaceUuid, String Name, String WikiId, String DataAddedUTC) {
        String Description = "";
        System.out.println(WikiId);

        String exe = "MERGE (e:Entity { Guid: \"" + PlaceUuid + "\" }) " +
                "ON CREATE SET e = {  QuickAdd: true,  EntityType: \"Place\",  EntityDescription: \"" + Description + "\",  Label: \"" + Name + "\", " +
                "UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataAddedUTC + "\",  " + "IsDeleted: false,  Guid: \"" + PlaceUuid + "\"}";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
        //create Knoten Normdata --> 2 Links ziehen
        createNormadataForPlace(DataAddedUTC, PlaceUuid, WikiId);

    }

    public void createNormadataForPlace(String DataAddedUTC, String PlaceUuid, String WikiId) {

        String uuidKnotenNormData = java.util.UUID.randomUUID().toString();


        String exe = "MERGE (e:Normdata { Guid: \"" + uuidKnotenNormData + "\" }) " +
                "ON CREATE SET e = { UserGuid: \"" + UserUuid + "\",  DateAddedUTC: \"" + DataAddedUTC + "\", Value: \"" + WikiId + "\",  " + "IsDeleted: false,  " + "Guid: \"" + uuidKnotenNormData + "\"}";

        System.out.println(exe);

        //create Knoten Normdata --> 2 Links ziehen

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

        //Dann Kante mit same-as
        String uuidRelationSameAs = java.util.UUID.randomUUID().toString();
        createRelationSameAs(PlaceUuid, uuidRelationSameAs, uuidKnotenNormData);

        //Wenn Value == null ---> dann kein Link ziehen....
        //dann Kante is_from_provider
        if (WikiId == null) {
            System.out.println(WikiId);
        } else {
            String uuidRelationIsFromProvider = java.util.UUID.randomUUID().toString();
            createRelationIsFromProvider(uuidKnotenNormData, uuidRelationIsFromProvider);
        }

    }

    public List<Record> execute(String query) {
        try (Session session = driver.session()) {
            Result result = session.run(query);
            return result.list();
        }
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }


    public void exportDarmstadt(String textName) {

        JSONObject letter = new JSONObject();


        try (Session session = driver.session()) {

            Result result = session.run("match (b:Text)--(spo:StandoffProperty)\n" +
                    "        match (b:Text)--(pm:PropertyMeta)\n" +
                    "        where b.Name = \"" + textName + "\"\n" +
                    "        return b as text,  collect(spo) as standoffProperties, pm as propertyMeta;");


            //TextDaten
            JSONObject textProperties = new JSONObject();

            //MetaDaten
            JSONObject metaProperties = new JSONObject();

            //StandoffProperties
            JSONArray standoffProperties = new JSONArray();

            while (result.hasNext()) {
                Record tempR = result.next();

                List<Pair<String, Value>> values = tempR.fields();
                for (Pair<String, Value> nameValue : values) {

                    if ("text".equals(nameValue.key())) {
                        Value attributes = nameValue.value();
                        textProperties.put("Name", attributes.get("Name").asString());
                        textProperties.put("TextType", attributes.get("TextType").asString());
                        textProperties.put("Text", attributes.get("Value").asString());
                    }

                    if ("propertyMeta".equals(nameValue.key())) {
                        Value attributes = nameValue.value();

                        metaProperties.put("Explicit", attributes.get("Explicit").asString());
                        metaProperties.put("Incipit", attributes.get("Incipit").asString());
                        metaProperties.put("Inscriptio", attributes.get("Inscriptio").asString());
                        metaProperties.put("FolioStart", attributes.get("FolioStart").asString());
                        metaProperties.put("FolioEnd", attributes.get("FolioEnd").asString());

                    }

                    if ("standoffProperties".equals(nameValue.key())) {

                        Value list = nameValue.value();

                        for (int i = 0; i < list.size(); i++) {
                            HashMap<String, String> resultList = new HashMap<String, String>();
                            HashMap<String, String> resultListRecensi = new HashMap<String, String>();
                            Value attributes = list.get(i);
                            JSONObject property = new JSONObject();
                            Iterable<String> properties = attributes.keys();


                            if (!attributes.get("Type").asString().contains("aggregate") && !attributes.get("Type").asString().equals("leiden/comment") && attributes.get("IsDeleted").asBoolean() == false) {


                                property.put("StartIndex", attributes.get("StartIndex").toString().replaceAll("\"", ""));
                                property.put("EndIndex", attributes.get("EndIndex").toString().replaceAll("\"", ""));
                                property.put("Annotated Text", attributes.get("Text").asString());
                                property.put("Type", attributes.get("Type").asString());
                                property.put("ValueEntity", attributes.get("Value").asString());
                                property.put("Description", attributes.get("Description").asString());
                                property.put("Guid", attributes.get("Guid").asString());

                                if (attributes.get("Type").asString().equals("leiden/recensi")) {
                                    property.put("ValueEntity", "null");
                                    property.put("RecensiManuText", attributes.get("Value").asString());
                                }

                                if (attributes.get("Type").asString().equals("leiden/sic")) {
                                    property.put("CorrectedType", attributes.get("CorrectedType").asString());
                                    property.put("Corrected", attributes.get("Corrected").asString());
                                }

                                if (attributes.get("Type").asString().equals("reference")) {
                                    property.put("TextReference", attributes.get("TextReference").asString());
                                    property.put("Verbatim", attributes.get("Verbatim").asString());
                                }

                                if (attributes.get("Type").asString().equals("akteur")) {
                                    property.put("ValueEntity", attributes.get("Value").asString());
                                    resultList = checkRelationAkteur(attributes.get("Guid").asString());


                                    //----------------------------------------------------------------------------
                                    if (resultList.get("communicationRolle").equals("Sender"))
                                        property.put("Sender", true);
                                    else
                                        property.put("Sender", false);
                                    //----------------------------------------------------------------------------
                                    if (resultList.get("communicationRolle").equals("Empfänger"))
                                        property.put("Empfänger", true);
                                    else
                                        property.put("Empfänger", false);

                                    //----------------------------------------------------------------------------

                                    property.put("ValueRole", resultList.get("valueRole"));

                                    //----------------------------------------------------------------------------
                                    property.put("ValuePlace", resultList.get("valuePlace"));

                                    //----------------------------------------------------------------------------
                                    property.put("ConceptLabel", resultList.get("conceptLabel"));

                                    //----------------------------------------------------------------------------
                                    property.put("ConceptSubLabel", resultList.get("conceptSubLabel"));

                                }

                                standoffProperties.put(property);
                            }
                        }
                    }
                }
            }
            letter.put("textProperties", textProperties);
            letter.put("metaProperties", metaProperties);
            letter.put("standoffProperties", standoffProperties);

            PrintWriter pWriter = null;
            try {

                String briefName = "Json_Brief_" + textName;
                pWriter = new PrintWriter(new BufferedWriter(new FileWriter("C://Markus//ConverterV2//src//converter_data//" + briefName + ".txt")));
                pWriter.println(letter.toString());

            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                if (pWriter != null) {
                    pWriter.flush();
                    pWriter.close();
                }
            }

        }
    }


    private HashMap<String, String> checkRelationAkteur(String guid) {

        HashMap<String, String> resultList = new HashMap<String, String>();

        try (Session session = driver.session()) {

            Result result = session.run("match (spo:StandoffProperty)\n" +
                    "where spo.Guid = \"" + guid + "\"\n" +
                    "optional match (ar:ActorRole)<-[:refers_to_actorRole_communication]-(spo:StandoffProperty)\n" +
                    "optional match (ar2:ActorRole)<-[:refers_to_actorRole]-(spo:StandoffProperty)\n" +
                    "optional match (e:Entity)<-[:refers_to_place]-(spo:StandoffProperty)\n" +
                    "optional match (c:Concept)<-[:has_concept]-(spo:StandoffProperty)\n" +
                    "return spo as standoffProperty, ar.Label as communicationRolle, ar2.Guid as valueRole, e.Guid as valuePlace, c.Label as conceptLabel, c.SubType as conceptSubLabel, ar.Guid as communicationGuid;");


            while (result.hasNext()) {
                Record tempR = result.next();

                List<Pair<String, Value>> values = tempR.fields();
                for (Pair<String, Value> nameValue : values) {

                    if ("communicationRolle".equals(nameValue.key())) {
                        Value attributes = nameValue.value();
                        resultList.put("communicationRolle", attributes.asString());

                    }
                    if ("communicationGuid".equals(nameValue.key())) {
                        Value attributes = nameValue.value();
                        resultList.put("communicationGuid", attributes.asString());

                    }
                    if ("valueRole".equals(nameValue.key())) {
                        Value attributes = nameValue.value();
                        resultList.put("valueRole", attributes.asString());

                    }
                    if ("valuePlace".equals(nameValue.key())) {
                        Value attributes = nameValue.value();
                        resultList.put("valuePlace", attributes.asString());
                    }
                    if ("conceptLabel".equals(nameValue.key())) {
                        Value attributes = nameValue.value();
                        resultList.put("conceptLabel", attributes.asString());
                    }
                    if ("conceptSubLabel".equals(nameValue.key())) {
                        Value attributes = nameValue.value();
                        resultList.put("conceptSubLabel", attributes.asString());
                    }

                }
                return resultList;
            }
        }
        return resultList;
    }

    public void wdexecuteTokenizeLetters(String letterName) {

        ArrayList<String> letterNames = new ArrayList<>();
        ArrayList<String> ignoreList = new ArrayList<>();

        ignoreList.add("R2-328rb-1");
        ignoreList.add("R24-334v-1");
        ignoreList.add("R4-330rb-1");
        ignoreList.add("R4-330rb-2");
        ignoreList.add("R18-333v-1");
        ignoreList.add("R20-333v-1");
        ignoreList.add("R50-340vb-1");
        ignoreList.add("R58-343va-1");
        ignoreList.add("R58-343vb-2");
        ignoreList.add("R218-374va-2");
        ignoreList.add("R220-375ra-2");
        ignoreList.add("R220-375rb-1");
        ignoreList.add("R217-374rb-1");
        ignoreList.add("R217-374va-1");
        ignoreList.add("R241-381va-1");
        ignoreList.add("R8-331rb-1");
        ignoreList.add("R7-331ra-1");
        ignoreList.add("R242");
        ignoreList.add("R264");
        ignoreList.add("R268");
        ignoreList.add("R274");
        ignoreList.add("R280");
        ignoreList.add("R282");

        try (Session session = driver.session()) {

            Result result = session.run(
                    "match (b:Text)" +
                    "where b.Name starts with 'R'" +
                    "return b.Name");


            Value textAttributes = null;
            while (result.hasNext()) {
                Record tempR = result.next();

                List<Pair<String, Value>> values = tempR.fields();

                for (Pair<String, Value> nameValue : values) {

                    if ("b.Name".equals(nameValue.key())) {

                        if (!ignoreList.contains(nameValue.value().asString()))
                            letterNames.add(nameValue.value().asString());

                    }

                }
            }
        }




        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize");
        // example of how to customize the PTBTokenizer (these are just random example settings!!)
          props.setProperty("tokenize.options", "splitHyphenated=false,americanize=false");
        // build pipeline


        StringBuilder printStream = new StringBuilder();

     for(int i = 0; i<letterNames.size();i++)
     {

         //String letter = "Wr"+i;
         try (Session session = driver.session()) {

             Result result = session.run("match (b:Text)--(spo:StandoffProperty) " +
                     "match (b:Text)--(pm:PropertyMeta) " +
                     "where b.Name =  \"" + letterNames.get(i) + "\"" +
                     "return b as textProperty");


             String letterText = "";
             Value textAttributes = null;
             while (result.hasNext()) {
                 Record tempR = result.next();

                 List<Pair<String, Value>> values = tempR.fields();

                 for (Pair<String, Value> nameValue : values) {

                     if ("textProperty".equals(nameValue.key())) {

                         textAttributes = nameValue.value();
                         //hier brauchen müssen wir die attributes speichern
                         letterText = textAttributes.get("Value").toString().substring(1, textAttributes.get("Value").toString().length() - 1);
                     }

                 }
             }

             System.out.println("Text:  " + letterText);

          /*  StringTokenizer st = new StringTokenizer(letterText, " \t\n\r\f,.:;?![]'");
            while (st.hasMoreTokens()) {
                System.out.println(st.nextToken());

            }*/


         }
      }
     System.out.println(printStream);

        PrintWriter pWriter = null;
        try {

            pWriter = new PrintWriter(new BufferedWriter(new FileWriter("C://Markus//ConverterV2//src//data//TokenizedTexts.txt")));
            //value.get("Value").asString().replaceAll("( )+", " ");
            pWriter.println(printStream);
            //    pWriter.println(value.get("Value").asString().trim());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (pWriter != null) {
                pWriter.flush();
                pWriter.close();
            }
        }


    }

    public void textToHtml(String letterName) {

        ArrayList<Integer> offSets = new ArrayList<Integer>();
        ArrayList<Value> annotationList = new ArrayList<Value>();

        try (Session session = driver.session()) {

            Result result = session.run("match (b:Text)--(spo:StandoffProperty) " +
                    "match (b:Text)--(pm:PropertyMeta) " +
                    "where b.Name =  \"" + letterName + "\"" +
                    "WITH b,spo " +
                    "ORDER BY spo.StartIndex " +
                    "return b as textProperty, collect(spo) as standoffProperties");


            String letterText = "";
            Value textAttributes = null;
            String propertyGuuid = "";
            while (result.hasNext()) {
                Record tempR = result.next();

                List<Pair<String, Value>> values = tempR.fields();

                for (Pair<String, Value> nameValue : values) {

                    // Get Textnode
                    if ("textProperty".equals(nameValue.key())) {

                        textAttributes = nameValue.value();
                        //hier brauchen müssen wir die attributes speichern
                        letterText = textAttributes.get("Value").toString().substring(1, textAttributes.get("Value").toString().length() - 1).trim();
                        System.out.println(letterText);
                    }
                    if ("standoffProperties".equals(nameValue.key())) {

                        Value standoffProperties = nameValue.value();

                        for (int i = 0; i < standoffProperties.size(); i++) {
                            Value property = null;
                            property = standoffProperties.get(i);

                            if(!property.get("Type").asString().equals("entity/aggregate") && !property.get("Type").asString().equals("reference")
                            && !property.get("Type").asString().equals("event") && property.get("IsDeleted").asBoolean() != true
                                    && !property.get("Type").asString().equals("leiden/unlesbar")
                                    && !property.get("Type").asString().equals("leiden/comment"))
                            {
                                Integer endIndex = 0;

                                if(property.get("StartIndex").type().name().equals("INTEGER"))
                                    endIndex = property.get("StartIndex").asInt();
                                else
                                    endIndex = Integer.parseInt(property.get("StartIndex").asString());


                               if(property.get("Type").asString().equals("leiden/column") && endIndex == 0 )
                                   System.out.println("col");
                               else if(property.get("Type").asString().equals("leiden/line") && endIndex == 1 )
                                   System.out.println("line");
                               else {
                                   offSets.add(endIndex);
                                   annotationList.add(property);
                               }
                               // standofProperties.put(endIndex, property);
                            }
                        }
                    }

                }
            }

            System.out.println(standofProperties);
            //nun standoffproperties sortieren...
            System.out.println(standofProperties);
            Map<Integer, Value> map = new TreeMap<Integer, Value>();
            treeMap.putAll(standofProperties);


            Integer previousLineStart = 0;
            ArrayList<Value> deleteList = new ArrayList<>();
            //Map<Integer, Value> activeList = new TreeMap<Integer, Value>();
            ArrayList<Value> activListValues = new ArrayList<>();
            ArrayList<Value> activListValuesSorted = new ArrayList<>();

            htmlString.append("<p>");
            htmlString.append(tagStartCol);
            htmlString.append("column");
            htmlString.append(tagEnd);
            htmlString.append(tagStartLine);
            htmlString.append("line");
            htmlString.append(tagEnd);

            for(int j = 0; j< annotationList.size(); j++){
                Value property = annotationList.get(j);
                System.out.println(Integer.parseInt(property.get("StartIndex").asObject().toString())-1);
            }


            ArrayList<Integer> sortedValues = new ArrayList<>();
            for(int j = 0; j< annotationList.size(); j++){

                Value property = annotationList.get(j);

                int startIndexSp = 0;
                startIndexSp = Integer.parseInt(property.get("StartIndex").asObject().toString())-1;

                int endIndexSp = 0;
                endIndexSp = Integer.parseInt(property.get("EndIndex").asObject().toString());

                //TODO sort values....


                for(int m = 0;m< activListValues.size();m++){
                    sortedValues.add(Integer.parseInt(activListValues.get(m).get("EndIndex").asObject().toString()));
                }
                Collections.sort(sortedValues);
                System.out.println(sortedValues);

                Integer treffer = 1039;
                if(sortedValues.contains(treffer))
                    System.out.println("Treffer");

                int counter = 0;
                for(int m = 0;m< sortedValues.size();m++){

                    for(Value value : activListValues) {
                        if (sortedValues.get(m) == Integer.parseInt(value.get("EndIndex").asObject().toString())) {
                            if(!activListValuesSorted.contains(value)) {
                                int pos=sortedValues.indexOf(sortedValues.get(m));
                                activListValuesSorted.add(pos, value);
                            }
                        }
                    }

                }

                ArrayList<Integer> removeValues = new ArrayList<>();
                for(int m = 0;m< activListValuesSorted.size();m++)
                {

                    int startIndexAktivSp = 0;
                    startIndexAktivSp = Integer.parseInt(activListValuesSorted.get(m).get("StartIndex").asObject().toString());
                    int endIndexAktivSp = 0;
                    endIndexAktivSp = Integer.parseInt(activListValuesSorted.get(m).get("EndIndex").asObject().toString());

                    if(endIndexAktivSp<=startIndexSp)
                    {
                       // System.out.println("M: " + m +"  Text: " +letterText.substring(textPosition,endIndexAktivSp));
                        htmlString.append(letterText.substring(textPosition,endIndexAktivSp));
                        textPosition = endIndexAktivSp;
                        //TODO hier checken wegen nested.....
                        for(int k = 0;k< activListValuesSorted.size();k++)
                        {
                            int startIndexNestedSpA = 0;
                            int endIndexNestedSpA = 0;
                            endIndexNestedSpA = Integer.parseInt(activListValuesSorted.get(k).get("EndIndex").asObject().toString());
                            startIndexNestedSpA = Integer.parseInt(activListValuesSorted.get(k).get("StartIndex").asObject().toString());


                            if(startIndexNestedSpA>startIndexAktivSp && endIndexAktivSp<endIndexNestedSpA)
                            {
                               // System.out.println("Treffer");
                            }
                        }

                        closeTagAnnotations(activListValuesSorted.get(m).get("Type").asString(), false);

                       // System.out.println(htmlString);
                        sortedValues.remove(Integer.valueOf(endIndexAktivSp));
                        removeValues.add(endIndexAktivSp);
                        deleteList.add(activListValuesSorted.get(m));
                    }


                }

                //TODO aktivlist einträge löschen
                for(Integer id : removeValues)
                {
                 //   sortedValues.remove(id);
                }
                for(Value deleteProperty : deleteList)
                {
                    activListValuesSorted.remove(deleteProperty);

                }
                activListValues.clear();
                deleteList.clear();


                if(startIndexSp > textPosition)
                {
                    if(startIndexSp>textPosition)
                    {

                        if(letterText.substring(textPosition,startIndexSp).equals(" ")) {
                            System.out.println(letterText.substring(textPosition, startIndexSp));
                            htmlString.append(("&nbsp;"));
                        }
                        else
                            htmlString.append(letterText.substring(textPosition,startIndexSp));


                        System.out.println(htmlString);
                        textPosition = startIndexSp;
                    }
                }

                if(property.get("Type").asString().equals("leiden/line") || property.get("Type").asString().equals("leiden/column")) {
                    ArrayList<String> closeStartList = new ArrayList<String>();


                    for(int k = 0;k< activListValuesSorted.size();k++)
                    {
                        int startIndexNestedSpA = 0;
                        int endIndexNestedSpA = 0;
                        endIndexNestedSpA = Integer.parseInt(activListValuesSorted.get(k).get("EndIndex").asObject().toString());
                        startIndexNestedSpA = Integer.parseInt(activListValuesSorted.get(k).get("StartIndex").asObject().toString());

                       // if(startIndexNestedSpA>previousLineStart && endIndexNestedSpA>startIndexSp)
                        if(endIndexNestedSpA>startIndexSp)
                        {
                            closeTagAnnotations(activListValuesSorted.get(k).get("Type").asString(), false);
                            closeStartList.add(activListValuesSorted.get(k).get("Type").asString());
                        }
                    }
                    previousLineStart = startIndexSp;
                    //TODO hier checken...
                    if(textPosition > 0) {
                        boolean inline = checkInlineLine(letterText, textPosition);
                        closeTagAnnotations(property.get("Type").asString(), inline);
                    }
                    for(String type : closeStartList)
                    {
                        startTagAnnotations(type);
                    }
                    closeStartList.clear();
                    System.out.println(htmlString.toString());
                }
                else {
                    ArrayList<Integer> sortedValuesStart = new ArrayList<>();
                    sortedValuesStart.add(Integer.parseInt(property.get("EndIndex").asObject().toString()));
                    activListValues.add(property);

                    for (int l = j; l < annotationList.size(); l++) {
                        int startIndexNextSp = 0;

                        if (l + 1 < offSets.size()) {
                            Value nextProperty = annotationList.get(l + 1);
                            startIndexNextSp = Integer.parseInt(nextProperty.get("StartIndex").asObject().toString()) - 1;
                            if (startIndexSp == startIndexNextSp) {
                                if (!nextProperty.get("Type").asString().equals("leiden/line") && !nextProperty.get("Type").asString().equals("leiden/column"))
                                    sortedValuesStart.add(Integer.parseInt(nextProperty.get("EndIndex").asObject().toString()));

                                activListValues.add(nextProperty);
                                annotationList.remove(l + 1);
                                offSets.remove(l + 1);
                            } else {
                                break;
                            }
                        }
                    }

                   // for(int m = 0;m< activListValues.size();m++){
                    //    sortedValuesStart.add(Integer.parseInt(activListValues.get(m).get("EndIndex").asObject().toString()));
                   // }

                    Collections.sort(sortedValuesStart, Collections.reverseOrder());
                    System.out.println(sortedValuesStart);
                    for(int m = 0;m< sortedValuesStart.size();m++){
                        for(Value value : activListValues) {
                            if (sortedValuesStart.get(m) == Integer.parseInt(value.get("EndIndex").asObject().toString())) {
                                startTagAnnotations(value.get("Type").asString());
                                System.out.println(htmlString.toString());
                            }
                        }

                    }
                }

            }

            for(Value aktivProperty : activListValues)
            {

                int endIndexAktivSp = 0;
                endIndexAktivSp = Integer.parseInt(aktivProperty.get("EndIndex").asObject().toString());

                htmlString.append(letterText.substring(textPosition,endIndexAktivSp));
                closeTagAnnotations(aktivProperty.get("Type").asString(), false);
                textPosition = endIndexAktivSp;

            }
          //
          //  String textToCheck = htmlString.substring(htmlString.length()-20,htmlString.length());

           // if(!textToCheck.contains("</u>")) {

            if(textPosition<letterText.length())
                htmlString.append(letterText.substring(textPosition,letterText.length()));

                htmlString.append("/");
                htmlString.append("</span>");
           // }

          //  if(!textToCheck.contains("</u>")) {
                htmlString.append("|");
                htmlString.append("</span>");

           // }


            htmlString.append("</p>");
            System.out.println(htmlString.toString());
        }
    }

    private boolean checkInlineLine(String letterText, Integer textPosition) {

        if(!letterText.substring(textPosition, textPosition+1).equals(" ") && !letterText.substring(textPosition-1, textPosition).equals(" "))
            return true;
        else
            return false;

    }

    private void closeTagAnnotations(String type, boolean inline) {

        if(type.equals("leiden/column"))
        {
            htmlString.append("|");
            htmlString.append("</span>");
            htmlString.append("<br>");

            htmlString.append(tagStartCol);
            htmlString.append("column");
            htmlString.append(tagEnd);

        }
        else if(type.equals("leiden/line"))
        {

            if(inline)
                htmlString.append("/");
            else
                htmlString.append(" /");

            htmlString.append("</span>");


            htmlString.append(tagStartLine);
            htmlString.append("line");
            htmlString.append(tagEnd);

        }
        else {
            htmlString.append("</u>");
        }

    }

    private void startTagAnnotations(String type) {


        if (type.equals("leiden/expansion")) {

            htmlString.append(tagStart);
            htmlString.append("expansion");
            htmlString.append(tagEnd);

        } else if (type.equals("leiden/emphasis")) {
            htmlString.append(tagStart);
            htmlString.append("emphasis");
            htmlString.append(tagEnd);

        }else if (type.equals("leiden/correction")) {
            htmlString.append(tagStart);
            htmlString.append("correction");
            htmlString.append(tagEnd);

        }
        else if (type.equals("leiden/supralineam")) {
            htmlString.append(tagStart);
            htmlString.append("supralineam");
            htmlString.append(tagEnd);

        } else if (type.equals("leiden/marginalia")) {
            htmlString.append(tagStart);
            htmlString.append("marginalia");
            htmlString.append(tagEnd);

        } else if (type.equals("leiden/strikedout")) {
            htmlString.append(tagStart);
            htmlString.append("strikedout");
            htmlString.append(tagEnd);

        } else if (type.equals("leiden/rewritten")) {
            htmlString.append(tagStart);
            htmlString.append("rewritten");
            htmlString.append(tagEnd);

        } else if (type.equals("leiden/repetition")) {
            htmlString.append(tagStart);
            htmlString.append("repetition");
            htmlString.append(tagEnd);

        } else if (type.equals("leiden/sic")) {
            htmlString.append(tagStart);
            htmlString.append("sic");
            htmlString.append(tagEnd);

        } else if (type.equals("leiden/recensi")) {
            htmlString.append(tagStart);
            htmlString.append("recensi");
            htmlString.append(tagEnd);

        } else if (type.equals("leiden/transposition")) {
            htmlString.append(tagStart);
            htmlString.append("transposition");
            htmlString.append(tagEnd);

        }
        else if (type.equals("leiden/titulus")) {
            htmlString.append(tagStart);
            htmlString.append("titulus");
            htmlString.append(tagEnd);

        }else if (type.equals("akteur")) {
            htmlString.append(tagStart);
            htmlString.append("akteur");
            htmlString.append(tagEnd);

        } else if (type.equals("place")) {
            htmlString.append(tagStart);
            htmlString.append("place");
            htmlString.append(tagEnd);

        } else if (type.equals("leiden/column")) {
            htmlString.append(tagStart);
            htmlString.append("column");
            htmlString.append(tagEnd);

        } else if (type.equals("leiden/line")) {
            htmlString.append(tagStart);
            htmlString.append("line");
            htmlString.append(tagEnd);

        }else if (type.equals("leiden/striked-out")) {
        htmlString.append(tagStart);
        htmlString.append("striked-out");
        htmlString.append(tagEnd);

    }
        else {
            System.out.println("nichts gefunden...für type:  " + type);
        }
    }

    public void splitTexts(String letterName) {

        //TODO get Text Node
        //TODO get PropertyMetaNode
        try (Session session = driver.session()) {

            Result result = session.run("match (b:Text)--(spo:StandoffProperty) " +
                    "match (b:Text)--(pm:PropertyMeta) " +
                    "where b.Name =  \"" + letterName + "\"" +
                    "return b as textProperty, b.TextType as textType, b.Value as text, b.DateModifiedUTC as dateModified, collect(spo) as standoffProperties, pm as propertyMeta");


            String letterText = "";
            Value textAttributes =null;
            String propertyGuuid ="";
            while (result.hasNext()) {
                Record tempR = result.next();

                List<Pair<String, Value>> values = tempR.fields();

                for (Pair<String, Value> nameValue : values) {

                    // Get MetaPropertyUuid
                    if ("propertyMeta".equals(nameValue.key())) {

                        Value attributes = nameValue.value();
                        //nachher relation zu dieser UUid
                        propertyGuuid = attributes.get("Guid").asString();
                    }
                    // Get Textnode
                    if ("textProperty".equals(nameValue.key())) {

                        textAttributes = nameValue.value();
                        //hier brauchen müssen wir die attributes speichern
                        letterText = textAttributes.get("Value").toString().substring(1,  textAttributes.get("Value").toString().length()-1);
                    }
                    if ("standoffProperties".equals(nameValue.key())) {

                        Value standoffProperties = nameValue.value();


                        for(int i = 0; i<standoffProperties.size(); i++) {
                            Value property = null;
                            property = standoffProperties.get(i);
                            Integer startIndex = 0;

                            if(!property.get("Type").asString().equals("entity/aggregate") && !property.get("Type").asString().equals("reference")
                                    && !property.get("Type").asString().equals("event") && !property.get("Type").asString().equals("leiden/comment")) {

                                if (property.get("EndIndex").type().name().equals("INTEGER"))
                                    startIndex = property.get("EndIndex").asInt();
                                else
                                    startIndex = Integer.parseInt(property.get("EndIndex").asString());

                                standofProperties.put(startIndex, property);
                            }


                        }
                    }

                }
            }

            //nun standoffproperties sortieren...
            System.out.println(standofProperties);
            Map<Integer, Value> map = new TreeMap<Integer, Value>();
            treeMap.putAll(standofProperties);
          //  printMap(treeMap);

            //TODO Search Suchen nach spalten.... bei der 4en print map speichern..... müssen schauen ob eine zeile noch gleich auf ist....
            int countSpalten = 0;
            ArrayList<Value> saveList = new ArrayList();
            int offSet = 0;
            int countLetter = 0;
            String uuidText="";
            String textValue="";
            String uuidRelationTextMeta ="";
            String uuidRelationTextSection ="";

            for(Map.Entry<Integer, Value> property : treeMap.entrySet()){

                //add to print/savelist...
                saveList.add(property.getValue());

                //check type und count...
                if(property.getValue().get("Type").asString().equals("leiden/column"))
                {
                    countSpalten++;
                    System.out.println(countSpalten);
                    if(countSpalten==2)
                    {
                        //TODO
                        // Check annotationlist for equal....notwendig?!?

                        countLetter++;

                        Integer startIndex = 0;

                        if (property.getValue().get("EndIndex").type().name().equals("INTEGER"))
                            startIndex = property.getValue().get("EndIndex").asInt()+1;
                        else
                            startIndex = Integer.parseInt(property.getValue().get("EndIndex").asString())+1;



                        textValue = letterText.substring(offSet, startIndex);

                        System.out.println(textValue);

                        uuidText = java.util.UUID.randomUUID().toString();
                       executeAddSplitText(textValue, textAttributes,letterName+"___"+countLetter, uuidText, "CodexLetter", Instant.now().toString().replace("00Z", "+00:00"));

                        //Todo create Link to PropertyMeta...
                        //public void executeLinkPropertyToTextMeta(String TextUid, String RelationUid, String PropertyUid) {
                        uuidRelationTextMeta = java.util.UUID.randomUUID().toString();
                        executeLinkPropertyToTextMeta(uuidText,uuidRelationTextMeta,propertyGuuid);

                        //(String TextUid, String RelationUid)
                        uuidRelationTextSection = java.util.UUID.randomUUID().toString();
                        executeLinkTextToSection(uuidText,uuidRelationTextSection);

                        //Todo create standoffProperties
                        // with Link...
                        for(Value standoffProperty :  saveList)
                        {
                            String relationUuid = java.util.UUID.randomUUID().toString();
                            Value letterFlag = standoffProperty.get("Attributes");
                            String flag = letterFlag.get(0).asString().split("\\|")[1];


                            Integer index = 0;
                            Integer start = 0;
                            Integer end = 0;


                            if (standoffProperty.get("Index").type().name().equals("INTEGER"))
                               index = standoffProperty.get("Index").asInt();
                            else
                                index = Integer.parseInt(standoffProperty.get("Index").asString());

                            if (standoffProperty.get("StartIndex").type().name().equals("INTEGER"))
                                start = standoffProperty.get("StartIndex").asInt();
                            else
                               start = Integer.parseInt(standoffProperty.get("StartIndex").asString());

                            if (standoffProperty.get("EndIndex").type().name().equals("INTEGER"))
                                end = standoffProperty.get("EndIndex").asInt();
                            else
                                end = Integer.parseInt(standoffProperty.get("EndIndex").asString());

                           // System.out.println(standoffProperty.get("Type").asString());
                        //    System.out.println(start);

                            if(start < 0)
                                start = 0;

                            executeAddPropertiesSplitText(relationUuid,
                                    index,
                                    standoffProperty.get("Text").asString(),
                                    standoffProperty.get("Type").asString(),
                                    start,
                                    end,
                                    standoffProperty.get("DateAddedUTC").asString(), standoffProperty.get("DateModifiedUTC").asString(),
                                    Boolean.parseBoolean(flag),  uuidText, offSet);



                        }



                        //TODO set new offset... checken... +1?

                        Integer newOff = 0;

                        if (property.getValue().get("EndIndex").type().name().equals("INTEGER"))
                            newOff = property.getValue().get("EndIndex").asInt()+1;
                        else
                            newOff = Integer.parseInt(property.getValue().get("EndIndex").asString())+1;

                        offSet = newOff;
                        saveList.clear();
                        countSpalten = 0;
                    }
                }


            }



            if(saveList.size()>0)
            {
                countLetter++;

                textValue = letterText.substring(offSet, textAttributes.get("Value").toString().length()-2);

                System.out.println(textValue);

                uuidText = java.util.UUID.randomUUID().toString();
                executeAddSplitText(textValue, textAttributes,letterName+"___"+countLetter, uuidText, "CodexLetter", Instant.now().toString().replace("00Z", "+00:00"));

                //Todo create Link to PropertyMeta...
                //public void executeLinkPropertyToTextMeta(String TextUid, String RelationUid, String PropertyUid) {
                uuidRelationTextMeta = java.util.UUID.randomUUID().toString();
                executeLinkPropertyToTextMeta(uuidText,uuidRelationTextMeta,propertyGuuid);

                //(String TextUid, String RelationUid)
                uuidRelationTextSection = java.util.UUID.randomUUID().toString();
                executeLinkTextToSection(uuidText,uuidRelationTextSection);

                //TODO save standoffproperties...
                for(Value standoffProperty :  saveList)
                {
                    String relationUuid = java.util.UUID.randomUUID().toString();
                    Value letterFlag = standoffProperty.get("Attributes");
                    String flag = letterFlag.get(0).asString().split("\\|")[1];

                    Integer index = 0;
                    Integer start = 0;
                    Integer end = 0;


                    if (standoffProperty.get("Index").type().name().equals("INTEGER"))
                        index = standoffProperty.get("Index").asInt();
                    else
                        index = Integer.parseInt(standoffProperty.get("Index").asString());

                    if (standoffProperty.get("StartIndex").type().name().equals("INTEGER"))
                        start = standoffProperty.get("StartIndex").asInt();
                    else
                        start = Integer.parseInt(standoffProperty.get("StartIndex").asString());

                    if (standoffProperty.get("EndIndex").type().name().equals("INTEGER"))
                        end = standoffProperty.get("EndIndex").asInt();
                    else
                        end = Integer.parseInt(standoffProperty.get("EndIndex").asString());

                    if(start < 0)
                        start = 0;

                            executeAddPropertiesSplitText(relationUuid,
                                    index,
                                    standoffProperty.get("Text").asString(),
                                    standoffProperty.get("Type").asString(),
                                    start,
                                    end,
                                    standoffProperty.get("DateAddedUTC").asString(), standoffProperty.get("DateModifiedUTC").asString(),
                                    Boolean.parseBoolean(flag),  uuidText, offSet);

                }

            }
        }


    }

    private void executeAddSplitText(String textValue, Value textAttributes, String textName, String uuidText, String textType, String dateAddedUTC) {

        System.out.println("Text add... textname..." + textName);
        System.out.println("Text add... uuid..." + uuidText);

        //Eigenen Typ eingeführt....
        // TextType = "CodexLetter";



        String exe = "MERGE (e:Text { Guid: \"" + uuidText + "\" }) " +
                "ON CREATE SET e = {CurrentVersion: 0,   Value: \""  + textValue + "\",  UserGuid: \"" + UserUuid + "\",  " +
                "TextType: \"" + textType + "\", " +
                "Name: \"" + textName + "\", DateAddedUTC: \"" + dateAddedUTC + "\", DateModifiedUTC: \"" + dateAddedUTC + "\",  " +
                "IsDeleted: false,  DisplayName: \"" + textName + "\",  Guid: \"" + uuidText + "\"}";

        System.out.println("Text add...." + exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }


    }

    public void executeLinkPropertyToTextSplitText(String TextUuid, String RelationUuid, String PropertyUuid) {


        String exe = "MATCH (x:Text { Guid: \"" + TextUuid + "\" }), " +
                "(y:StandoffProperty { Guid: \"" + PropertyUuid + "\" }) " +
                "MERGE(x)-[r:text_has_standoff_property {Guid: \"" + RelationUuid + "\"}]->(y)";


        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }




    public static <K, V> void printMap(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey()
                    + " Value : " + entry.getValue());
        }
    }

    Map<Integer, Value> treeMap = new TreeMap<Integer, Value>(
            new Comparator<Integer>() {

                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1.compareTo(o2);
                }

            });


    public void executeAddPropertiesSplitText(String propertyGuid, Integer IndexNr, String TextName, String PropertyType, Integer StartIndex, Integer EndIndex, String DataAddedUTC, String DataModifiedUTC, boolean letterFlag, String textUuid, Integer offSet) {

        System.out.println(letterFlag);

        EndIndex = EndIndex-offSet;
        StartIndex = StartIndex-offSet;

        if(StartIndex<0)
            StartIndex = 0;

        String exe = "MERGE (e:StandoffProperty { Guid: \"" + propertyGuid + "\" }) " +
                "ON CREATE SET e = { Index: \"" + IndexNr + "\", Type: \"" + PropertyType + "\", StartIndex: \"" + StartIndex + "\", EndIndex: \"" + EndIndex + "\", Text: \"" + TextName + "\",  IsZeroPoint: false,  Attributes:  [\"letterFlag|" + letterFlag + "\"], " +
                "UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataModifiedUTC + "\", IsDeleted: false, Guid: \"" + propertyGuid + "\"} " +
                "ON MATCH SET e = { Index: \"" + IndexNr + "\", Type: \"" + PropertyType + "\", StartIndex: \"" + StartIndex + "\", EndIndex: \"" + EndIndex + "\", Text: \"" + TextName + "\",  IsZeroPoint: false,  Attributes:  [\"letterFlag|" + letterFlag + "\"], " +
                "UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataModifiedUTC + "\", IsDeleted: false, Guid: \"" + propertyGuid + "\"}";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

        String relationUuid = java.util.UUID.randomUUID().toString();
        executeLinkPropertyToTextSplitText(textUuid, relationUuid, propertyGuid);

    }

    public void executeAddProperties(String Guid, Integer IndexNr, String TextName, String PropertyType, Integer StartIndex,
                                     Integer EndIndex, String DataAddedUTC, String DataModifiedUTC, boolean letterFlag) {

        System.out.println(letterFlag);

        String exe = "MERGE (e:StandoffProperty { Guid: \"" + Guid + "\" }) " +
                "ON CREATE SET e = { Index: \"" + IndexNr + "\", Type: \"" + PropertyType + "\", StartIndex: \"" + StartIndex + "\", EndIndex: \"" + EndIndex + "\", Text: \"" + TextName + "\",  IsZeroPoint: false,  Attributes:  [\"letterFlag|" + letterFlag + "\"], " +
                "UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataModifiedUTC + "\", IsDeleted: false, Guid: \"" + Guid + "\"} " +
                "ON MATCH SET e = { Index: \"" + IndexNr + "\", Type: \"" + PropertyType + "\", StartIndex: \"" + StartIndex + "\", EndIndex: \"" + EndIndex + "\", Text: \"" + TextName + "\",  IsZeroPoint: false,  Attributes:  [\"letterFlag|" + letterFlag + "\"], " +
                "UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataModifiedUTC + "\", IsDeleted: false, Guid: \"" + Guid + "\"}";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }

    public void executeAddPropertiesMergeTexts(String Guid, String TextName, String PropertyType, Integer StartIndex,
                                     Integer EndIndex, String DataAddedUTC, String DataModifiedUTC, boolean letterFlag) {

        System.out.println(letterFlag);

        String exe = "MERGE (e:StandoffProperty { Guid: \"" + Guid + "\" }) " +
                "ON CREATE SET e = { Type: \"" + PropertyType + "\", StartIndex: \"" + StartIndex + "\", EndIndex: \"" + EndIndex + "\", Text: \"" + TextName + "\",  IsZeroPoint: false,  Attributes:  [\"letterFlag|" + letterFlag + "\"], " +
                "UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataModifiedUTC + "\", IsDeleted: false, Guid: \"" + Guid + "\"} " +
                "ON MATCH SET e = { Type: \"" + PropertyType + "\", StartIndex: \"" + StartIndex + "\", EndIndex: \"" + EndIndex + "\", Text: \"" + TextName + "\",  IsZeroPoint: false,  Attributes:  [\"letterFlag|" + letterFlag + "\"], " +
                "UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataModifiedUTC + "\", IsDeleted: false, Guid: \"" + Guid + "\"}";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }

    public void executeAddPropertySic(String Guid, String TextName, String PropertyType, Integer StartIndex,
                                     Integer EndIndex, String DataAddedUTC, String DataModifiedUTC, boolean letterFlag, String CorrectedType, String Corrected) {

        System.out.println(letterFlag);

        String exe = "MERGE (e:StandoffProperty { Guid: \"" + Guid + "\" }) " +
                "ON CREATE SET e = {Type: \"" + PropertyType + "\", StartIndex: \"" + StartIndex + "\", EndIndex: \"" + EndIndex + "\", Text: \"" + TextName + "\",  IsZeroPoint: false,  Attributes:  [\"letterFlag|" + letterFlag + "\"], " +
                "UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataModifiedUTC + "\", IsDeleted: false, Guid: \"" + Guid + "\", Corrected: \"" + Corrected +"\", CorrectedType: \"" + CorrectedType +"\"} " +
                "ON MATCH SET e = { Type: \"" + PropertyType + "\", StartIndex: \"" + StartIndex + "\", EndIndex: \"" + EndIndex + "\", Text: \"" + TextName + "\",  IsZeroPoint: false,  Attributes:  [\"letterFlag|" + letterFlag + "\"], " +
                "UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataModifiedUTC + "\", IsDeleted: false, Guid: \"" + Guid + "\", Corrected: \"" + Corrected +"\", CorrectedType: \"" + CorrectedType +"\"}";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }


    public void executeAddPropertyRecensi(String Guid, String TextName, String PropertyType, Integer StartIndex,
                                      Integer EndIndex, String DataAddedUTC, String DataModifiedUTC, boolean letterFlag, String RecensiTextName, String AdditionalText, String Value) {

        System.out.println(letterFlag);

        String exe = "MERGE (e:StandoffProperty { Guid: \"" + Guid + "\" }) " +
                "ON CREATE SET e = {Type: \"" + PropertyType + "\", StartIndex: \"" + StartIndex + "\", EndIndex: \"" + EndIndex + "\", Text: \"" + TextName + "\",  IsZeroPoint: false,  Attributes:  [\"letterFlag|" + letterFlag + "\"], " +
                "UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataModifiedUTC + "\", IsDeleted: false, Guid: \"" + Guid + "\"} " +
                "ON MATCH SET e = {Type: \"" + PropertyType + "\", StartIndex: \"" + StartIndex + "\", EndIndex: \"" + EndIndex + "\", Text: \"" + TextName + "\",  IsZeroPoint: false,  Attributes:  [\"letterFlag|" + letterFlag + "\"], " +
                "UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataModifiedUTC + "\", IsDeleted: false, Guid: \"" + Guid + "\", " +
                "RecensiTextName: \"" + RecensiTextName + "\", AdditionalText: \"" + AdditionalText +"\",  Value: \"" + Value +"\"}";


        //TODo create textknoten
        String textRecensiUuid = java.util.UUID.randomUUID().toString();
        executeAddTextRecensi(textRecensiUuid, Value, RecensiTextName, "RecensiManuNote", Instant.now().toString().replace("00Z", "+00:00"));

        executeLinkPropertyToRecensi(Guid, textRecensiUuid);

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }


    }

    public void executeAddPropertyEntity(String Guid, String TextName, String PropertyType, Integer StartIndex,
                                          Integer EndIndex, String DataAddedUTC, String DataModifiedUTC, boolean letterFlag, String Value) {

        System.out.println(letterFlag);

        String exe = "MERGE (e:StandoffProperty { Guid: \"" + Guid + "\" }) " +
                "ON CREATE SET e = {Type: \"" + PropertyType + "\", StartIndex: \"" + StartIndex + "\", EndIndex: \"" + EndIndex + "\", Text: \"" + TextName + "\",  IsZeroPoint: false,  Attributes:  [\"letterFlag|" + letterFlag + "\"], " +
                "UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataModifiedUTC + "\", IsDeleted: false, Guid: \"" + Guid + "\"} " +
                "ON MATCH SET e = {Type: \"" + PropertyType + "\", StartIndex: \"" + StartIndex + "\", EndIndex: \"" + EndIndex + "\", Text: \"" + TextName + "\",  IsZeroPoint: false,  Attributes:  [\"letterFlag|" + letterFlag + "\"], " +
                "UserGuid: \"" + UserUuid + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataModifiedUTC + "\", IsDeleted: false, Guid: \"" + Guid + "\", Value: \"" + Value +"\"}";


        //Spo zu Entity

        executeLinkPropertyToEntity(Guid, Value);

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }


    }

    private void executeLinkPropertyToEntity(String guid, String value) {

        String relationUuid = java.util.UUID.randomUUID().toString();

        String exe = "MATCH (x:StandoffProperty { Guid: \"" + guid + "\" }), " +
                "(y:Entity { Guid: \"" + value + "\" }) " +
                "MERGE(x)-[r:standoff_property_refers_to_entity {Guid: \"" + relationUuid + "\"}]->(y)";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

    }


    public void executeAddTextRecensi(String Guid, String TextValue, String Name, String TextType, String DataAddedUTC) {

        //Eigenen Typ eingeführt....
        // TextType = "CodexLetter";
        System.out.println(TextType);
        String exe = "MERGE (e:Text { Guid: \"" + Guid + "\" }) " +
                "ON CREATE SET e = {CurrentVersion: 0,   Value: \"" + TextValue + "\",  UserGuid: \"" + UserUuid + "\",  TextType: \"" + TextType + "\", " +
                "Name: \"" + Name + "\", DateAddedUTC: \"" + DataAddedUTC + "\", DateModifiedUTC: \"" + DataAddedUTC + "\",  IsDeleted: false,  DisplayName: \"" + Name + "\",  Guid: \"" + Guid + "\"}";

        System.out.println("Text add...." + exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }

    public void executeLinkPropertyToPlace(String PropertyUuid, String PlaceUuid) {


        String relationUuid = java.util.UUID.randomUUID().toString();

        String exe = "MATCH (x:StandoffProperty { Guid: \"" + PropertyUuid + "\" }), " +
                "(y:Entity { Guid: \"" + PlaceUuid + "\" }) " +
                "MERGE(x)-[r:refers_to_place {Guid: \"" + relationUuid + "\"}]->(y)";


        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }


    public void executeLinkPropertyToRole(String PropertyUuid, String ActorRoleUuid) {


        String relationUuid = java.util.UUID.randomUUID().toString();

        String exe = "MATCH (x:StandoffProperty { Guid: \"" + PropertyUuid + "\" }), " +
                "(y:ActorRole { Guid: \"" + ActorRoleUuid + "\" }) " +
                "MERGE(x)-[r:refers_to_actorRole {Guid: \"" + relationUuid + "\"}]->(y)";


        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }


    public void executeLinkPropertyToCommunicationRole(String PropertyUuid, String ActorRoleUuid) {

        String relationUuid = java.util.UUID.randomUUID().toString();

        String exe = "MATCH (x:StandoffProperty { Guid: \"" + PropertyUuid + "\" }), " +
                "(y:ActorRole { Guid: \"" + ActorRoleUuid + "\" }) " +
                "MERGE(x)-[r:refers_to_actorRole_communication {Guid: \"" + relationUuid + "\"}]->(y)";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }


    public void executeLinkPropertyToRecensi(String PropertyUuid, String TextUuid) {


        String relationUuid = java.util.UUID.randomUUID().toString();

        String exe = "MATCH (x:StandoffProperty { Guid: \"" + PropertyUuid + "\" }), " +
                "(y:Text { Guid: \"" + TextUuid + "\" }) " +
                "MERGE(x)-[r:has_note {Guid: \"" + relationUuid + "\"}]->(y)";


        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }
    }


    public void mergeLongTexts(String letterName) {

        //TODO Brief uuid holen
        String textUuid = getLetterUuid(letterName);

        //TODO zunächst relations löschen, die uns hindern zu löschen....

        String exeC = "MATCH (n:Text )--(sp:StandoffProperty)-[r:refers_to_actorRole_communication]-()" +
                "where n.Name =  \"" + letterName + "\"" +
                "delete r";

        System.out.println(exeC);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exeC));
        }

        String exeCo = "MATCH (n:Text )--(sp:StandoffProperty)-[r:has_concept]-()" +
                "where n.Name =  \"" + letterName + "\"" +
                "delete r";

        System.out.println(exeCo);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exeCo));
        }

        String exeAr = "MATCH (n:Text )--(sp:StandoffProperty)-[r:refers_to_actorRole]-()" +
                "where n.Name =  \"" + letterName + "\"" +
                "delete r";

        System.out.println(exeAr);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exeAr));
        }

        String exeRe = "MATCH (n:Text )--(sp:StandoffProperty)-[r:standoff_property_refers_to_entity]-()" +
                "where n.Name =  \"" + letterName + "\"" +
                "delete r";

        System.out.println(exeRe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exeRe));
        }

        String exeP = "MATCH (n:Text )--(sp:StandoffProperty)-[r:refers_to_place]-()" +
                "where n.Name =  \"" + letterName + "\"" +
                "delete r";

        System.out.println(exeP);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exeP));
        }


        //TODO zunächst Brief suchen und Relations + SPs löschen

        String exe = "MATCH (n:Text )-[r:text_has_standoff_property]->(sp:StandoffProperty)" +
                "where n.Name =  \"" + letterName + "\"" +
                "delete r, sp";

        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }


        //TODO danach Briefname___ einsammeln....

        try (Session session = driver.session()) {


                Result result = session.run("match (b:Text)--(spo:StandoffProperty) " +
                        "where b.Name STARTS WITH  \"" + letterName + "_" + "\"" +
                        " return b as text,  collect(spo) as standoffProperties");

            System.out.println("hmm");

            //StandoffProperties
            int counter = 1;
            JSONArray standoffProperties = new JSONArray();
            Integer offSet = 0;
            String letterText = "";
            String textBlock = "";
            while (result.hasNext()) {
                Record tempR = result.next();

                List<Pair<String, Value>> values = tempR.fields();

                for (Pair<String, Value> nameValue : values) {

                    if ("text".equals(nameValue.key())) {

                        letterText = letterText + nameValue.value().get("Value").asString();
                        textBlock = nameValue.value().get("Value").asString();
                    }


                    if ("standoffProperties".equals(nameValue.key())) {
                        Value list = nameValue.value();

                        for (int i = 0; i < list.size(); i++) {
                            HashMap<String, String> resultList = new HashMap<String, String>();
                            HashMap<String, String> resultListRecensi = new HashMap<String, String>();
                            Value attributes = list.get(i);
                            JSONObject property = new JSONObject();
                            Iterable<String> properties = attributes.keys();


                            if (!attributes.get("Type").asString().contains("aggregate") && !attributes.get("Type").asString().equals("leiden/comment") && attributes.get("IsDeleted").asBoolean() == false) {

                                //TODO....
                                Integer start = 0;
                                Integer end = 0;

                                if (attributes.get("StartIndex").type().name().equals("INTEGER"))
                                    start = attributes.get("StartIndex").asInt();
                                else
                                    start = Integer.parseInt(attributes.get("StartIndex").asString());

                                if (attributes.get("EndIndex").type().name().equals("INTEGER"))
                                    end = attributes.get("EndIndex").asInt();
                                else
                                    end = Integer.parseInt(attributes.get("EndIndex").asString());

                                System.out.println("s: " +start);
                                start= start + offSet;
                                System.out.println("s: " +start);
                                System.out.println("e: "+ end);
                                end = end + offSet;
                                System.out.println("e: "+ end);

                                property.put("StartIndex", start);
                                property.put("EndIndex", end);
                                property.put("AnnotatedText", attributes.get("Text").asString());
                                property.put("Type", attributes.get("Type").asString());
                                property.put("ValueEntity", attributes.get("Value").asString());
                                property.put("Description", attributes.get("Description").asString());
                                property.put("Guid", attributes.get("Guid").asString());

                                if (attributes.get("Type").asString().equals("leiden/recensi")) {
                                    property.put("Value", attributes.get("Value").asString());
                                    property.put("RecensiTextName", attributes.get("RecensiTextName").asString());
                                    property.put("AdditionalText", attributes.get("Value").asString());
                                }

                                if (attributes.get("Type").asString().equals("leiden/sic")) {
                                    property.put("CorrectedType", attributes.get("CorrectedType").asString());
                                    property.put("Corrected", attributes.get("Corrected").asString());
                                }

                                if (attributes.get("Type").asString().equals("reference")) {
                                    property.put("TextReference", attributes.get("TextReference").asString());
                                    property.put("Verbatim", attributes.get("Verbatim").asString());
                                }
                                if (attributes.get("Type").asString().equals("place")) {
                                    property.put("Value", attributes.get("Value").asString());


                                }

                                if (attributes.get("Type").asString().equals("akteur")) {
                                    property.put("Value", attributes.get("Value").asString());
                                    resultList = checkRelationAkteur(attributes.get("Guid").asString());


                                    //----------------------------------------------------------------------------
                                    if (resultList.get("communicationRolle").equals("Sender"))
                                        property.put("Sender", true);
                                    else
                                        property.put("Sender", false);
                                    //----------------------------------------------------------------------------
                                    if (resultList.get("communicationRolle").equals("Empfänger"))
                                        property.put("Empfänger", true);
                                    else
                                        property.put("Empfänger", false);


                                    property.put("CommunicationRoleGuid", resultList.get("communicationGuid"));

                                    property.put("RoleGuid", resultList.get("valueRole"));

                                    //----------------------------------------------------------------------------
                                    property.put("PlaceGuid", resultList.get("valuePlace"));

                                    //----------------------------------------------------------------------------
                                    property.put("ConceptLabel", resultList.get("conceptLabel"));

                                    //----------------------------------------------------------------------------
                                    property.put("ConceptSubLabel", resultList.get("conceptSubLabel"));

                                }

                                standoffProperties.put(property);
                            }
                        }
                    }

                }

                System.out.println(counter);
                counter++;
                offSet = offSet+textBlock.length();
                System.out.println(offSet);

            }

            //TODO Text setzen....
            String exeSetText = "MATCH (n:Text )" +
                    "where n.Name =  \"" + letterName + "\"" +
                    "set n.Value = \"" + letterText + "\"" +
                    "return n";

            System.out.println(exe);

            try (Session sessionSetText = driver.session()) {
                sessionSetText.writeTransaction(tx -> tx.run(exeSetText));
            }

            for(int i=0;i<standoffProperties.length();i++)
            {
                JSONObject spo = (JSONObject) standoffProperties.get(i);
                System.out.println(spo.get("Type"));

                //uuid property
                String uuidProperty = UUID.randomUUID().toString();



                //TODO Sonderfälle: leiden/correction/sic?!?, leiden/recensi, place,akteur

                //TODO-ELSE bei diesen sppos muss nix gemacht werden: leiden/titulus, leiden/line, leiden/unlesbar, leiden/repetition, leiden/gap, leiden/emphasis,
                // leiden/rewritten, leiden/striked-out, leiden/marginalia, leiden/transposition, leiden/supralineam, leiden/column, leiden/expansion

                if(spo.get("Type").equals("leiden/sic"))
                {
                    executeAddPropertySic(uuidProperty,spo.get("AnnotatedText").toString() ,spo.get("Type").toString(),Integer.parseInt(spo.get("StartIndex").toString()),
                            Integer.parseInt(spo.get("EndIndex").toString()), Instant.now().toString().replace("00Z", "+00:00"),
                            Instant.now().toString().replace("00Z", "+00:00"), false, spo.get("CorrectedType").toString(),spo.get("Corrected").toString());
                    //zusätzlich 2 attribute
                    // CorrectedType
                    // Corrected
                }
                else if(spo.get("Type").equals("leiden/recensi"))
                {
                    //Todo
                    // spo erzeugen... property.put("ValueEntity", "null");
                    // property.put("RecensiManuText", attributes.get("Value").asString());


                    executeAddPropertyRecensi(uuidProperty,spo.get("AnnotatedText").toString() ,spo.get("Type").toString(),Integer.parseInt(spo.get("StartIndex").toString()),
                            Integer.parseInt(spo.get("EndIndex").toString()), Instant.now().toString().replace("00Z", "+00:00"),
                            Instant.now().toString().replace("00Z", "+00:00"), false, spo.get("RecensiTextName").toString(), spo.get("AdditionalText").toString(),
                    spo.get("Value").toString());

                    String type = "RecensiManuNote";
                    String recenciUuid ="";
                    //TODO....
                    try (Session sessionRecensi = driver.session()) {

                        Result resultRecensi = sessionRecensi.run("MATCH (n:Text) " +
                                "where n.TextType = \"" + type + "\"" +
                                "and n.Name = \"" + spo.get("RecensiTextName").toString() + "\"" +
                                "RETURN n;");



                        while (resultRecensi.hasNext()) {
                            Record tempR = resultRecensi.next();

                            List<Pair<String, Value>> values = tempR.fields();
                            for (Pair<String, Value> nameValue : values) {
                                Value value = nameValue.value();
                                recenciUuid = value.get("Guid").asString();
                            }
                        }
                    }

                    executeLinkToRecenciNote(uuidProperty, recenciUuid);
                    //link ziehen zu Textknoten ziehen....
                }
                else if(spo.get("Type").equals("place")){

                    executeAddPropertyEntity(uuidProperty,spo.get("AnnotatedText").toString() ,spo.get("Type").toString(),Integer.parseInt(spo.get("StartIndex").toString()),
                            Integer.parseInt(spo.get("EndIndex").toString()), Instant.now().toString().replace("00Z", "+00:00"),
                            Instant.now().toString().replace("00Z", "+00:00"), false, spo.get("Value").toString());


                    //Todo link to lemma.....
                    executeLinkPropertyToEntity(uuidProperty,spo.get("Value").toString());
                }
                else if(spo.get("Type").equals("akteur")){

                    // spo erzeugen + link ziehen zu entity
                    executeAddPropertyEntity(uuidProperty,spo.get("AnnotatedText").toString() ,spo.get("Type").toString(),Integer.parseInt(spo.get("StartIndex").toString()),
                            Integer.parseInt(spo.get("EndIndex").toString()), Instant.now().toString().replace("00Z", "+00:00"),
                            Instant.now().toString().replace("00Z", "+00:00"), false, spo.get("Value").toString());

/*
                    property.put("CommunicationRoleGuid", resultList.get("communicationGuid"));
                    property.put("RoleGuid", resultList.get("valueRole"));
                    //----------------------------------------------------------------------------
                    property.put("PlaceGuid", resultList.get("valuePlace"));
                    //----------------------------------------------------------------------------
                    property.put("ConceptLabel", resultList.get("conceptLabel"));
                    //----------------------------------------------------------------------------
                    property.put("ConceptSubLabel", resultList.get("conceptSubLabel"));*/

                    //Todo link to lemma.....
                    executeLinkPropertyToEntity(uuidProperty, spo.get("Value").toString());
                    executeLinkPropertyToCommunicationRole(uuidProperty, spo.get("CommunicationRoleGuid").toString());
                    executeLinkPropertyToRole(uuidProperty, spo.get("RoleGuid").toString());
                    executeLinkPropertyToPlace(uuidProperty, spo.get("PlaceGuid").toString());

                    //Todo knoten erstellen und link ziehen....
                    executeCreateAndLinkConcept(uuidProperty,spo.get("ConceptLabel").toString(), spo.get("ConceptSubLabel"), Instant.now().toString().replace("00Z", "+00:00"));
                    //concept +
                    //subconecptlabel
                }
                else
                {

                   executeAddPropertiesMergeTexts(uuidProperty,spo.get("AnnotatedText").toString() ,spo.get("Type").toString(),Integer.parseInt(spo.get("StartIndex").toString()),
                           Integer.parseInt(spo.get("EndIndex").toString()), Instant.now().toString().replace("00Z", "+00:00"),
                                   Instant.now().toString().replace("00Z", "+00:00"), false);


                }

                //TODO Link von spo zu text ziehen....
                String relationUuid = java.util.UUID.randomUUID().toString();
                executeLinkPropertyToTextSplitText(textUuid, relationUuid, uuidProperty);

            }

        }


    }

    private void executeLinkToRecenciNote(String uuidProperty, String recenciUuid) {

        String relationUuid = java.util.UUID.randomUUID().toString();

        String exe = "MATCH (x:StandoffProperty { Guid: \"" + uuidProperty + "\" }), " +
                "(y:Text { Guid: \"" + recenciUuid + "\" }) " +
                "MERGE(x)-[r:has_note {Guid: \"" + relationUuid + "\"}]->(y)";


        System.out.println(exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }

    }

    private void executeCreateAndLinkConcept(String uuidProperty, String conceptLabel, Object conceptSubLabel, String DataAddedUTC) {

        String conceptUuid = java.util.UUID.randomUUID().toString();
        //Todo create node


        String exe = "MERGE (e:Concept { Guid: \"" + conceptUuid + "\" }) " +
                "ON CREATE SET e = {UserGuid: \"" + UserUuid + "\", " +
                "Label: \"" + conceptLabel + "\", DateAddedUTC: \"" + DataAddedUTC + "\",  IsDeleted: false,  SubType: \"" + conceptSubLabel + "\",  Guid: \"" + conceptUuid + "\"}";

        System.out.println("Text add...." + exe);

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(exe));
        }


        //TODO Link von spo zu text ziehen....
        executeLinkPropertyToConcept(uuidProperty, conceptUuid);
    }

    private void executeLinkPropertyToConcept(String uuidProperty, String conceptUuid) {



    }

    private String getLetterUuid(String letterName) {
        String textUuid = "";
        try (Session session = driver.session()) {

            Result result = session.run("MATCH (n:Text) " +
                    "where n.Name =  \"" + letterName + "\"" +
                    "RETURN n;");

        /*Result result = session.run("MATCH (n:Regesta) WHERE n.identifier STARTS WITH 'RI II,4' " +
                "AND n.isoStartDate > date('1002-01-01') AND n.isoEndDate < date('1021-12-31') AND n.latLong IS NOT NULL RETURN n;");*/



            while (result.hasNext()) {
                Record tempR = result.next();

                List<Pair<String, Value>> values = tempR.fields();
                for (Pair<String, Value> nameValue : values) {
                    Value value = nameValue.value();

                    textUuid = value.get("Guid").asString();
                }
            }
        }

        return textUuid;
    }
}
