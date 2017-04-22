package dtuqac.runtimerapp;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    public CustomTime(long _miliSeconds){
        this.Heures = (int)(_miliSeconds / 3600000);
        _miliSeconds = (int)(_miliSeconds % 3600000);

        this.Minutes = (int)(_miliSeconds/60000);
        _miliSeconds = (int)(_miliSeconds%60000);

        this.Secondes = (int)(_miliSeconds/1000);
        _miliSeconds = (int)(_miliSeconds%1000);

        this.Millisecondes = (int)_miliSeconds;
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
        String MainTimer = String.format("%d", Secondes) + "." + SmallTimer;

        if (Minutes > 0)
        {
            MainTimer = String.format("%d:%02d", Minutes, Secondes) + "."+ SmallTimer;
        }

        if (Minutes >= 10)
        {
            MainTimer = String.format("%02d:%02d", Minutes, Secondes) + "."+ SmallTimer;
        }

        if (Heures > 0)
        {
            MainTimer = String.format("%d:%02d:%02d", Heures, Minutes, Secondes) + "." + SmallTimer;
        }

        if (Heures >= 10)
        {
            MainTimer = String.format("%02d:%02d:%02d", Heures, Minutes, Secondes) + "." + SmallTimer;
        }

        return MainTimer;
    }

    public String getString(){
        return String.format("%02d:%02d:%02d.%02d", Heures,Minutes,Secondes,Millisecondes);
    }

    public long ToMiliseconds(CustomTime _Time)
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

    //Format "00:00:00.00"
    public Boolean setString(String _input){

        if (Objects.equals(_input, new String("0"))) //LOL beau langage
        {
            return false;
        }

        //String[] salut = _input.split(".");

        List<String> temps = Arrays.asList(Arrays.asList(_input.split("\\.")).get(0).split(":"));
        Integer milis = Integer.parseInt(Arrays.asList(_input.split("\\.")).get(1));

        if (temps.size() == 1)
        {
            Heures = 0;
            Minutes = 0;
            Secondes = Integer.parseInt(temps.get(0));
            Millisecondes = milis;

            return true;
        }
        else if(temps.size() == 2)
        {
            Heures = 0;
            Minutes = Integer.parseInt(temps.get(0));
            Secondes = Integer.parseInt(temps.get(1));
            Millisecondes = milis;

            return true;
        }
        else if (temps.size() == 3)
        {
            Heures = Integer.parseInt(temps.get(0));
            Minutes = Integer.parseInt(temps.get(1));
            Secondes = Integer.parseInt(temps.get(2));
            Millisecondes = milis;

            return true;
        }
        else {
            return false;
        }
    }

    public CustomTime soustraire(CustomTime _tempsASoustraire){
        long total = ToMiliseconds(this) - ToMiliseconds(_tempsASoustraire);

        this.Heures = (int)(total / 3600000);
        total = (int)(total % 3600000);

        this.Minutes = (int)(total/60000);
        total = (int)(total%60000);

        this.Secondes = (int)(total/1000);
        total = (int)(total%1000);

        this.Millisecondes = (int)total;

        return this;
    }

    public CustomTime additionner(CustomTime _tempsAAdditioner){
        long total = ToMiliseconds(this) + ToMiliseconds(_tempsAAdditioner);

        this.Heures = (int)(total / 3600000);
        total = (int)(total % 3600000);

        this.Minutes = (int)(total/60000);
        total = (int)(total%60000);

        this.Secondes = (int)(total/1000);
        total = (int)(total%1000);

        this.Millisecondes = (int)total;

        return this;
    }
}
