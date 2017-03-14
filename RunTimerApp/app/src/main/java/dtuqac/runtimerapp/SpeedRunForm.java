package dtuqac.runtimerapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class SpeedRunForm extends AppCompatActivity {

    private Date Offset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_run_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Offset = new Date();
        Offset.setTime(0);
    }

    public void validateEntry(View _view){
        SpeedRunEntity tmp = new SpeedRunEntity(0,"TEST","TEST",true,new Date());

        Intent resultIntent = new Intent();
        //TODO mettre les attributs de la speed run un par un
        resultIntent.putExtra("speedrun", (Serializable)tmp);
        setResult(SpeedRunForm.RESULT_OK, resultIntent);
        finish();
    }

    public void showTimePicker(View v){
            // Get Current Time
            final int mHour, mMinute;

            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            ((EditText)findViewById(R.id.txtOffset)).setText(hourOfDay + ":" + minute);
                            Offset.setHours(hourOfDay);
                            Offset.setMinutes(minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
    }
}
