package dtuqac.runtimerapp;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

}
