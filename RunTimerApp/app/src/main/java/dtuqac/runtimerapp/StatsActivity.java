package dtuqac.runtimerapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.List;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        //Set title
        if(ActiveSpeedrun.getInstance().IsInitialized()) {
            getSupportActionBar().setTitle(ActiveSpeedrun.getInstance().GetGameName() + " - " + ActiveSpeedrun.getInstance().GetCategoryName());
            MettreAJourStatsRun();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    void AjouterDonneesGraphique(List<Attempt> listeAttempts){
        Attempt BestAttempt = null;
        for (Attempt att:listeAttempts) {
            if(att.getIsBestAttempt())
            {
                BestAttempt = att;
                break;
            }
        }

        int i = 0;
        DataPoint data[] = new DataPoint[BestAttempt.getSplits().size()];

        for (Split split:BestAttempt.getSplits()) {
            data[i] = new DataPoint(i,(split.getDuration().ToMiliseconds()/1000));
            i++;
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(data);

        series.setSpacing(0);

        graph.addSeries(series);

        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        // draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        //series.setValuesOnTopSize(50);
    }

    //Mettre à jour les statistiques
    void MettreAJourStatsRun(){
        SGBD db = new SGBD(StatsActivity.this);
        List<Attempt> listeAttempts = db.getAttemptList(ActiveSpeedrun.getInstance().GetSpeedrunID());

        //Nombre d'essais (attempt)
        TextView t1 = (TextView)this.findViewById(R.id.txtC2L1);
        t1.setText(Integer.toString(listeAttempts.size()));

        //Nombre de segments (splits)
        TextView t2 = (TextView)this.findViewById(R.id.txtC2L2);
        t2.setText(Integer.toString(ActiveSpeedrun.getInstance().GetSplitDefinition().size()));

        //Temps meilleur essai
        TextView t3 = (TextView)this.findViewById(R.id.txtC2L3);

        if(listeAttempts.size() >0) {
            //------------------------------------------------------
            //Ajouter les données dans le graphique
            //------------------------------------------------------
            AjouterDonneesGraphique(listeAttempts);

            Attempt BestAttempt = null;
            for (Attempt att : listeAttempts) {
                if (att.getIsBestAttempt()) {
                    BestAttempt = att;
                    break;
                }
            }
            t3.setText(BestAttempt.getTimeEnded().soustraire(BestAttempt.getTimeStarted()).getString() + "");
        }
        else{
            t3.setText("Aucun essai");
        }


        //Segment le plus court
        TextView t4 = (TextView)this.findViewById(R.id.txtC2L4);
        //Segment le plus long
        TextView t5 = (TextView)this.findViewById(R.id.txtC2L5);
        //Durée moyenne des segments
        TextView t6 = (TextView)this.findViewById(R.id.txtC2L6);

        Split court = new Split(0,0,0,new CustomTime(9999,9999,9999,9999),null,null);
        Split slong = new Split(0,0,0,new CustomTime(0,0,0,0),null,null);
        int nbSplit = 0;
        CustomTime cumul = new CustomTime(0,0,0,0);
        if(listeAttempts.size() >0) {

            Attempt BestAttempt = null;
            for (Attempt att : listeAttempts) {
                for (Split split: att.getSplits()) {
                    if(!split.getDuration().IsGreaterThan(court.getDuration())){
                        court = split;
                    }
                    if(split.getDuration().IsGreaterThan(slong.getDuration())){
                        slong = split;
                    }
                    cumul = cumul.additionner(split.getDuration());
                    nbSplit++;
                }
            }
            t4.setText("("+ db.getSplitDefinitionById(court.getIdSplitDefinition()).getSplitName() +") " + court.getDuration().getString());
            t5.setText("("+ db.getSplitDefinitionById(slong.getIdSplitDefinition()).getSplitName() +") " + slong.getDuration().getString());

            long moyenne = cumul.ToMiliseconds()/nbSplit;
            cumul = new CustomTime(moyenne);
            t6.setText(cumul.getString());
        }
        else{
            t4.setText("Aucun essai");
            t5.setText("Aucun essai");
            t6.setText("Aucun essai");
        }

    }

    //Action sur le bouton PLAY
    public void startSpeedRun(View _inputView){
        //Renvoyer l'utilisateur à l'édition des splits de la speedrun choisie
        Intent TimerIntent = new Intent(StatsActivity.this, TimerActivity.class);
        startActivity(TimerIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
