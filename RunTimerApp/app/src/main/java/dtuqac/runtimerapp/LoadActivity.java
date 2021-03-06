package dtuqac.runtimerapp;

import android.content.Intent;
import android.os.Bundle;
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

    static final int SPEED_RUN_FORM = 1;

    private SGBD db;
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
        popupMaker = new PopupMessage(LoadActivity.this);
        db = new SGBD(LoadActivity.this);

        //Charger les fichiers dans la listview
        ChargerEntreesListView();
        //Ajouter l'évènement onClick dans la listview
        AjouterListenerListView();

        ModeSuppression = false;
    }

    private void AjouterListenerListView()  {

        ListView lv = (ListView) findViewById(R.id.Liste_Fichiers);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // selected item
                String selected = parent.getItemAtPosition(position).toString();
                int speedRunId = db.getSpeedRunId(selected);

                if(ModeSuppression) {
                    //Confirmer la suppression de l'entrée de SpeedRun
                    if(speedRunId != -1)
                    {
                    popupMaker.ConfirmerSuppressionPopup("Supprimer \"" + selected +"\"?",speedRunId);
                    }else {
                        Toast.makeText(getBaseContext(),"Erreur lors de la suppression!",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    //Ici on fait les actions si on la SpeedRun
                    ActiveSpeedrun.getInstance().SetCurrentSpeedrun(db.getSpeedRunEntity(speedRunId));
                    //Renvoyer l'utilisateur à l'édition des splits de la speedrun choisie
                    Intent TimerIntent = new Intent(LoadActivity.this, TimerActivity.class);
                    startActivity(TimerIntent);
                    finish();
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

    public void ChargerEntreesListView() {
        List<SpeedRunEntity> speedRunExistantes;
        speedRunExistantes = db.getSpeedRunList();

        TextView txt = (TextView)findViewById(R.id.txtListViewVide);

        if(speedRunExistantes.size() == 0) {txt.setVisibility(View.VISIBLE);}
        else {txt.setVisibility(View.INVISIBLE);}

        ListView lv = (ListView) findViewById(R.id.Liste_Fichiers);

        List<String> listeNomsSpeedRun = new ArrayList<>();
        for(SpeedRunEntity s:speedRunExistantes){
            listeNomsSpeedRun.add(s.getGameName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                listeNomsSpeedRun);

        lv.setAdapter(arrayAdapter);
    }

    public void addNewSpeedRun(View _inputView){
        popupMaker.AjouterSpeedRunPopup("Nouvelle SpeedRun");

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
