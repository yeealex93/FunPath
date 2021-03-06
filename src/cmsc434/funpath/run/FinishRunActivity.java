package cmsc434.funpath.run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cmsc434.funpath.R;
import cmsc434.funpath.login.HomeActivity;
import cmsc434.funpath.login.LoginActivity;
import cmsc434.funpath.login.RegisterActivity;
import cmsc434.funpath.map.utils.MapTools;
import cmsc434.funpath.map.utils.TextDisplayTools;
import cmsc434.funpath.prerun.ConfigureRunActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class FinishRunActivity extends Activity {
	
	private GoogleMap map;
	private RunPath run;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finishrun);
		
		// Load intent data
		Intent runTrackerIntent = getIntent();
		long hilliness = runTrackerIntent.getIntExtra(ConfigureRunActivity.HILLINESS, 0);

		run = new RunPath(runTrackerIntent); // must load first for distance display to work
		setDistanceDisplayFromIntent(runTrackerIntent);
		setTimeDisplayFromIntent(runTrackerIntent);

		// Button handlnig
		final String units = runTrackerIntent.getStringExtra(RunTrackerActivity.UNITS_STRING);
		Button goBackHome = (Button) findViewById(R.id.review_new_run_button);
		goBackHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FinishRunActivity.this, HomeActivity.class);
				intent.putExtra(RunTrackerActivity.UNITS_STRING, units);
				startActivity(intent);
			}
		});
		
		// Get map fragment
		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.review_map);
		map = mapFragment.getMap();
		map.setMyLocationEnabled(true);
		
		MapTools.drawPath(map, run);
		MapTools.zoomToLocation(map, run);

		// save path
		double distanceTravelled = runTrackerIntent.getDoubleExtra(RunTrackerActivity.DISTANCE_TRAVELLED, -1);
		long timeTaken = runTrackerIntent.getLongExtra(RunTrackerActivity.TIME_TAKEN, -1);
		writeToFile(run.getPath(), distanceTravelled, timeTaken, hilliness);
	}

	private void setDistanceDisplayFromIntent(Intent runTrackerIntent) {
		double distanceTravelled = runTrackerIntent.getDoubleExtra(RunTrackerActivity.DISTANCE_TRAVELLED, -1);
		float totalDistance = run.getPathDistanceInMeters();
		String units = runTrackerIntent.getStringExtra(RunTrackerActivity.UNITS_STRING);
		TextView distance = (TextView) findViewById(R.id.review_distance);
		distance.setText(TextDisplayTools.getDistanceText(distanceTravelled, totalDistance, units));
	}

	private void setTimeDisplayFromIntent(Intent runTrackerIntent) {
		long timeTaken = runTrackerIntent.getLongExtra(RunTrackerActivity.TIME_TAKEN, -1);
		TextView time = (TextView) findViewById(R.id.review_time);
		time.setText(TextDisplayTools.getTimeText(timeTaken));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.options_return_home) {
			startActivity(new Intent(getApplicationContext(), cmsc434.funpath.login.HomeActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}
	
	//Code for saving a runPath object to a file
	/*
	 * In the format:
	 * distance
	 * time
	 * elevation
	 * lat (tab \t) long
	 * lat (tab \t) long
	 * 		.
	 * 		.
	 * 		.
	 * 
	 */
	public void writeToFile(LatLng[] run, double dist, long time, long hilliness){
		File mediaStorageDir = new File(LoginActivity.APP_FILEPATH);

		// Create the storage directory if it does not exist (it should for the login.txt file...
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return; //PROBLEM!!
			}
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		File file = new File(mediaStorageDir.getPath(), RegisterActivity.USERNAME+"_"+ timeStamp + ".txt");
		// look for last index of "_" --> get username before it
		
		try {
			//TODO: test which of these is correct:
			FileOutputStream outputStream = new FileOutputStream(file);
			//FileOutputStream outputStream = openFileOutput(file.getAbsolutePath(), Context.MODE_PRIVATE); //TODO check this works/is the right mode
			
			final String toWrite = dist +"\n" + time + "\n" + hilliness;
			outputStream.write(toWrite.getBytes());
			
			for (int i = 0; i < run.length; ++i){
				LatLng obj = run[i];
				outputStream.write(("\n" + obj.latitude + "\t"+ obj.longitude).getBytes());
			}
			
			//outputStream.flush(); //may not be necessary
			outputStream.close();
			Log.i("FunPath", "Wrote to file: " + file.getPath());
		} catch (IOException e) {
			//TODO handle error...
			Log.i("FunPath", "Error updating "+file.getPath()+" with new registered user.");
		}
	}
	
}
