package dtuqac.runtimerapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import dtuqac.runtimerapp.GestionFichier;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void addNewSpeedRun(View _inputView){
        GestionFichier fileWorker = new GestionFichier(getApplicationContext());

        if(fileWorker.FichierExiste("2.txt"))
        {
            Toast.makeText(this,"Oui", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this,"Non", Toast.LENGTH_SHORT).show();
        }


        if (fileWorker.EcrireFichier("1.txt","Ceci est un test")){
            String tmp = fileWorker.LireFichier("1.txt");
            Toast.makeText(this,tmp, Toast.LENGTH_SHORT).show();
        }
    }

}
