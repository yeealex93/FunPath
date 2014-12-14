package cmsc434.funpath.run;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cmsc434.funpath.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

// display map of run & user position using a MapView
// TODO actually track position along path
// TODO track time, allow pause/unpause
public class RunTrackerActivity extends Activity {
	private static final float INITIAL_ZOOM = 20f;
	private static final float DISTANCE_THRESHOLD_CHECKPOINT_REACHED = 20;

	// intent extras
	public static final String RUNPATH_ARRAY = "RUNPATH_ARRAY";
	public static final String DISTANCE_TRAVELLED = "DISTANCE_TRAVELLED";
	public static final String TIME_TAKEN_MILLISECONDS = "TIME_TAKEN_MILLISECONDS";
	public static final String RUN_COMPLETED = "RUN_COMPLETED";

	private static final RunPath aroundCsic = new RunPath(new LatLng[]{new LatLng(38.990175,-76.9365), new LatLng(38.98965,-76.93645), new LatLng(38.98967624772949, -76.93633887916803), new LatLng(38.98966399969287, -76.93621147423983), new LatLng(38.98968693218526, -76.9360800459981), new LatLng(38.98988133687924, -76.93588323891163), new LatLng(38.98997775723984, -76.93586379289627), new LatLng(38.99008355888978, -76.93588189780712), new LatLng(38.99019613584117, -76.9359677284956), new LatLng(38.99017346410841, -76.93600829690695), new LatLng(38.99016199794195, -76.93609949201345)});
	private static final RunPath aroundCsicAndWindTunnel = new RunPath(new LatLng[]{new LatLng(38.98985, -76.9358), new LatLng(38.98985, -76.93586), new LatLng(38.98998, -76.93583), new LatLng(38.99014, -76.93587), new LatLng(38.99014, -76.93587), new LatLng(38.99017, -76.93587), new LatLng(38.99021, -76.93598), new LatLng(38.99018, -76.93601), new LatLng(38.99017, -76.93602), new LatLng(38.99017, -76.93613), new LatLng(38.99017, -76.93613), new LatLng(38.99018, -76.9365), new LatLng(38.9902, -76.93665), new LatLng(38.9902, -76.93665), new LatLng(38.99018, -76.93696), new LatLng(38.99017, -76.93726), new LatLng(38.99017, -76.93726), new LatLng(38.98991, -76.93725), new LatLng(38.98961, -76.93711), new LatLng(38.98961, -76.93711), new LatLng(38.98947, -76.93704), new LatLng(38.98952, -76.93686), new LatLng(38.98952, -76.93686), new LatLng(38.98961, -76.93641), new LatLng(38.98961, -76.93635), new LatLng(38.9896, -76.9363), new LatLng(38.98959, -76.93628), new LatLng(38.98964, -76.93609), new LatLng(38.98971, -76.93597), new LatLng(38.98983, -76.93587)});
	private static final RunPath acrossBridgeAndBack = new RunPath(new LatLng[]{new LatLng(38.98968 ,-76.93602), new LatLng(38.98982 ,-76.93587), new LatLng(38.98995 ,-76.93584), new LatLng(38.99013 ,-76.93587), new LatLng(38.99014 ,-76.93587), new LatLng(38.99014 ,-76.93587), new LatLng(38.99017 ,-76.93587), new LatLng(38.99014 ,-76.93579), new LatLng(38.99016 ,-76.93576), new LatLng(38.99019 ,-76.9357), new LatLng(38.99019 ,-76.93537), new LatLng(38.99038 ,-76.93489), new LatLng(38.99038 ,-76.93489), new LatLng(38.99019 ,-76.93539), new LatLng(38.99018 ,-76.93572), new LatLng(38.99015 ,-76.93578), new LatLng(38.99017 ,-76.93587), new LatLng(38.99014 ,-76.93587), new LatLng(38.99014 ,-76.93587), new LatLng(38.98998 ,-76.93583), new LatLng(38.98985 ,-76.93586), new LatLng(38.98976 ,-76.93592), new LatLng(38.98971 ,-76.93597), new LatLng(38.98968 ,-76.93602)});
	private static final RunPath cutThroughPathtoPaintBranch = new RunPath(new LatLng[]{new LatLng(38.98967 ,-76.93604), new LatLng(38.98962 ,-76.93616), new LatLng(38.98961 ,-76.93631), new LatLng(38.98961 ,-76.93637), new LatLng(38.98961 ,-76.93644), new LatLng(38.9896 ,-76.93649), new LatLng(38.98937 ,-76.93664), new LatLng(38.98937 ,-76.93664), new LatLng(38.98915 ,-76.93678), new LatLng(38.98909 ,-76.93678), new LatLng(38.9889 ,-76.9367), new LatLng(38.9889 ,-76.9367), new LatLng(38.98909 ,-76.93678), new LatLng(38.98915 ,-76.93678), new LatLng(38.98937 ,-76.93664), new LatLng(38.98937 ,-76.93664), new LatLng(38.98961 ,-76.93649), new LatLng(38.98961 ,-76.93641), new LatLng(38.98961 ,-76.93635), new LatLng(38.98959 ,-76.93628), new LatLng(38.98964 ,-76.93609), new LatLng(38.98967 ,-76.93604)});
	public static final RunPath[] possiblePaths = new RunPath[]{aroundCsic, aroundCsicAndWindTunnel, acrossBridgeAndBack, cutThroughPathtoPaintBranch};

	private TextView distanceDisplay;
	private GoogleMap map;
	private FusedLocationService fusedLocationService; // gets location updates

	private RunPath currentPath;
	private int pathIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_runtracker);

		// Basic gui code
		distanceDisplay = (TextView) findViewById(R.id.distanceDisplay);
		Button finishRunButton = (Button) findViewById(R.id.finishRunButton);
		finishRunButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				long distanceTravelled = 0; // TODO get distance travelled extra
				long timeTakenMilliseconds = 0; // TODO get time taken extra
				boolean runCompleted = false;
				Intent finishRun = new Intent(RunTrackerActivity.this, FinishRunActivity.class);
				finishRun.putExtra(RUNPATH_ARRAY, currentPath.getPath());
				finishRun.putExtra(DISTANCE_TRAVELLED, distanceTravelled);
				finishRun.putExtra(TIME_TAKEN_MILLISECONDS, timeTakenMilliseconds);
				finishRun.putExtra(RUN_COMPLETED, runCompleted);
				startActivity(finishRun);
			}
		});

		// Map code
		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		map = mapFragment.getMap();
		map.setMyLocationEnabled(true);

		fusedLocationService = new FusedLocationService(this);
		fusedLocationService.setOnLocationChangedListener(new OnLocationChangedListener() {
			private boolean firstCall = true;
			@Override
			public void onLocationChanged(Location location) {
				if (firstCall) {
					firstCall = false;
					zoomToLocation(location);
				}
				updatePositionAndDistance(location);
			}
		});

		setPath(aroundCsic);

		// debug, for path generation
		showCoordinatesOnTap();
		clearPathOnLongPress();
	}

	protected void updatePositionAndDistance(Location location) {
		LatLng curPos = getLatLng(location);
		int nextPathIndex = pathIndex + 1;
		// update path index
		LatLng nextPos = null;
		if (nextPathIndex >= currentPath.getPath().length) {
			if (currentPath.getPath().length > 0) {
				nextPos = currentPath.getPath()[0];
			}
		} else {
			nextPos = currentPath.getPath()[nextPathIndex];
		}
		if (nextPos != null && reachedPoint(nextPos, curPos)) {
			pathIndex++;
		}
		// update distance
		updateDistance(location);
	}

	private void updateDistance(Location location) {
		// TODO call at beginning?
		float totalDistance = currentPath.getPathDistanceInMeters();
		float remainingDistance = totalDistance;
		if (pathIndex >= 0) {
			LatLng curPos = getLatLng(location);
			remainingDistance = currentPath.getRemainingDistanceInMeters(pathIndex, curPos);
		}
		float distanceTravelled = Math.max(totalDistance - remainingDistance, 0);
		distanceDisplay.setText("Distance (m): " + distanceTravelled + " / " + totalDistance);
	}

	private boolean reachedPoint(LatLng pos1, LatLng pos2) {
		double distanceDifference = RunPath.getDistanceBetweenCoords(pos1, pos2);
		return distanceDifference <= DISTANCE_THRESHOLD_CHECKPOINT_REACHED;
	}

	public void setPath(RunPath run) {
		// clear old path
		map.clear();
		// draw new path
		this.currentPath = run;
		LatLng[] path = run.getPath();
		PolylineOptions pathLine = new PolylineOptions().geodesic(true).add(path);
		if (path.length > 0) {
			pathLine.add(path[0]);
		}
		map.addPolyline(pathLine);
		// show path distance
		pathIndex = -1;
//		distanceDisplay.setText("Distance (m): " + run.getPathDistanceInMeters());
	}

	protected void clearPathOnLongPress() { // debug - paths cannot be cleared by actual users
		map.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng point) {
				setPath(new RunPath(new LatLng[0]));
				Log.d("Path Cleared", "Tap to create new path");
			}
		}); 
	}

	protected void showCoordinatesOnTap() { // debug - paths cannot be modified by actual users
		map.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng point) {
				//Log.d("MapClick", "new LatLng(" + point.latitude + ", " + point.longitude + ")");
				// add to current path (debug)
				LatLng[] path = currentPath.getPath();
				LatLng[] newPath = new LatLng[path.length + 1];
				System.arraycopy(path, 0, newPath, 0, path.length);
				newPath[path.length] = point;
				setPath(new RunPath(newPath));
				// display whole path for easy copy-paste
				StringBuilder pathStr = new StringBuilder();
				pathStr.append("new RunPath(new LatLng[]{");
				boolean first = true;
				for (LatLng newPoint : newPath) {
					if (!first) {
						pathStr.append(", ");
					} else {
						first = false;
					}
					pathStr.append("new LatLng(" + newPoint.latitude + ", " + newPoint.longitude + ")");
				}
				pathStr.append("});");
				Log.d("New Path: ", pathStr.toString());
			}
		});
	}

	private void zoomToLocation(Location location) {
		if (map != null) {
			LatLng coordinates = getLatLng(location);
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, INITIAL_ZOOM));
		}
	}

	private LatLng getLatLng(Location location) {
		LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
		return coordinates;
	}

	private boolean isGooglePlayServicesAvailable() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (ConnectionResult.SUCCESS == status) {
			return true;
		} else {
			GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
			return false;
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
