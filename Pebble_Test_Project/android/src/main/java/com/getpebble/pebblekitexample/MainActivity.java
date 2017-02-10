package com.getpebble.pebblekitexample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.UUID;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;

//Importer les fonctions communes
import com.getpebble.pebblekitexample.Common;

public class MainActivity extends Activity {
	
	private static final UUID WATCHAPP_UUID = UUID.fromString("6456a937-1e6d-40cf-a871-6545ea853727");
	private static final String WATCHAPP_FILENAME = "Speed_Run_Remote.pbw";
	
	private static final int
		KEY_BUTTON = 0,
		MESSAGE_TIME = 43,
		BUTTON_UP = 0,
		BUTTON_SELECT = 1,
		BUTTON_DOWN = 2;
	
	private Handler handler = new Handler();
	private PebbleDataReceiver appMessageReciever;
	private TextView whichButtonView;
	private Common fctCommunes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Customize ActionBar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("PebbleKit Example");
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_orange)));

		// Add Install Button behavior
		Button installButton = (Button)findViewById(R.id.button_install);
		installButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Install 
				Toast.makeText(getApplicationContext(), "Installing watchapp...", Toast.LENGTH_SHORT).show();
				sideloadInstall(getApplicationContext(), WATCHAPP_FILENAME);

			}
			
		});
		
		// Add vibrate Button behavior
		Button vibrateButton = (Button)findViewById(R.id.button_vibrate);
		vibrateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Send KEY_VIBRATE to Pebble
				PebbleDictionary out = new PebbleDictionary();
				//out.addInt32(KEY_VIBRATE, 0);
				String time = new SimpleDateFormat("hh:mm:ss").format(new java.util.Date());

				out.addString(MESSAGE_TIME, time);
				PebbleKit.sendDataToPebble(getApplicationContext(), WATCHAPP_UUID, out);
			}
			
		});
		
		// Add output TextView behavior
		whichButtonView = (TextView)findViewById(R.id.which_button);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		// Define AppMessage behavior
		if(appMessageReciever == null) {
			appMessageReciever = new PebbleDataReceiver(WATCHAPP_UUID) {
				
				@Override
				public void receiveData(Context context, int transactionId, PebbleDictionary data) {
					// Always ACK
					PebbleKit.sendAckToPebble(context, transactionId);
					
					// What message was received?
					if(data.getInteger(KEY_BUTTON) != null) {
						// KEY_BUTTON was received, determine which button
						final int button = data.getInteger(KEY_BUTTON).intValue();
						
						// Update UI on correct thread
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								switch(button) {
								case BUTTON_UP:
									whichButtonView.setText("UP");
									fctCommunes.ExecVibration(getApplicationContext(),500);
									break;
								case BUTTON_SELECT:
									whichButtonView.setText("SELECT");
									fctCommunes.ExecVibration(getApplicationContext(),500);
									break;
								case BUTTON_DOWN:
									whichButtonView.setText("DOWN");
									fctCommunes.ExecVibration(getApplicationContext(),500);
									break;
								default:
									Toast.makeText(getApplicationContext(), "Unknown button: " + button, Toast.LENGTH_SHORT).show();
									break;
								}
							}
							
						});
					} 
				}
			};
		
			// Add AppMessage capabilities
			PebbleKit.registerReceivedDataHandler(this, appMessageReciever);
		}
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();

		//Open app on pebble
		if(PebbleKit.isWatchConnected(getApplicationContext())) {
			PebbleKit.startAppOnPebble(getApplicationContext(), WATCHAPP_UUID);
		}

	}
	
	@Override
	protected void onPause() {
		super.onPause();

		//Close app on pebble
		PebbleKit.closeAppOnPebble(getApplicationContext(),WATCHAPP_UUID);

		// Unregister AppMessage reception
		if(appMessageReciever != null) {
			unregisterReceiver(appMessageReciever);
			appMessageReciever = null;
		}
	}
	
	/**
     * Alternative sideloading method
     * Source: http://forums.getpebble.com/discussion/comment/103733/#Comment_103733 
     */
    public static void sideloadInstall(Context ctx, String assetFilename) {
        try {
            // Read .pbw from assets/
        	Intent intent = new Intent(Intent.ACTION_VIEW);    
            File file = new File(ctx.getExternalFilesDir(null), assetFilename);
            InputStream is = ctx.getResources().getAssets().open(assetFilename);
            OutputStream os = new FileOutputStream(file);
            byte[] pbw = new byte[is.available()];
            is.read(pbw);
            os.write(pbw);
            is.close();
            os.close();
             
            // Install via Pebble Android app
            intent.setDataAndType(Uri.fromFile(file), "application/pbw");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        } catch (IOException e) {
            Toast.makeText(ctx, "App install failed: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
