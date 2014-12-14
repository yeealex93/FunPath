package cmsc434.funpath.run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import cmsc434.funpath.R;
import cmsc434.funpath.login.LoginActivity;
import cmsc434.funpath.login.RegisterActivity;
import cmsc434.funpath.prerun.ConfigureRunActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class FinishRunActivity extends Activity {
	
	private GoogleMap map;
	private LatLng[] run;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finishrun);
		
		
		Intent runTrackerIntent = getIntent();
		long distanceTraveled = runTrackerIntent.getLongExtra(RunTrackerActivity.DISTANCE_TRAVELLED, 0);
		long timeTaken = runTrackerIntent.getLongExtra(RunTrackerActivity.TIME_TAKEN, 0);
		long hilliness = runTrackerIntent.getIntExtra(ConfigureRunActivity.HILLINESS, 0);
		Parcelable[] runpathArrIn= runTrackerIntent.getParcelableArrayExtra(RunTrackerActivity.RUNPATH_ARRAY);
		
		run = new LatLng[runpathArrIn.length];
		for (int i = 0; i < runpathArrIn.length; i++) {
			run[i] = (LatLng) runpathArrIn[i];
		}
		
		long distLong = runTrackerIntent.getLongExtra(RunTrackerActivity.DISTANCE_TRAVELLED, -1);
		long timeLong = runTrackerIntent.getLongExtra(RunTrackerActivity.TIME_TAKEN, -1);
		
		TextView distance = (TextView) findViewById(R.id.review_distance);
		TextView time = (TextView) findViewById(R.id.review_time);
		
		distance.setText(distLong+"");
		time.setText(timeLong+"");
		
		// Get map fragment
		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.review_map);
		map = mapFragment.getMap();
		map.setMyLocationEnabled(true);
		
		
		setPath(new RunPath(run));
		zoomToLocation();
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
	
	// Draw the path on the map.
	public void setPath(RunPath run) {
		// clear old path
		map.clear();

		LatLng[] path = run.getPath();
		PolylineOptions pathLine = new PolylineOptions().geodesic(true).add(path);
		if (path.length > 0) {
			pathLine.add(path[0]);
		}
		map.addPolyline(pathLine);
	}
	
	// Calculate average latitude and longitude of a path.
	private LatLng averagePathPoints() {
		double totalLat = 0;
		double totalLon = 0;

		for(int i = 0; i < run.length; i++) {
			totalLat += run[i].latitude;
			totalLon += run[i].longitude;
		}
		
		return new LatLng(totalLat/run.length, totalLon/run.length);
	}
	
	// Zoom to a given point on the map.
	private void zoomToLocation() {
		LatLng point = averagePathPoints();
		double lat = point.latitude;
		double lon = point.longitude;
		
		if (map != null) {
			LatLng coordinates = new LatLng(lat, lon);
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 17f));
		}
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
	public void writeToFile(LatLng[] run, long dist, long time, long hilliness){
		File mediaStorageDir = new File(LoginActivity.LOGIN_FILEPATH);

		// Create the storage directory if it does not exist (it should for the login.txt file...
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return; //PROBLEM!!
			}
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		File file = new File(mediaStorageDir.getPath() + File.separator + RegisterActivity.USERNAME+"_"+ timeStamp + ".jpg");
		// look for last index of "_" --> get username before it
		
		
		try {
			//TODO: test which of these is correct:
			FileOutputStream outputStream = openFileOutput(file.getAbsolutePath(), Context.MODE_PRIVATE); //TODO check this works/is the right mode
			
			final String toWrite = dist +"\n" + time + "\n" + hilliness;
			outputStream.write(toWrite.getBytes());
			
			for (int i = 0; i < run.length; ++i){
				LatLng obj = run[i];
				outputStream.write(("\n" + obj.latitude + "\t"+ obj.longitude).getBytes());
			}
			
			//outputStream.flush(); //may not be necessary
			outputStream.close();
		} catch (IOException e) {
			//TODO handle error...
			Log.i("FunPath", "Error updating "+file.getPath()+" with new registered user.");
		}
	}
	
}
