package cmsc434.funpath.prerun;

import java.util.ArrayList;

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
import cmsc434.funpath.map.utils.TextDisplayTools;
import cmsc434.funpath.run.RunPath;
import cmsc434.funpath.run.RunTrackerActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class PreviewRunActivity extends Activity {

	private GoogleMap map;
	private final ArrayList<Integer> notAllowed = new ArrayList<Integer>();
	private String units;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_previewrun);

		// Get extras
		final double wantedDist = Double.parseDouble(getIntent().getStringExtra(ConfigureRunActivity.DISTANCE));
		final boolean inKm = getIntent().getBooleanExtra(ConfigureRunActivity.UNITS, false);
		if(inKm) {
			units = "km";
		} else {
			units = "mi";
		}
		
		final int hilliness = getIntent().getIntExtra(ConfigureRunActivity.HILLINESS, 0);

		// Put everything in meters
		final double wantedDistInMeters = convertToMeters(wantedDist, inKm);

		// Find the path that has a length closest to what the user wants
		final RunPath[] paths = RunTrackerActivity.possiblePaths;
		final RunPath bestPath = findBestPath(paths, wantedDistInMeters);
		for (RunPath path : paths) {
			Log.i("Path", "Dist = " + path.getPathDistanceInMeters());
		}

		Button generateRun = (Button) findViewById(R.id.preview_generate_run);
		generateRun.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(notAllowed.size() == paths.length) {
					notAllowed.clear();
				}
				
				RunPath nextBest = findBestPath(paths, wantedDistInMeters);
				afterBestPathFound(nextBest, hilliness, units);
			}

		});

		TextView hillyView = (TextView) findViewById(R.id.preview_elevation);
		hillyView.setText(TextDisplayTools.getElevationText(hilliness));
		
		afterBestPathFound(bestPath, hilliness, units);
	}

	private void afterBestPathFound(final RunPath bestPath, final int hilliness, final String units){
		
		
		// Set up button listener
		Button startRun = (Button) findViewById(R.id.preview_begin_run);
		startRun.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent startIntent = new Intent(PreviewRunActivity.this, RunTrackerActivity.class);

				//any of these needed?
				//startIntent.putExtra(ConfigureRunActivity.DISTANCE_METERS, wantedDistInMeters);
				//startIntent.putExtra(ConfigureRunActivity.UNITS, inKm);
				//startIntent.putExtra(ConfigureRunActivity.DISTANCE, wantedDist);

				startIntent.putExtra(ConfigureRunActivity.HILLINESS, hilliness);
				startIntent.putExtra(ConfigureRunActivity.UNITS, units);
				startIntent.putExtra(RunTrackerActivity.RUNPATH_ARRAY, bestPath.getPath());

				startActivity(startIntent);
			}

		});
		
		// Set our text for the distance and elevation text areas
		TextView distanceView = (TextView) findViewById(R.id.preview_distance);
		distanceView.setText(TextDisplayTools.getDistanceText(bestPath.getPathDistanceInMeters(),units));

		// Get map fragment
		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.preview_map);
		map = mapFragment.getMap();
		map.setMyLocationEnabled(true);

		// Find average point of trail to zoom in on
		LatLng avgPt = averagePathPoints(bestPath);
		zoomToLocation(avgPt.latitude, avgPt.longitude);

		// Draw the path
		setPath(bestPath);
	}


	// Find the path with length closest to what the user wants to run.
	private RunPath findBestPath(RunPath[] paths, double wantedDist) {
		double closestDiff = Integer.MAX_VALUE;
		int bestPos = 0;

		for (int i = 0; i < paths.length; i++) {
			double diff = Math.abs(wantedDist - paths[i].getPathDistanceInMeters());
			if (diff < closestDiff && !notAllowed.contains(i)) {
				closestDiff = diff;
				bestPos = i;
			}
		}
		
		notAllowed.add(bestPos);
		return paths[bestPos];
	}

	// Convert miles and kilometers to miles.
	private static double convertToMeters(double dist, boolean inKm) {
		if(inKm) {
			return dist * 1000;
		} else {
			return dist * 1609.34;
		}
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
	private LatLng averagePathPoints(RunPath run) {
		double totalLat = 0;
		double totalLon = 0;

		LatLng[] path = run.getPath();
		for(int i = 0; i < path.length; i++) {
			totalLat += path[i].latitude;
			totalLon += path[i].longitude;
		}

		return new LatLng(totalLat/path.length, totalLon/path.length);
	}

	// Zoom to a given point on the map.
	private void zoomToLocation(double lat, double lon) {
		if (map != null) {
			LatLng coordinates = new LatLng(lat, lon);
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 17f));
		}
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
}
