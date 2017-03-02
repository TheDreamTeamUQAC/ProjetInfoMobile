package dtuqac.runtimerapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LoadActivity extends AppCompatActivity {

    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void addNewSpeedRun(View _inputView){
        GestionFichier fileWorker = new GestionFichier(this);

        List<String> fichiersExistants = new ArrayList<String>();
        fichiersExistants = fileWorker.ObtenirListeFichiers();


        PopupMessage popup = new PopupMessage(this);

        popup.AskString("Nom de la nouvelle SpeedRun?");

        //Toast.makeText(this,"Non", Toast.LENGTH_SHORT).show();
/*



        if(fileWorker.FichierExiste("1.txt"))
        {
            Toast.makeText(this,"Oui", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this,"Non", Toast.LENGTH_SHORT).show();
        }


        if (fileWorker.EcrireFichier("2.txt","Ceci est un test")){
            String tmp = fileWorker.LireFichier("2.txt");
            Toast.makeText(this,tmp, Toast.LENGTH_SHORT).show();
        }*/
    }

}
