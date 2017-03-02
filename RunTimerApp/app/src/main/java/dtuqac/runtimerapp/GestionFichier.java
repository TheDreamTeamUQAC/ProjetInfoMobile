package dtuqac.runtimerapp;

import android.content.Context;


import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Tommy Duperré on 2017-03-01.
 */

public class GestionFichier {
    static Context fileContext;

    GestionFichier(Context ctx){
        fileContext = ctx;
    }

    public String LireFichier(String _nomFichier){

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

    //True = Success
    //False = Failure
    //Gestion de l'écriture dans les fichiers
    public Boolean EcrireFichier(String _nomFichier, String _contenu){

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

}
