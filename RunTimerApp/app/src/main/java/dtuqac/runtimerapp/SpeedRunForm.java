package dtuqac.runtimerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class SpeedRunForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_run_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void validateEntry(View _view){
        //Valider le nom de la speedrun
        String nomSpeedRun = ((EditText)findViewById(R.id.txtNomSpeedRun)).getText().toString();

        SGBD db = new SGBD(SpeedRunForm.this);
        if (db.speedRunExiste(nomSpeedRun)){
            //Avertir que l'entrée bd existe déjà
            Toast.makeText(SpeedRunForm.this,"La speedrun \"" + nomSpeedRun + "\" existe déjà!\nEntrez un nom de speedrun différent.",Toast.LENGTH_LONG).show();
            return;
        }

        Intent resultIntent = new Intent();

        resultIntent.putExtra("bundle", toBundle());
        setResult(SpeedRunForm.RESULT_OK, resultIntent);
        finish();
    }

    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString("gamename", ((EditText)findViewById(R.id.txtNomSpeedRun)).getText().toString());
        b.putString("categoryname", ((EditText)findViewById(R.id.txtCategorie)).getText().toString());
        b.putBoolean("usesemulator", ((CheckBox)findViewById(R.id.chkEmulator)).isChecked());

        //TODO arranger l'offset si on ajoute un picker
        //resultIntent.putExtra("offset", ((EditText)findViewById(R.id.txtOffset)).getText());
        b.putString("offset", "00:00:00");
        return b;
    }
}
