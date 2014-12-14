package cmsc434.funpath.run;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import cmsc434.funpath.R;

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
		Parcelable[] runpathArrIn= runTrackerIntent.getParcelableArrayExtra(RunTrackerActivity.RUNPATH_ARRAY);
		
		run = new LatLng[runpathArrIn.length];
		for (int i = 0; i < runpathArrIn.length; i++) {
			run[i] = (LatLng) runpathArrIn[i];
		}
		
		long distLong = runTrackerIntent.getLongExtra(RunTrackerActivity.DISTANCE_TRAVELLED, -1);
		long timeLong = runTrackerIntent.getLongExtra(RunTrackerActivity.TIME_TAKEN_MILLISECONDS, -1);
		
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
}
