package dtuqac.runtimerapp;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tommy Duperré on 2017-03-01.
 */

public class GestionFichier {
    static Context fileContext;

    GestionFichier(Context ctx){
        fileContext = ctx;
    }
    private String extensionFichier = ".xml";

    private String AjouterExtensionFichier(String _nomFichier) {
        return _nomFichier + extensionFichier;
    }
    private String EnleverExtensionFichier(String _nomFichier){
        return _nomFichier.replace(extensionFichier,"");
    }

    public String LireFichier(String _nomFichier){
        _nomFichier = AjouterExtensionFichier(_nomFichier);

        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = fileContext.openFileInput(_nomFichier);

            int contenu;
            while((contenu = fis.read()) != -1){
                sb.append((char)contenu);
            }

            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public Boolean EcrireFichier(String _nomFichier){
        return EcrireFichier(_nomFichier,ObtenirFichierDeBase(_nomFichier));
    }

    //True = Success
    //False = Failure
    //Gestion de l'écriture dans les fichiers
    public Boolean EcrireFichier(String _nomFichier, String _contenu){
        _nomFichier = AjouterExtensionFichier(_nomFichier);

        FileOutputStream outputStream;

        try {
            outputStream = fileContext.openFileOutput(_nomFichier, Context.MODE_PRIVATE);
            outputStream.write(_contenu.getBytes());

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //Vérifier l'existence d'un fichier
    public Boolean FichierExiste(String _nomFichier){
        _nomFichier = AjouterExtensionFichier(_nomFichier);

        try {
            FileInputStream fis = fileContext.openFileInput(_nomFichier);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Obetnir la liste des fichiers
    public List<String> ObtenirListeFichiers(){
        String path = fileContext.getFilesDir().getAbsolutePath().toString();
        File directory = new File(path);
        File[] files = directory.listFiles();

        List<String> result = new ArrayList<String>();

        for (int i = 0; i < files.length; i++)
        {
            if(!files[i].getName().equals("instant-run")){
                result.add(EnleverExtensionFichier(files[i].getName()));
            }
        }
        return result;
    }

    //Supprimer un fichier
    public Boolean SupprimerFichier(String _nomFichier){
        _nomFichier = AjouterExtensionFichier(_nomFichier);

        String path = fileContext.getFilesDir().getAbsolutePath().toString();

        try {
            File file = new File(path, _nomFichier);
            file.delete();
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    public String ObtenirFichierDeBase(String _nomSpeedRun){

        /*
        SplitTime split = new SplitTime(new Date());
        List<SplitTime> lstSplit = new ArrayList<>();
        lstSplit.add(split);

        Segment seg = new Segment("1e étape", lstSplit);
        List<Segment> lstSeg = new ArrayList<>();
        lstSeg.add(seg);

        Attempt att = new Attempt(0,new Date(), new Date(Calendar.getInstance().getTimeInMillis() + (10 * 60000)), lstSeg);
        List<Attempt> lstAtt = new ArrayList<>();
        lstAtt.add(att);

        XmlStructure xmlStruct =  new XmlStructure(
                "Test Game",
                "N/A",
                false,
                lstAtt
        );
        */

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8",null);
            serializer.startTag("","Run").attribute("","version","1.6.0");

            serializer.startTag("","GameIcon");serializer.endTag("","GameIcon");

            serializer.startTag("","GameName");
            serializer.text(_nomSpeedRun);
            serializer.endTag("","GameName");

            serializer.startTag("","CategoryName");
            serializer.text("");
            serializer.endTag("","CategoryName");


            serializer.startTag("","Metadata");

            serializer.startTag("","Run").attribute("","id","");serializer.endTag("","Run");
            serializer.startTag("","Platform").attribute("","usesEmulator","False");
            serializer.text("");
            serializer.endTag("","Platform");
            serializer.startTag("","Region");
            serializer.text("");
            serializer.endTag("","Region");
            serializer.startTag("","Variables");serializer.endTag("","Variables");

            serializer.endTag("","Metadata");

            serializer.startTag("","Offset");
            serializer.text("00:00:00");
            serializer.endTag("","Offset");

            serializer.startTag("","AttemptCount");
            serializer.text("0");
            serializer.endTag("","AttemptCount");

            serializer.startTag("","AttemptHistory");
            serializer.startTag("","Attempt").attribute("","id","0").attribute("","started","00/00/000 00:00:00").attribute("","isStartedSynced","True").attribute("","ended","00/00/000 00:00:00").attribute("","isEndedSynced","True");
            serializer.startTag("","RealTime");
            serializer.text("00:00:00.0000000");
            serializer.endTag("","RealTime");
            serializer.endTag("","Attempt");
            serializer.endTag("","AttemptHistory");

            serializer.startTag("","Segments");

            //!!!!!!!!!!!!!!
            //Debut Segment
            //!!!!!!!!!!!!!!
            serializer.startTag("","Segment");
            serializer.startTag("","Name");
            serializer.text("Allez à \"Edit Splits\" pour changer les segments");
            serializer.endTag("","Name");
            serializer.startTag("","Icon");
            serializer.endTag("","Icon");

            serializer.startTag("","SplitTimes");
            serializer.startTag("","SplitTime").attribute("","name","Personal Best");
            serializer.startTag("","RealTime");
            serializer.text("00:00:00.0000000");
            serializer.endTag("","RealTime");
            serializer.endTag("","SplitTime");
            serializer.endTag("","SplitTimes");

            serializer.startTag("","BestSegmentTime");
            serializer.startTag("","RealTime");
            serializer.text("00:00:00.0000000");
            serializer.endTag("","RealTime");
            serializer.endTag("","BestSegmentTime");

            //Historique du segment
            serializer.startTag("","SegmentHistory");

            serializer.startTag("","Time").attribute("","id","0");
            serializer.startTag("","RealTime");
            serializer.text("00:00:00.0000000");
            serializer.endTag("","RealTime");
            serializer.endTag("","Time");

            serializer.endTag("","SegmentHistory");

            serializer.endTag("","Segment");

            //!!!!!!!!!!!!!!
            //Fin Segment
            //!!!!!!!!!!!!!!

            serializer.endTag("","Segments");

            serializer.startTag("","AutoSplitterSettings");
            serializer.endTag("","AutoSplitterSettings");

            serializer.endTag("", "Run");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    public void TraiterXML(String _nomFichier) {
        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();


            parser.setInput(new StringReader(LireFichier(_nomFichier)));

            String title = null;
            String summary = null;
            String link = null;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                String name;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("GameName")) {
                            title = readGameName(parser);
                        } else if (name.equals("CategoryName")) {
                            summary = readCategoryName(parser);
                        }
                        else if (name.equals("Attempt")){
                            List<String> lstAttributes = new ArrayList<>();
                            //Aller chercher tous les essais
                            for (int i = 0;i<parser.getAttributeCount();i++)
                            {
                                lstAttributes.add(parser.getAttributeName(i));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        break;
                }
                eventType = parser.next();
            }
        }
        catch (Exception e) {
        }

            //return null;
    }

    private String readCategoryName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, "", "CategoryName");
        String CategoryName = readText(parser);
        parser.require(XmlPullParser.END_TAG, "", "CategoryName");
        return CategoryName;
    }

    // Processes title tags in the feed.
    private String readGameName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, "", "GameName");
        String GameName = readText(parser);
        parser.require(XmlPullParser.END_TAG, "", "GameName");
        return GameName;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }


}
