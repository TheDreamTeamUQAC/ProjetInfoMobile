package dtuqac.runtimerapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LoadActivity extends AppCompatActivity {

    private GestionFichier fileWorker;
    private PopupMessage popupMaker;

    private Boolean ModeSuppression;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialiser le gestionnaire de fichier
         fileWorker = new GestionFichier(LoadActivity.this);
        popupMaker = new PopupMessage(LoadActivity.this);

        //Charger les fichiers dans la listview
        ChargerFichiersListView();
        //Ajouter l'évènement onClick dans la listview
        AjouterListenetListView();

        ModeSuppression = false;
    }

    private void AjouterListenetListView() {

        ListView lv = (ListView) findViewById(R.id.Liste_Fichiers);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // selected item
                String selected = parent.getItemAtPosition(position).toString();

                if(ModeSuppression) {
                    //Confirmer la suppresion du fichier de SpeedRun
                    popupMaker.ConfirmerSuppressionPopup("Supprimer \"" + selected +"\"?", selected);
                }
                else {
                    //Ici on fait les évènements si on charge le fichier de SpeedRun
                    //TODO Linker avec les autres activity
                }
            }
        });
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.load, menu);
        return true;
    }

    public void ChargerFichiersListView() {
        List<String> fichiersExistants = new ArrayList<String>();
        fichiersExistants = fileWorker.ObtenirListeFichiers();

        TextView txt = (TextView)findViewById(R.id.txtListViewVide);

        if(fichiersExistants.size() == 0) {txt.setVisibility(View.VISIBLE);}
        else {txt.setVisibility(View.INVISIBLE);}

        ListView lv = (ListView) findViewById(R.id.Liste_Fichiers);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                fichiersExistants);

        lv.setAdapter(arrayAdapter);
    }

    public void addNewSpeedRun(View _inputView){
        popupMaker.AjouterFichierPopup("Nom de la nouvelle SpeedRun?");

        ModeSuppression = true;
        switchDelete(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.miDelete:
                switchDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void switchDelete()
    {
        switchDelete(true);
    }
    public void switchDelete(Boolean _afficher){
        ListView lv = (ListView) findViewById(R.id.Liste_Fichiers);
        String mode;
        if(ModeSuppression)
        {
            lv.setBackground((getDrawable(R.drawable.list_view_shape)));
            mode = "désactivé";
        }
        else
        {
            lv.setBackground((getDrawable(R.drawable.list_view_shape_delete)));
            mode = "activé";
        }

        //Envoyer un toast si demandé
        if(_afficher)
        {
            Toast.makeText(getBaseContext(),"Mode suppression " + mode + "!",Toast.LENGTH_SHORT).show();
        }
        //Changer le switch
        ModeSuppression = !ModeSuppression;
    }
}
