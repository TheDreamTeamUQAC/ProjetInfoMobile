package dtuqac.runtimerapp;

import java.util.Arrays;
import java.util.List;

import javax.xml.datatype.Duration;

/**
 * Created by Tommy Duperré on 2017-03-15.
 */

public class CustomTime {
    //region Getter and Setter

    public int getHeures() {
        return Heures;
    }

    public void setHeures(int heures) {
        Heures = heures;
    }

    public int getMinutes() {
        return Minutes;
    }

    public void setMinutes(int minutes) {
        Minutes = minutes;
    }

    public int getSecondes() {
        return Secondes;
    }

    public void setSecondes(int secondes) {
        Secondes = secondes;
    }

    public int getMillisecondes() {
        return Millisecondes;
    }

    public void setMillisecondes(int millisecondes) {
        Millisecondes = millisecondes;
    }

    //endregion

    private int Heures;
    private int Minutes;
    private int Secondes;
    private int Millisecondes;

    CustomTime(){}

    public CustomTime(int heures, int minutes, int secondes, int millisecondes) {
        Heures = heures;
        Minutes = minutes;
        Secondes = secondes;
        Millisecondes = millisecondes;
    }

    public CustomTime(String _input){
        if(!setString(_input)){
            //Incapable de traiter la string
            Heures=0;Minutes=0;Secondes=0;Millisecondes=0;
        }
    }
    public CustomTime(CustomTime _t){
        recopier(_t);
    }

    public void recopier(CustomTime _t){
        this.Heures = _t.getHeures();
        this.Minutes = _t.getMinutes();
        this.Secondes = _t.getSecondes();
        this.Millisecondes = _t.getMillisecondes();
    }

    public String getStringWithoutZero() {
        String SmallTimer = String.format("%02d", Millisecondes);
        String MainTimer = String.format("%d", Secondes) + ":" + SmallTimer;

        if (Minutes > 0)
        {
            MainTimer = String.format("%d:%02d", Minutes, Secondes) + ":"+ SmallTimer;
        }

        if (Minutes >= 10)
        {
            MainTimer = String.format("%02d:%02d", Minutes, Secondes) + ":"+ SmallTimer;
        }

        if (Heures > 0)
        {
            MainTimer = String.format("%d:%02d:%02d", Heures, Minutes, Secondes) + ":" + SmallTimer;
        }

        if (Heures >= 10)
        {
            MainTimer = String.format("%02d:%02d:%02d", Heures, Minutes, Secondes) + ":" + SmallTimer;
        }

        return MainTimer;
    }

    public String getString(){
        return String.format("%02d:%02d:%02d:%02d", Heures,Minutes,Secondes,Millisecondes);
    }

    private long ToMiliseconds(CustomTime _Time)
    {
        long Duration = 0;

        Duration += _Time.Millisecondes;
        Duration += _Time.Secondes * 1000;
        Duration += _Time.Minutes * 60000;
        Duration += _Time.Heures * 3600000;

        return Duration;
    }

    //Retourne TRUE si l'instance est une duree plus longue que celle passe en parametre
    public boolean IsGreaterThan(CustomTime _TimeCompare)
    {
        if (ToMiliseconds(this) > ToMiliseconds(_TimeCompare))
        {
            return true;
        }
        return false;
    }

    //Format "00:00:00:00"
    public Boolean setString(String _input){
        List<String> temps = Arrays.asList(_input.split(":"));

        if (temps.size() == 1)
        {
            Heures = 0;
            Minutes = 0;
            Secondes = 0;
            Millisecondes = Integer.parseInt(temps.get(0));

            return true;
        }
        else if(temps.size() == 2)
        {
            Heures = 0;
            Minutes = 0;
            Secondes = Integer.parseInt(temps.get(0));
            Millisecondes = Integer.parseInt(temps.get(1));

            return true;
        }
        else if (temps.size() == 3)
        {
            Heures = 0;
            Minutes = Integer.parseInt(temps.get(0));
            Secondes = Integer.parseInt(temps.get(1));
            Millisecondes = Integer.parseInt(temps.get(2));

            return true;
        }
        else if(temps.size() == 4)
        {
            Heures = Integer.parseInt(temps.get(0));
            Minutes = Integer.parseInt(temps.get(1));
            Secondes = Integer.parseInt(temps.get(2));
            Millisecondes = Integer.parseInt(temps.get(3));

            return true;
        }
        else {
            return false;
        }
    }
}
