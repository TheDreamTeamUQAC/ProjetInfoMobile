package dtuqac.runtimerapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import dtuqac.runtimerapp.ActiveSpeedrun;
import dtuqac.runtimerapp.SpeedRunEntity;

public class EditSplits extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_splits);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Teste si il y a une run active
        if (ActiveSpeedrun.getInstance().IsInitialized())
        {
            LoadtSplits();
        }
        else
        {
            Toast.makeText(getBaseContext(),"Veuillez d'abord choisir une speedrun!",Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void LoadtSplits()
    {
        //Set le nom de la run
        String RunName = ActiveSpeedrun.getInstance().GetActiveSpeedrun().getGameName();
        final TextView NameText = (TextView)findViewById(R.id.txtSpeedrunName);
        NameText.setText(RunName);

        //Récupère la liste des splits time
        int PBID = ActiveSpeedrun.getInstance().GetPersonnalBestID();
        List<Split> PBSplits = ActiveSpeedrun.getInstance().GetSplitsByAttemptID(PBID);

        //Récupère la liste des splits name
        List<SplitDefinition> SplitsList = ActiveSpeedrun.getInstance().GetActiveSpeedrun().getSpeedRunSplits();

        //Map la liste dans le listview
        EditSplit_Adapter ListAdapter = new EditSplit_Adapter(this, SplitsList, PBSplits);

        ListView lv = (ListView) findViewById(R.id.list_ActiveSplits);

        lv.setAdapter(ListAdapter);
    }

    public void AddSplit(View view)
    {
        Toast.makeText(getBaseContext(),"Salut",Toast.LENGTH_SHORT).show();

        //TODO: Add split
        //ActiveSpeedrun.getInstance().
    }


}
