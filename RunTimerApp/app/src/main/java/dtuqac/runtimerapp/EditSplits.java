package dtuqac.runtimerapp;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;


public class EditSplits extends AppCompatActivity implements AdapterView.OnItemClickListener, EditSplitDialogFragment.EditSplitDialogListener {

    private int LastClickedItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_splits);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        lv.setOnItemClickListener(this);
    }

    public void AddSplit(View view)
    {
        //get l'ID du dernier split
        List<SplitDefinition> DefList = ActiveSpeedrun.getInstance().GetSplitDefinition();
        int LastID = DefList.get(DefList.size() - 1).getId();
        //Cree un nouveau split avec un nom temporaire
        SplitDefinition NewSplit = new SplitDefinition(LastID + 1, ActiveSpeedrun.getInstance().GetSpeedrunID(), "New Split");

        ActiveSpeedrun.getInstance().AddSplitDefinition(NewSplit);

        //refresh la list
        LoadtSplits();
        ActiveSpeedrun.getInstance().SaveInstance(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        LastClickedItem = position;

        EditSplit_Adapter adapt = (EditSplit_Adapter)parent.getAdapter();
        SplitDefinition splitdef = adapt.GetDefDataAt(position);
        Split splittime = adapt.GetTimeDataAt(position);

        FragmentManager fm = getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("splitdef",splitdef.getSplitName());
        bundle.putString("splittime", splittime.getSplitTime().getStringWithoutZero());

        DialogFragment dialog = new EditSplitDialogFragment();
        dialog.setArguments(bundle);
        dialog.show(fm,"tag");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Bundle info = dialog.getArguments();
        String SplitName = info.getString("splitdef");
        String SplitTime = info.getString("splittime");

        ValiderEdit(SplitName, SplitTime);
        ActiveSpeedrun.getInstance().SaveInstance(this);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

    }

    public void ValiderEdit(String SplitName, String SplitTime)
    {
        CustomTime time = new CustomTime();
        int pbID = ActiveSpeedrun.getInstance().GetPersonnalBestID();
        List<Split> splitList = ActiveSpeedrun.getInstance().GetSplitsByAttemptID(pbID);

        if (SplitName == "")
        {
            SplitName = "New Split";
        }
        if (Objects.equals(SplitTime, new String("")))
        {
            //Si le temps entré est vide ou est invalide, met le temps du split d'avant

            if (LastClickedItem == 0) //Si le premier split, ya pas de split avant...
            {
                time.setSecondes(1);
            }
            else
            {
                try
                {
                    time = new CustomTime(SplitTime);
                }
                catch (Exception ex)
                {
                    time = splitList.get(LastClickedItem - 1).getSplitTime();
                }
            }
        }
        else
        {
            try
            {
                time = new CustomTime(SplitTime);
            }
            catch (Exception ex)
            {
                time = splitList.get(LastClickedItem - 1).getSplitTime();
            }
        }

        ActiveSpeedrun.getInstance().UpdateSplitDefinition(LastClickedItem, SplitName);
        ActiveSpeedrun.getInstance().UpdateSplitTime(LastClickedItem, time);
        LoadtSplits();
    }
}
