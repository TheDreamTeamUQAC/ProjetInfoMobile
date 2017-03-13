package dtuqac.runtimerapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;

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

    private HashMap hp;

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
        }
        catch (SQLException ex)
        {
            throw ex;
        }

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

    public boolean insertContact (String name, String phone, String email, String street,String place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.insert("contacts", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SPEEDRUN_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteSpeedRun (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(SPEEDRUN_TABLE_NAME,
                SPEEDRUN_COLONNE_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public boolean speedRunExiste(String _nomSpeedRun){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + SPEEDRUN_TABLE_NAME +
                                            " where " + SPEEDRUN_COLONNE_GAMENAME + "=\""+_nomSpeedRun +"\"", null );

        if(res.getCount() > 0){
            return true;
        }
        else{
            return false;
        }

    }

    public boolean addSpeedRun(SpeedRunEntity speedrun){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SPEEDRUN_COLONNE_GAMENAME, speedrun.getGameName());
        contentValues.put(SPEEDRUN_COLONNE_CATEGORYNAME, speedrun.getCategoryName());
        contentValues.put(SPEEDRUN_COLONNE_USESEMULATOR, speedrun.getUsesEmulator());
        contentValues.put(SPEEDRUN_COLONNE_OFFSET, String.valueOf(speedrun.getOffSet()));
        db.insert(SPEEDRUN_TABLE_NAME, null, contentValues);
        return true;
    }

    public ArrayList<SpeedRunEntity> getSpeedRunList() {
        ArrayList<SpeedRunEntity> array_list = new ArrayList<>();

        //speedRunExiste("MOUHAHAHA");

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res;

        try {
            res =  db.rawQuery("select * from " + SPEEDRUN_TABLE_NAME,null);
        }
        catch (SQLException ex)
        {
            throw ex;
        }


        res.moveToFirst();

        DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.CANADA_FRENCH);

        while(res.isAfterLast() == false){
            try {
                SpeedRunEntity tmp = new SpeedRunEntity(
                        res.getInt(res.getColumnIndex(SPEEDRUN_COLONNE_ID)),
                        res.getString(res.getColumnIndex(SPEEDRUN_COLONNE_GAMENAME)),
                        res.getString(res.getColumnIndex(SPEEDRUN_COLONNE_CATEGORYNAME)),
                        ((res.getInt(res.getColumnIndex(SPEEDRUN_COLONNE_USESEMULATOR))==1)?true:false),
                        format.parse(res.getString(res.getColumnIndex(SPEEDRUN_COLONNE_OFFSET)))
                );

                array_list.add(tmp);
                res.moveToNext();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return array_list;
    }
}