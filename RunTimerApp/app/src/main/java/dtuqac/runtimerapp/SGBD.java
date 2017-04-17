package dtuqac.runtimerapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.logging.ConsoleHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

/**
 * Created by Tommy Duperré on 2017-03-09.
 */

public class SGBD extends SQLiteOpenHelper {
    /*********************************************************************
     Nom de la database
     *********************************************************************/

    public static final String DATABASE_NAME = "SpeedRunTimer.db";
    public static final int DATABASE_VERSION = 2;

    /*********************************************************************
     Classe SpeedRunEntity
     *********************************************************************/

    public static String SPEEDRUN_TABLE_NAME = "speedrun";

    public static String SPEEDRUN_COLONNE_ID = "id";
    public static String SPEEDRUN_COLONNE_GAMENAME = "gamename";
    public static String SPEEDRUN_COLONNE_CATEGORYNAME = "categoryname";
    public static String SPEEDRUN_COLONNE_USESEMULATOR = "usesemulator";
    public static String SPEEDRUN_COLONNE_OFFSET = "offset";

    public static  String SPEEDRUN_CREATE_SQL =
            "create table " + SPEEDRUN_TABLE_NAME + " (" +
            SPEEDRUN_COLONNE_ID + " integer primary key autoincrement, " +
            SPEEDRUN_COLONNE_GAMENAME + " text not null, " +
            SPEEDRUN_COLONNE_CATEGORYNAME + " text not null, " +
            SPEEDRUN_COLONNE_USESEMULATOR + " integer not null, " +
            SPEEDRUN_COLONNE_OFFSET + " text not null"
            +")";

    /*********************************************************************
     Classe SplitDefinition pour garder une définition basique des split/segments
     *********************************************************************/

    public static String SPLITDEFINITION_TABLE_NAME = "splitdefinition";

    public static String SPLITDEFINITION_COLONNE_ID = "id";
    public static String SPLITDEFINITION_COLONNE_NOM = "nom";
    public static String SPLITDEFINITION_COLONNE_SPEEDRUNID = "speedrunid";

    public String SPLITDEFINITION_CREATE_SQL =
            "create table " + SPLITDEFINITION_TABLE_NAME + " (" +
                    SPLITDEFINITION_COLONNE_ID + " integer primary key autoincrement, " +
                    SPLITDEFINITION_COLONNE_NOM + " text not null, " +
                    SPLITDEFINITION_COLONNE_SPEEDRUNID + " integer not null, " +
                    "FOREIGN KEY ("+SPLITDEFINITION_COLONNE_SPEEDRUNID + ") REFERENCES "+SPEEDRUN_TABLE_NAME + "(" + SPEEDRUN_COLONNE_ID + ")"
                    + ")";

    /*********************************************************************
    Classe Attempt lorsqu'on fait exécute une speedrun afind e sauvegarder les données
    *********************************************************************/

    public static String ATTEMPT_TABLE_NAME = "attempt";

    public static String ATTEMPT_COLONNE_ID = "id";
    public static String ATTEMPT_COLONNE_SPEEDRUNID = "speedrunid";
    public static String ATTEMPT_COLONNE_TIMESTARTED = "timestarted";
    public static String ATTEMPT_COLONNE_TIMEENDED = "timeended";
    public static String ATTEMPT_COLONNE_ISBESTATTEMPT = "isbestattempt";

    public static String ATTEMPT_CREATE_SQL =
            "create table " + ATTEMPT_TABLE_NAME + " (" +
                    ATTEMPT_COLONNE_ID + " integer primary key autoincrement, " +
                    ATTEMPT_COLONNE_SPEEDRUNID + " integer not null, " +
                    ATTEMPT_COLONNE_TIMESTARTED + " text not null, " +
                    ATTEMPT_COLONNE_TIMEENDED + " text not null, " +
                    ATTEMPT_COLONNE_ISBESTATTEMPT + " integer not null, " +
                    "FOREIGN KEY ("+ATTEMPT_COLONNE_SPEEDRUNID + ") REFERENCES "+SPEEDRUN_TABLE_NAME + "(" + SPEEDRUN_COLONNE_ID + ")"
                    +")";

    /*********************************************************************
    Classe Split afin de garder tous les temps des segments séparément lors d'un Attempt
    *********************************************************************/

    public static String SPLIT_TABLE_NAME = "split";

    public static String SPLIT_COLONNE_ID = "id";
    public static String SPLIT_COLONNE_IDATTEMPT = "idattempt";
    public static String SPLIT_COLONNE_SEGMENTTIME = "segmenttime";
    public static String SPLIT_COLONNE_SPLITTIME = "splittime";
    public static String SPLIT_COLONNE_IDSPLITDEFINITION = "idsplitdefinition";
    public static String SPLIT_COLONNE_ISBESTSEGMENT = "isbestsegment";

    public static String SPLIT_CREATE_SQL =
            "create table " + SPLIT_TABLE_NAME + " (" +
                    SPLIT_COLONNE_ID + " integer primary key autoincrement, " +
                    SPLIT_COLONNE_IDATTEMPT + " integer not null, " +
                    SPLIT_COLONNE_SEGMENTTIME + " text not null, " +
                    SPLIT_COLONNE_SPLITTIME + " text not null, " +
                    SPLIT_COLONNE_IDSPLITDEFINITION + " integer not null, " +
                    SPLIT_COLONNE_ISBESTSEGMENT + " integer null, " +
                    "FOREIGN KEY ("+SPLIT_COLONNE_IDATTEMPT + ") REFERENCES "+ATTEMPT_TABLE_NAME + "(" + ATTEMPT_COLONNE_ID + "), " +
                    "FOREIGN KEY ("+SPLIT_COLONNE_IDSPLITDEFINITION + ") REFERENCES "+SPLITDEFINITION_TABLE_NAME + "(" + SPLITDEFINITION_COLONNE_ID + ")"
                    +")";

    /*********************************************************************/

    private Context SGBDContext;

    public SGBD(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        SGBDContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Structure des tables lorsque la bd est créée pour la première fois

        try {
            db.execSQL(SPEEDRUN_CREATE_SQL);
            db.execSQL(SPLITDEFINITION_CREATE_SQL);
            db.execSQL(ATTEMPT_CREATE_SQL);
            db.execSQL(SPLIT_CREATE_SQL);

            try
            {
                ReadSQLFile(db);
            }
            catch (IOException e)
            {
                //Well... shit ¯\_(ツ)_/¯
            }

        }
        catch (SQLException ex)
        {
            throw ex;
        }

    }

    private void ReadSQLFile(SQLiteDatabase db) throws IOException
    {
        int result = 0;

        InputStream insertStream = SGBDContext.getResources().openRawResource(R.raw.inserts);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader((insertStream)));
        while (insertReader.ready())
        {
            String query = insertReader.readLine();
            if (query.charAt(0) == 'I')
            {
                db.execSQL(query);
                result++;
            }
        }
        insertReader.close();
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + SPLIT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SPLITDEFINITION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ATTEMPT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SPEEDRUN_TABLE_NAME);
        onCreate(db);
    }

    public void deleteDatabse(){
        SGBDContext.deleteDatabase(DATABASE_NAME);
    }


    //TODO NUMBER OF ROWS EXAMPLE
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SPEEDRUN_TABLE_NAME);
        return numRows;
    }


    public SpeedRunEntity getSpeedRunEntity(int _nomSpeedRunId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor resSpeedRun =  db.rawQuery( "select * from " + SPEEDRUN_TABLE_NAME +
                " where " + SPEEDRUN_COLONNE_ID + "=\""+_nomSpeedRunId +"\"", null );

        int count = resSpeedRun.getCount();

        if(count > 0){
            resSpeedRun.moveToNext();
            SpeedRunEntity speedRunRes = extraireSpeedRunFromCursor(resSpeedRun);

            //Vérifier les split definition liés
            Cursor resSplitDef =  db.rawQuery( "select * from " + SPLITDEFINITION_TABLE_NAME +
                    " where " + SPLITDEFINITION_COLONNE_SPEEDRUNID + "=\""+_nomSpeedRunId +"\"", null );

            count = resSplitDef.getCount();
            if(count >0) {
                resSplitDef.moveToFirst();
                while (resSplitDef.isAfterLast() == false) {
                    speedRunRes.addSplitDefinition(extraireSplitDefinitionFromCursor(resSplitDef));
                    resSplitDef.moveToNext();
                }

                //Vérifier les attempts liés
                Cursor resAttempt = db.rawQuery("select * from " + ATTEMPT_TABLE_NAME +
                        " where " + ATTEMPT_COLONNE_SPEEDRUNID + "=\"" + _nomSpeedRunId + "\"", null);

                count = resAttempt.getCount();
                if (count > 0) {
                    resAttempt.moveToFirst();
                    while (resAttempt.isAfterLast() == false) {
                        Attempt att = extraireAttemptFromCursor(resAttempt);

                        //Vérifier pour les splits dans l'attempt trouvé
                        Cursor resSplit = db.rawQuery("select * from " + SPLIT_TABLE_NAME +
                                " where " + SPLIT_COLONNE_IDATTEMPT + "=\"" + att.getId() + "\"", null);

                        count = resSplit.getCount();
                        if (count > 0) {
                            resSplit.moveToFirst();
                            while (resSplit.isAfterLast() == false) {
                                att.addSplit(extraireSplitFromCursor(resSplit));
                                resSplit.moveToNext();
                            }
                        }

                        speedRunRes.addAttempt(att);
                        resAttempt.moveToNext();
                    }
                }

            }
            return speedRunRes;
        }
        else{
            return null;
        }
    }

    public void SaveInstance(SpeedRunEntity entity){
        if(!speedRunExiste(entity.getGameName())){
            return;
        }

        updateSpeedRun(entity);
    }


    //region SpeedRun

    public Integer deleteSpeedRun (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(SPEEDRUN_TABLE_NAME,
                SPEEDRUN_COLONNE_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public boolean addSpeedRun(SpeedRunEntity speedrun){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SPEEDRUN_COLONNE_GAMENAME, speedrun.getGameName());
        contentValues.put(SPEEDRUN_COLONNE_CATEGORYNAME, speedrun.getCategoryName());
        contentValues.put(SPEEDRUN_COLONNE_USESEMULATOR, speedrun.getUsesEmulator());
        contentValues.put(SPEEDRUN_COLONNE_OFFSET, speedrun.getOffSet().getString());
        db.insert(SPEEDRUN_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateSpeedRun (SpeedRunEntity speedRun) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SPEEDRUN_COLONNE_GAMENAME, speedRun.getGameName());
        contentValues.put(SPEEDRUN_COLONNE_CATEGORYNAME, speedRun.getCategoryName());
        contentValues.put(SPEEDRUN_COLONNE_OFFSET, speedRun.getOffSet().toString());
        contentValues.put(SPEEDRUN_COLONNE_USESEMULATOR, speedRun.getUsesEmulator());
        db.update(SPEEDRUN_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(speedRun.getId()) } );

        for (SplitDefinition splitDef: speedRun.getSpeedRunSplits()) {
            if(getSplitDefinitionById(splitDef.getId())==null) {
                addSplitDefinition(splitDef);
            }
            else{
                updateSplitDefinition(splitDef);
            }
        }

        for (Attempt att: speedRun.getAttemptHistory()) {
            if(getAttemptById(att.getId())==null){
                  addAttempt(att);
            }
            else
            {
                updateAttempt(att);
            }
        }

        return true;
    }

    public int getNextSpeedRunId(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select max(id) as maximum from " + SPEEDRUN_TABLE_NAME, null );

        int count = res.getCount();
        res.moveToNext();
        if(count > 0){
            return res.getInt(res.getColumnIndex("maximum")) +1;
        }
        else{
            return -1;
        }
    }

    public boolean speedRunExiste(String _nomSpeedRun){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select id from " + SPEEDRUN_TABLE_NAME +
                                            " where " + SPEEDRUN_COLONNE_GAMENAME + "=\""+_nomSpeedRun +"\"", null );
        int count = res.getCount();
        if(count > 0){
            return true;
        }
        else{
            return false;
        }
    }

    public int getSpeedRunId(String _nomSpeedRun){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select id from " + SPEEDRUN_TABLE_NAME +
                " where " + SPEEDRUN_COLONNE_GAMENAME + "=\""+_nomSpeedRun +"\"", null );

        int count = res.getCount();

        if(count > 0){
            res.moveToNext();
            return res.getInt(res.getColumnIndex(SPEEDRUN_COLONNE_ID));
        }
        else{
            return -1;
        }
    }

    public SpeedRunEntity getSpeedRunById(int _id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + SPEEDRUN_TABLE_NAME +
                " where " + SPEEDRUN_COLONNE_ID + "=\""+_id +"\"", null );

        int count = res.getCount();

        if(count > 0){
            res.moveToNext();
            return extraireSpeedRunFromCursor(res);
        }
        else{
            return null;
        }
    }

    public ArrayList<SpeedRunEntity> getSpeedRunList() {
        ArrayList<SpeedRunEntity> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery("select * from " + SPEEDRUN_TABLE_NAME,null);

        res.moveToFirst();

        int count = res.getCount();
        if(count >0)
        {
            while(res.isAfterLast() == false) {
                SpeedRunEntity tmp = extraireSpeedRunFromCursor(res);
                array_list.add(tmp);
                res.moveToNext();
            }
        }
        return array_list;
    }

    private SpeedRunEntity extraireSpeedRunFromCursor(Cursor csr) {
            return new SpeedRunEntity(
                    csr.getInt(csr.getColumnIndex(SPEEDRUN_COLONNE_ID)),
                    csr.getString(csr.getColumnIndex(SPEEDRUN_COLONNE_GAMENAME)),
                    csr.getString(csr.getColumnIndex(SPEEDRUN_COLONNE_CATEGORYNAME)),
                    ((Integer.parseInt(csr.getString(csr.getColumnIndex(SPEEDRUN_COLONNE_USESEMULATOR))) == 1) ? true : false),
                    new CustomTime(csr.getString(csr.getColumnIndex(SPEEDRUN_COLONNE_OFFSET)))
            );
    }

    //endregion

    //region Attempt

    public Integer deleteAttempt (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ATTEMPT_TABLE_NAME,
                ATTEMPT_COLONNE_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public boolean addAttempt(Attempt att){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ATTEMPT_COLONNE_ISBESTATTEMPT, att.getBestAttempt());
        contentValues.put(ATTEMPT_COLONNE_SPEEDRUNID, att.getSpeedRunId());
        contentValues.put(ATTEMPT_COLONNE_TIMEENDED, att.getTimeEnded().getString());
        contentValues.put(ATTEMPT_COLONNE_TIMESTARTED, att.getTimeStarted().getString());
        db.insert(ATTEMPT_TABLE_NAME, null, contentValues);

        //Ajouter les splits dans l'attempt
        for (Split split: att.getSplits()) {
            addSplit(split);
        }

        return true;
    }

    public boolean updateAttempt(Attempt att){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ATTEMPT_COLONNE_ISBESTATTEMPT, att.getBestAttempt());
        contentValues.put(ATTEMPT_COLONNE_SPEEDRUNID, att.getSpeedRunId());
        contentValues.put(ATTEMPT_COLONNE_TIMEENDED, att.getTimeEnded().getString());
        contentValues.put(ATTEMPT_COLONNE_TIMESTARTED, att.getTimeStarted().getString());
        db.update(ATTEMPT_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(att.getId()) } );

        //Mettre à jour les splits dans l'attempt
        for (Split split: att.getSplits()) {
            updateSplit(split);
        }

        return true;
    }

    public int getNextAttemptId(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select max(id) from as maximum " + ATTEMPT_TABLE_NAME, null );

        int count = res.getCount();
        res.moveToNext();
        if(count > 0){
            return res.getInt(res.getColumnIndex("maximum"))+1;
        }
        else{
            return -1;
        }
    }

    public Attempt getAttemptById(int _id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + ATTEMPT_TABLE_NAME +
                " where " + ATTEMPT_COLONNE_ID + "=\""+_id +"\"", null );

        int count = res.getCount();

        if(count > 0){
            res.moveToNext();
            return extraireAttemptFromCursor(res);
        }
        else{
            return null;
        }
    }

    private Attempt extraireAttemptFromCursor(Cursor csr) {
        return new Attempt(
                csr.getInt(csr.getColumnIndex(ATTEMPT_COLONNE_ID)),
                csr.getInt(csr.getColumnIndex(ATTEMPT_COLONNE_SPEEDRUNID)),
                new CustomTime(csr.getString(csr.getColumnIndex(ATTEMPT_COLONNE_TIMESTARTED))),
                new CustomTime(csr.getString(csr.getColumnIndex(ATTEMPT_COLONNE_TIMEENDED))),
                ((Integer.parseInt(csr.getString(csr.getColumnIndex(ATTEMPT_COLONNE_ISBESTATTEMPT))) == 1) ? true : false)
        );
    }


    //endregion



    //region Split

    private Integer deleteSplit (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(SPLIT_TABLE_NAME,
                SPLIT_COLONNE_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    private boolean addSplit(Split split){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SPLIT_COLONNE_IDATTEMPT, split.getIdAttempt());
        contentValues.put(SPLIT_COLONNE_IDSPLITDEFINITION, split.getIdSplitDefinition());
        contentValues.put(SPLIT_COLONNE_ISBESTSEGMENT, split.getIsBestSegment());
        contentValues.put(SPLIT_COLONNE_SEGMENTTIME, split.getDuration().getString());
        contentValues.put(SPLIT_COLONNE_SPLITTIME, split.getSplitTime().getString());
        db.insert(SPLIT_TABLE_NAME, null, contentValues);
        return true;
    }

    private boolean updateSplit(Split split){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SPLIT_COLONNE_IDATTEMPT, split.getIdAttempt());
        contentValues.put(SPLIT_COLONNE_IDSPLITDEFINITION, split.getIdSplitDefinition());
        contentValues.put(SPLIT_COLONNE_ISBESTSEGMENT, split.getIsBestSegment());
        contentValues.put(SPLIT_COLONNE_SEGMENTTIME, split.getDuration().getString());
        contentValues.put(SPLIT_COLONNE_SPLITTIME, split.getSplitTime().getString());
        db.update(SPLIT_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(split.getId()) } );
        return true;
    }

    public int getNextSplitId(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select max(id) as maximum from " + SPLIT_TABLE_NAME, null );

        int count = res.getCount();
        res.moveToNext();
        if(count > 0){
            return res.getInt(res.getColumnIndex("maximum"))+1;
        }
        else{
            return -1;
        }
    }

    public Split getSplitById(int _id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + SPLIT_TABLE_NAME +
                " where " + SPLIT_COLONNE_ID + "=\""+_id +"\"", null );

        int count = res.getCount();

        if(count > 0){
            res.moveToNext();
            return extraireSplitFromCursor(res);
        }
        else{
            return null;
        }
    }



    private Split extraireSplitFromCursor(Cursor csr) {
        return new Split(
                csr.getInt(csr.getColumnIndex(SPLIT_COLONNE_ID)),
                csr.getInt(csr.getColumnIndex(SPLIT_COLONNE_IDATTEMPT)),
                csr.getInt(csr.getColumnIndex(SPLIT_COLONNE_IDSPLITDEFINITION)),
                new CustomTime(csr.getString(csr.getColumnIndex(SPLIT_COLONNE_SEGMENTTIME))),
                new CustomTime(csr.getString(csr.getColumnIndex(SPLIT_COLONNE_SPLITTIME))),
                ((Integer.parseInt(csr.getString(csr.getColumnIndex(SPLIT_COLONNE_ISBESTSEGMENT))) == 1) ? true : false)
        );
    }

    //endregion


    //region SplitDefinition

    public Integer deleteSplitDefinition (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(SPLITDEFINITION_TABLE_NAME,
                SPLITDEFINITION_COLONNE_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public boolean addSplitDefinition(SplitDefinition splitDef){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SPLITDEFINITION_COLONNE_NOM, splitDef.getSplitName());
        contentValues.put(SPLITDEFINITION_COLONNE_SPEEDRUNID, splitDef.getSpeedRunId());
        db.insert(SPLITDEFINITION_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateSplitDefinition(SplitDefinition splitDef){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SPLITDEFINITION_COLONNE_NOM, splitDef.getSplitName());
        contentValues.put(SPLITDEFINITION_COLONNE_SPEEDRUNID, splitDef.getSpeedRunId());
        db.update(SPLITDEFINITION_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(splitDef.getId()) } );
        return true;
    }

    public int getNextSplitDefinitionId(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select max(id) as maximum from " + SPLITDEFINITION_TABLE_NAME, null );

        int count = res.getCount();
        res.moveToNext();
        if(count > 0){
            return res.getInt(res.getColumnIndex("maximum"))+1;
        }
        else{
            return -1;
        }
    }

    public SplitDefinition getSplitDefinitionById(int _id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + SPLITDEFINITION_TABLE_NAME +
                " where " + SPLITDEFINITION_COLONNE_ID + "=\""+_id +"\"", null );

        int count = res.getCount();

        if(count > 0){
            res.moveToNext();
            return extraireSplitDefinitionFromCursor(res);
        }
        else{
            return null;
        }
    }

    private SplitDefinition extraireSplitDefinitionFromCursor(Cursor csr) {
        return new SplitDefinition(
                csr.getInt(csr.getColumnIndex(SPLITDEFINITION_COLONNE_ID)),
                csr.getInt(csr.getColumnIndex(SPLITDEFINITION_COLONNE_SPEEDRUNID)),
                csr.getString(csr.getColumnIndex(SPLITDEFINITION_COLONNE_NOM))
        );
    }

    //endregion



    //region Tests
    private void InsertTestValues(SQLiteDatabase db)
    {
        //SQLiteDatabase db = this.getWritableDatabase();

        //insert la speedrun ***********************************************************************
        ContentValues contentValues = new ContentValues();
        contentValues.put(SPEEDRUN_COLONNE_GAMENAME, "The Legend of Zelda: Ocarina of Time");
        contentValues.put(SPEEDRUN_COLONNE_CATEGORYNAME, "Any%");
        contentValues.put(SPEEDRUN_COLONNE_USESEMULATOR, false);
        contentValues.put(SPEEDRUN_COLONNE_OFFSET, 0);
        db.insert(SPEEDRUN_TABLE_NAME, null, contentValues);

        //insert les split definition *************************************************************
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(SPLITDEFINITION_COLONNE_ID, 1);
        contentValues1.put(SPLITDEFINITION_COLONNE_NOM, "Escape");
        contentValues1.put(SPLITDEFINITION_COLONNE_SPEEDRUNID, 1);
        db.insert(SPLITDEFINITION_TABLE_NAME, null, contentValues1);

        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(SPLITDEFINITION_COLONNE_ID, 2);
        contentValues2.put(SPLITDEFINITION_COLONNE_NOM, "Kakariko");
        contentValues2.put(SPLITDEFINITION_COLONNE_SPEEDRUNID, 1);
        db.insert(SPLITDEFINITION_TABLE_NAME, null, contentValues2);

        ContentValues contentValues3 = new ContentValues();
        contentValues3.put(SPLITDEFINITION_COLONNE_ID, 3);
        contentValues3.put(SPLITDEFINITION_COLONNE_NOM, "Bottle");
        contentValues3.put(SPLITDEFINITION_COLONNE_SPEEDRUNID, 1);
        db.insert(SPLITDEFINITION_TABLE_NAME, null, contentValues3);

        ContentValues contentValues4 = new ContentValues();
        contentValues4.put(SPLITDEFINITION_COLONNE_ID, 4);
        contentValues4.put(SPLITDEFINITION_COLONNE_NOM, "Deku Tree");
        contentValues4.put(SPLITDEFINITION_COLONNE_SPEEDRUNID, 1);
        db.insert(SPLITDEFINITION_TABLE_NAME, null, contentValues4);

        ContentValues contentValues5 = new ContentValues();
        contentValues5.put(SPLITDEFINITION_COLONNE_ID, 5);
        contentValues5.put(SPLITDEFINITION_COLONNE_NOM, "Gohma");
        contentValues5.put(SPLITDEFINITION_COLONNE_SPEEDRUNID, 1);
        db.insert(SPLITDEFINITION_TABLE_NAME, null, contentValues5);

        ContentValues contentValues6 = new ContentValues();
        contentValues6.put(SPLITDEFINITION_COLONNE_ID, 6);
        contentValues6.put(SPLITDEFINITION_COLONNE_NOM, "Ganondoor");
        contentValues6.put(SPLITDEFINITION_COLONNE_SPEEDRUNID, 1);
        db.insert(SPLITDEFINITION_TABLE_NAME, null, contentValues6);

        ContentValues contentValues7 = new ContentValues();
        contentValues7.put(SPLITDEFINITION_COLONNE_ID, 7);
        contentValues7.put(SPLITDEFINITION_COLONNE_NOM, "Tower Collapse");
        contentValues7.put(SPLITDEFINITION_COLONNE_SPEEDRUNID, 1);
        db.insert(SPLITDEFINITION_TABLE_NAME, null, contentValues7);

        ContentValues contentValues8 = new ContentValues();
        contentValues8.put(SPLITDEFINITION_COLONNE_ID, 8);
        contentValues8.put(SPLITDEFINITION_COLONNE_NOM, "Done");
        contentValues8.put(SPLITDEFINITION_COLONNE_SPEEDRUNID, 1);
        db.insert(SPLITDEFINITION_TABLE_NAME, null, contentValues8);

        //insert Attempts**************************************************************************
        ContentValues contentValues9 = new ContentValues();
        contentValues9.put(ATTEMPT_COLONNE_ID, 1);
        contentValues9.put(ATTEMPT_COLONNE_ISBESTATTEMPT, false);
        contentValues9.put(ATTEMPT_COLONNE_TIMESTARTED, 0);
        contentValues9.put(ATTEMPT_COLONNE_TIMEENDED, new CustomTime(0,20,15,280).getStringWithoutZero());
        contentValues9.put(ATTEMPT_COLONNE_SPEEDRUNID, 1);
        db.insert(ATTEMPT_TABLE_NAME, null, contentValues9);

        ContentValues contentValues10 = new ContentValues();
        contentValues10.put(ATTEMPT_COLONNE_ID, 2);
        contentValues10.put(ATTEMPT_COLONNE_ISBESTATTEMPT, false);
        contentValues10.put(ATTEMPT_COLONNE_TIMESTARTED, 0);
        contentValues10.put(ATTEMPT_COLONNE_TIMEENDED, new CustomTime(0,20,7,410).getStringWithoutZero());
        contentValues10.put(ATTEMPT_COLONNE_SPEEDRUNID, 1);
        db.insert(ATTEMPT_TABLE_NAME, null, contentValues10);

        ContentValues contentValues11 = new ContentValues();
        contentValues11.put(ATTEMPT_COLONNE_ID, 3);
        contentValues11.put(ATTEMPT_COLONNE_ISBESTATTEMPT, false);
        contentValues11.put(ATTEMPT_COLONNE_TIMESTARTED, 0);
        contentValues11.put(ATTEMPT_COLONNE_TIMEENDED, new CustomTime(0,19,31,720).getStringWithoutZero());
        contentValues11.put(ATTEMPT_COLONNE_SPEEDRUNID, 1);
        db.insert(ATTEMPT_TABLE_NAME, null, contentValues11);

        ContentValues contentValues12 = new ContentValues();
        contentValues12.put(ATTEMPT_COLONNE_ID, 4);
        contentValues12.put(ATTEMPT_COLONNE_ISBESTATTEMPT, true);
        contentValues12.put(ATTEMPT_COLONNE_TIMESTARTED, 0);
        contentValues12.put(ATTEMPT_COLONNE_TIMEENDED, new CustomTime(0,19,16,170).getStringWithoutZero());
        contentValues12.put(ATTEMPT_COLONNE_SPEEDRUNID, 1);
        db.insert(ATTEMPT_TABLE_NAME, null, contentValues12);

        //insert Split pour Attempt 1 *************************************************************************************************
        ContentValues contentValues13 = new ContentValues();
        contentValues13.put(SPLIT_COLONNE_ID, 1);
        contentValues13.put(SPLIT_COLONNE_IDATTEMPT, 1);
        contentValues13.put(SPLIT_COLONNE_IDSPLITDEFINITION, 1); //Split Escape
        contentValues13.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues13.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,5,14,610).getStringWithoutZero());
        contentValues13.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,5,14,610).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues13);

        ContentValues contentValues14 = new ContentValues();
        contentValues14.put(SPLIT_COLONNE_ID, 2);
        contentValues14.put(SPLIT_COLONNE_IDATTEMPT, 1);
        contentValues14.put(SPLIT_COLONNE_IDSPLITDEFINITION, 2); //Split Kakariko
        contentValues14.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues14.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,1,10,860).getStringWithoutZero());
        contentValues14.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,6,25,470).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues14);

        ContentValues contentValues15 = new ContentValues();
        contentValues15.put(SPLIT_COLONNE_ID, 3);
        contentValues15.put(SPLIT_COLONNE_IDATTEMPT, 1);
        contentValues15.put(SPLIT_COLONNE_IDSPLITDEFINITION, 3); //Split Bottle
        contentValues15.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues15.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,11,69).getStringWithoutZero());
        contentValues15.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,8,36,540).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues15);

        ContentValues contentValues16 = new ContentValues();
        contentValues16.put(SPLIT_COLONNE_ID, 4);
        contentValues16.put(SPLIT_COLONNE_IDATTEMPT, 1);
        contentValues16.put(SPLIT_COLONNE_IDSPLITDEFINITION, 4); //Split Deku Tree
        contentValues16.put(SPLIT_COLONNE_ISBESTSEGMENT, true);
        contentValues16.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,11,19).getStringWithoutZero());
        contentValues16.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,10,47,730).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues16);

        ContentValues contentValues17 = new ContentValues();
        contentValues17.put(SPLIT_COLONNE_ID, 5);
        contentValues17.put(SPLIT_COLONNE_IDATTEMPT, 1);
        contentValues17.put(SPLIT_COLONNE_IDSPLITDEFINITION, 5); //Split Gohma
        contentValues17.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues17.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,19,319).getStringWithoutZero());
        contentValues17.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,13,7,50).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues17);

        ContentValues contentValues18 = new ContentValues();
        contentValues18.put(SPLIT_COLONNE_ID, 6);
        contentValues18.put(SPLIT_COLONNE_IDATTEMPT, 1);
        contentValues18.put(SPLIT_COLONNE_IDSPLITDEFINITION, 6); //Split Ganondoor
        contentValues18.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues18.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,1,9,930).getStringWithoutZero());
        contentValues18.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,14,16,980).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues18);

        ContentValues contentValues19 = new ContentValues();
        contentValues19.put(SPLIT_COLONNE_ID, 7);
        contentValues19.put(SPLIT_COLONNE_IDATTEMPT, 1);
        contentValues19.put(SPLIT_COLONNE_IDSPLITDEFINITION, 7); //Split Tower Collapse
        contentValues19.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues19.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,17,319).getStringWithoutZero());
        contentValues19.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,16,34,300).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues19);

        ContentValues contentValues20 = new ContentValues();
        contentValues20.put(SPLIT_COLONNE_ID, 8);
        contentValues20.put(SPLIT_COLONNE_IDATTEMPT, 1);
        contentValues20.put(SPLIT_COLONNE_IDSPLITDEFINITION, 8); //Split Done
        contentValues20.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues20.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,3,40,980).getStringWithoutZero());
        contentValues20.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,20,15,280).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues20);

        //insert Split pour Attempt 2 *************************************************************************************************
        ContentValues contentValues21 = new ContentValues();
        contentValues21.put(SPLIT_COLONNE_ID, 9);
        contentValues21.put(SPLIT_COLONNE_IDATTEMPT, 2);
        contentValues21.put(SPLIT_COLONNE_IDSPLITDEFINITION, 1); //Split Escape
        contentValues21.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues21.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,4,57,210).getStringWithoutZero());
        contentValues21.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,4,57,210).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues21);

        ContentValues contentValues22 = new ContentValues();
        contentValues22.put(SPLIT_COLONNE_ID, 10);
        contentValues22.put(SPLIT_COLONNE_IDATTEMPT, 2);
        contentValues22.put(SPLIT_COLONNE_IDSPLITDEFINITION, 2); //Split Kakariko
        contentValues22.put(SPLIT_COLONNE_ISBESTSEGMENT, true);
        contentValues22.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,1,4,770).getStringWithoutZero());
        contentValues22.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,6,1,979).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues22);

        ContentValues contentValues23 = new ContentValues();
        contentValues23.put(SPLIT_COLONNE_ID, 11);
        contentValues23.put(SPLIT_COLONNE_IDATTEMPT, 2);
        contentValues23.put(SPLIT_COLONNE_IDSPLITDEFINITION, 3); //Split Bottle
        contentValues23.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues23.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,22,980).getStringWithoutZero());
        contentValues23.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,8,24,959).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues23);

        ContentValues contentValues24 = new ContentValues();
        contentValues24.put(SPLIT_COLONNE_ID, 12);
        contentValues24.put(SPLIT_COLONNE_IDATTEMPT, 2);
        contentValues24.put(SPLIT_COLONNE_IDSPLITDEFINITION, 4); //Split Deku Tree
        contentValues24.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues24.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,12,910).getStringWithoutZero());
        contentValues24.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,10,37,869).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues24);

        ContentValues contentValues25 = new ContentValues();
        contentValues25.put(SPLIT_COLONNE_ID, 13);
        contentValues25.put(SPLIT_COLONNE_IDATTEMPT, 2);
        contentValues25.put(SPLIT_COLONNE_IDSPLITDEFINITION, 5); //Split Gohma
        contentValues25.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues25.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,8,890).getStringWithoutZero());
        contentValues25.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,12,46,759).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues25);

        ContentValues contentValues26 = new ContentValues();
        contentValues26.put(SPLIT_COLONNE_ID, 14);
        contentValues26.put(SPLIT_COLONNE_IDATTEMPT, 2);
        contentValues26.put(SPLIT_COLONNE_IDSPLITDEFINITION, 6); //Split Ganondoor
        contentValues26.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues26.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,1,0,940).getStringWithoutZero());
        contentValues26.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,13,47,699).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues26);

        ContentValues contentValues27 = new ContentValues();
        contentValues27.put(SPLIT_COLONNE_ID, 15);
        contentValues27.put(SPLIT_COLONNE_IDATTEMPT, 2);
        contentValues27.put(SPLIT_COLONNE_IDSPLITDEFINITION, 7); //Split Tower Collapse
        contentValues27.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues27.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,38,130).getStringWithoutZero());
        contentValues27.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,16,25,829).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues27);

        ContentValues contentValues28 = new ContentValues();
        contentValues28.put(SPLIT_COLONNE_ID, 16);
        contentValues28.put(SPLIT_COLONNE_IDATTEMPT, 2);
        contentValues28.put(SPLIT_COLONNE_IDSPLITDEFINITION, 8); //Split Done
        contentValues28.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues28.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,3,41,580).getStringWithoutZero());
        contentValues28.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,20,7,409).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues28);

        //insert Split pour Attempt 3 *************************************************************************************************
        ContentValues contentValues29 = new ContentValues();
        contentValues29.put(SPLIT_COLONNE_ID, 17);
        contentValues29.put(SPLIT_COLONNE_IDATTEMPT, 3);
        contentValues29.put(SPLIT_COLONNE_IDSPLITDEFINITION, 1); //Split Escape
        contentValues29.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues29.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,4,58,210).getStringWithoutZero());
        contentValues29.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,4,58,210).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues29);

        ContentValues contentValues30 = new ContentValues();
        contentValues30.put(SPLIT_COLONNE_ID, 18);
        contentValues30.put(SPLIT_COLONNE_IDATTEMPT, 3);
        contentValues30.put(SPLIT_COLONNE_IDSPLITDEFINITION, 2); //Split Kakariko
        contentValues30.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues30.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,1,5,820).getStringWithoutZero());
        contentValues30.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,6,4,30).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues30);

        ContentValues contentValues31 = new ContentValues();
        contentValues31.put(SPLIT_COLONNE_ID, 19);
        contentValues31.put(SPLIT_COLONNE_IDATTEMPT, 3);
        contentValues31.put(SPLIT_COLONNE_IDSPLITDEFINITION, 3); //Split Bottle
        contentValues31.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues31.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,12,510).getStringWithoutZero());
        contentValues31.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,8,16,539).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues31);

        ContentValues contentValues32 = new ContentValues();
        contentValues32.put(SPLIT_COLONNE_ID, 20);
        contentValues32.put(SPLIT_COLONNE_IDATTEMPT, 3);
        contentValues32.put(SPLIT_COLONNE_IDSPLITDEFINITION, 4); //Split Deku Tree
        contentValues32.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues32.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,14,840).getStringWithoutZero());
        contentValues32.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,10,31,840).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues32);

        ContentValues contentValues33 = new ContentValues();
        contentValues33.put(SPLIT_COLONNE_ID, 21);
        contentValues33.put(SPLIT_COLONNE_IDATTEMPT, 3);
        contentValues33.put(SPLIT_COLONNE_IDSPLITDEFINITION, 5); //Split Gohma
        contentValues33.put(SPLIT_COLONNE_ISBESTSEGMENT, true);
        contentValues33.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,6,139).getStringWithoutZero());
        contentValues33.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,12,37,570).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues33);

        ContentValues contentValues34 = new ContentValues();
        contentValues34.put(SPLIT_COLONNE_ID, 22);
        contentValues34.put(SPLIT_COLONNE_IDATTEMPT, 3);
        contentValues34.put(SPLIT_COLONNE_IDSPLITDEFINITION, 6); //Split Ganondoor
        contentValues34.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues34.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,0,57,500).getStringWithoutZero());
        contentValues34.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,13,35,19).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues34);

        ContentValues contentValues35 = new ContentValues();
        contentValues35.put(SPLIT_COLONNE_ID, 23);
        contentValues35.put(SPLIT_COLONNE_IDATTEMPT, 3);
        contentValues35.put(SPLIT_COLONNE_IDSPLITDEFINITION, 7); //Split Tower Collapse
        contentValues35.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues35.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,20,740).getStringWithoutZero());
        contentValues35.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,15,55,759).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues35);

        ContentValues contentValues36 = new ContentValues();
        contentValues36.put(SPLIT_COLONNE_ID, 24);
        contentValues36.put(SPLIT_COLONNE_IDATTEMPT, 3);
        contentValues36.put(SPLIT_COLONNE_IDSPLITDEFINITION, 8); //Split Done
        contentValues36.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues36.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,3,36,80).getStringWithoutZero());
        contentValues36.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,19,31,839).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues36);

        //insert Split pour Attempt 4 *************************************************************************************************
        ContentValues contentValues37 = new ContentValues();
        contentValues37.put(SPLIT_COLONNE_ID, 25);
        contentValues37.put(SPLIT_COLONNE_IDATTEMPT, 4);
        contentValues37.put(SPLIT_COLONNE_IDSPLITDEFINITION, 1); //Split Escape
        contentValues37.put(SPLIT_COLONNE_ISBESTSEGMENT, true);
        contentValues37.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,4,56,320).getStringWithoutZero());
        contentValues37.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,4,56,320).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues37);

        ContentValues contentValues38 = new ContentValues();
        contentValues38.put(SPLIT_COLONNE_ID, 26);
        contentValues38.put(SPLIT_COLONNE_IDATTEMPT, 4);
        contentValues38.put(SPLIT_COLONNE_IDSPLITDEFINITION, 2); //Split Kakariko
        contentValues38.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues38.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,1,5,650).getStringWithoutZero());
        contentValues38.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,6,1,970).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues38);

        ContentValues contentValues39 = new ContentValues();
        contentValues39.put(SPLIT_COLONNE_ID, 27);
        contentValues39.put(SPLIT_COLONNE_IDATTEMPT, 4);
        contentValues39.put(SPLIT_COLONNE_IDSPLITDEFINITION, 3); //Split Bottle
        contentValues39.put(SPLIT_COLONNE_ISBESTSEGMENT, true);
        contentValues39.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,8,870).getStringWithoutZero());
        contentValues39.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,8,10,840).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues39);

        ContentValues contentValues40 = new ContentValues();
        contentValues40.put(SPLIT_COLONNE_ID, 28);
        contentValues40.put(SPLIT_COLONNE_IDATTEMPT, 4);
        contentValues40.put(SPLIT_COLONNE_IDSPLITDEFINITION, 4); //Split Deku Tree
        contentValues40.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues40.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,12,10).getStringWithoutZero());
        contentValues40.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,10,22,850).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues40);

        ContentValues contentValues41 = new ContentValues();
        contentValues41.put(SPLIT_COLONNE_ID, 29);
        contentValues41.put(SPLIT_COLONNE_IDATTEMPT, 4);
        contentValues41.put(SPLIT_COLONNE_IDSPLITDEFINITION, 5); //Split Gohma
        contentValues41.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues41.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,18,50).getStringWithoutZero());
        contentValues41.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,12,40,900).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues41);

        ContentValues contentValues42 = new ContentValues();
        contentValues42.put(SPLIT_COLONNE_ID, 30);
        contentValues42.put(SPLIT_COLONNE_IDATTEMPT, 4);
        contentValues42.put(SPLIT_COLONNE_IDSPLITDEFINITION, 6); //Split Ganondoor
        contentValues42.put(SPLIT_COLONNE_ISBESTSEGMENT, false);
        contentValues42.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,0,58,10).getStringWithoutZero());
        contentValues42.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,13,38,910).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues42);

        ContentValues contentValues43 = new ContentValues();
        contentValues43.put(SPLIT_COLONNE_ID, 31);
        contentValues43.put(SPLIT_COLONNE_IDATTEMPT, 4);
        contentValues43.put(SPLIT_COLONNE_IDSPLITDEFINITION, 7); //Split Tower Collapse
        contentValues43.put(SPLIT_COLONNE_ISBESTSEGMENT, true);
        contentValues43.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,2,1,76).getStringWithoutZero());
        contentValues43.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,15,39,986).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues43);

        ContentValues contentValues44 = new ContentValues();
        contentValues44.put(SPLIT_COLONNE_ID, 32);
        contentValues44.put(SPLIT_COLONNE_IDATTEMPT, 4);
        contentValues44.put(SPLIT_COLONNE_IDSPLITDEFINITION, 8); //Split Done
        contentValues44.put(SPLIT_COLONNE_ISBESTSEGMENT, true);
        contentValues44.put(SPLIT_COLONNE_SEGMENTTIME, new CustomTime(0,3,36,184).getStringWithoutZero());
        contentValues44.put(SPLIT_COLONNE_SPLITTIME, new CustomTime(0,19,16,170).getStringWithoutZero());
        db.insert(SPLIT_TABLE_NAME, null, contentValues44);
    }
    //endregion


}

