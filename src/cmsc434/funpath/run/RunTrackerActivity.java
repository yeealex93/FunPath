package cmsc434.funpath.run;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import cmsc434.funpath.R;
import cmsc434.funpath.map.utils.MapTools;
import cmsc434.funpath.map.utils.TextDisplayTools;
import cmsc434.funpath.prerun.ConfigureRunActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

// display map of run & user position using a MapView, only allows one direction of following path through checkpoints
// TODO show progress bar
public class RunTrackerActivity extends Activity {
	public static final boolean DEBUG_TOOLS_ENABLED = true; // allows path adding and clearing, disable for released version

	private static final float INITIAL_ZOOM = 20f;
	private static final float DISTANCE_THRESHOLD_CHECKPOINT_REACHED = 20;

	// intent extras
	public static final String RUNPATH_ARRAY = "RUNPATH_ARRAY";
	public static final String DISTANCE_TRAVELLED = "DISTANCE_TRAVELLED";
	public static final String TIME_TAKEN = "TIME_TAKEN_MILLISECONDS";
	public static final String RUN_COMPLETED = "RUN_COMPLETED";

	private static final RunPath aroundCsic = new RunPath(new LatLng[]{new LatLng(38.990175,-76.9365), new LatLng(38.98965,-76.93645), new LatLng(38.98967624772949, -76.93633887916803), new LatLng(38.98966399969287, -76.93621147423983), new LatLng(38.98968693218526, -76.9360800459981), new LatLng(38.98988133687924, -76.93588323891163), new LatLng(38.98997775723984, -76.93586379289627), new LatLng(38.99008355888978, -76.93588189780712), new LatLng(38.99019613584117, -76.9359677284956), new LatLng(38.99017346410841, -76.93600829690695), new LatLng(38.99016199794195, -76.93609949201345)});
	private static final RunPath aroundCsicAndWindTunnel = new RunPath(new LatLng[]{new LatLng(38.98985, -76.9358), new LatLng(38.98985, -76.93586), new LatLng(38.98998, -76.93583), new LatLng(38.99014, -76.93587), new LatLng(38.99014, -76.93587), new LatLng(38.99017, -76.93587), new LatLng(38.99021, -76.93598), new LatLng(38.99018, -76.93601), new LatLng(38.99017, -76.93602), new LatLng(38.99017, -76.93613), new LatLng(38.99017, -76.93613), new LatLng(38.99018, -76.9365), new LatLng(38.9902, -76.93665), new LatLng(38.9902, -76.93665), new LatLng(38.99018, -76.93696), new LatLng(38.99017, -76.93726), new LatLng(38.99017, -76.93726), new LatLng(38.98991, -76.93725), new LatLng(38.98961, -76.93711), new LatLng(38.98961, -76.93711), new LatLng(38.98947, -76.93704), new LatLng(38.98952, -76.93686), new LatLng(38.98952, -76.93686), new LatLng(38.98961, -76.93641), new LatLng(38.98961, -76.93635), new LatLng(38.9896, -76.9363), new LatLng(38.98959, -76.93628), new LatLng(38.98964, -76.93609), new LatLng(38.98971, -76.93597), new LatLng(38.98983, -76.93587)});
	private static final RunPath acrossBridgeAndBack = new RunPath(new LatLng[]{new LatLng(38.98968 ,-76.93602), new LatLng(38.98982 ,-76.93587), new LatLng(38.98995 ,-76.93584), new LatLng(38.99013 ,-76.93587), new LatLng(38.99014 ,-76.93587), new LatLng(38.99014 ,-76.93587), new LatLng(38.99017 ,-76.93587), new LatLng(38.99014 ,-76.93579), new LatLng(38.99016 ,-76.93576), new LatLng(38.99019 ,-76.9357), new LatLng(38.99019 ,-76.93537), new LatLng(38.99038 ,-76.93489), new LatLng(38.99038 ,-76.93489), new LatLng(38.99019 ,-76.93539), new LatLng(38.99018 ,-76.93572), new LatLng(38.99015 ,-76.93578), new LatLng(38.99017 ,-76.93587), new LatLng(38.99014 ,-76.93587), new LatLng(38.99014 ,-76.93587), new LatLng(38.98998 ,-76.93583), new LatLng(38.98985 ,-76.93586), new LatLng(38.98976 ,-76.93592), new LatLng(38.98971 ,-76.93597), new LatLng(38.98968 ,-76.93602)});
	private static final RunPath cutThroughPathtoPaintBranch = new RunPath(new LatLng[]{new LatLng(38.98967 ,-76.93604), new LatLng(38.98962 ,-76.93616), new LatLng(38.98961 ,-76.93631), new LatLng(38.98961 ,-76.93637), new LatLng(38.98961 ,-76.93644), new LatLng(38.9896 ,-76.93649), new LatLng(38.98937 ,-76.93664), new LatLng(38.98937 ,-76.93664), new LatLng(38.98915 ,-76.93678), new LatLng(38.98909 ,-76.93678), new LatLng(38.9889 ,-76.9367), new LatLng(38.9889 ,-76.9367), new LatLng(38.98909 ,-76.93678), new LatLng(38.98915 ,-76.93678), new LatLng(38.98937 ,-76.93664), new LatLng(38.98937 ,-76.93664), new LatLng(38.98961 ,-76.93649), new LatLng(38.98961 ,-76.93641), new LatLng(38.98961 ,-76.93635), new LatLng(38.98959 ,-76.93628), new LatLng(38.98964 ,-76.93609), new LatLng(38.98967 ,-76.93604)});
	private static final RunPath nearSouthCampus = new RunPath(new LatLng[]{new LatLng(38.982477163899595, -76.94291733205318), new LatLng(38.98247950950659, -76.94345377385616), new LatLng(38.982721627856165, -76.94347623735666)});
	public static final RunPath[] possiblePaths = new RunPath[]{aroundCsic, aroundCsicAndWindTunnel, acrossBridgeAndBack, cutThroughPathtoPaintBranch};

	private TextView distanceDisplay, timeDisplay;
	private GoogleMap map;
	private FusedLocationService fusedLocationService; // gets location updates

	private RunPath currentPath;
	private int pathIndex; // last checkpoint reached, -1 = hasn't started
	private Marker nextCheckpoint; // located at currentPath.getPath()[pathIndex + 1], except when pathIndex == path.length, then it's [0]
	private double distanceTravelled; // in meters
	private long timeElapsedSeconds; // only starts counting after reaching checkpoint
	private boolean paused = false;

	private Button pauseRunButton;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_runtracker);

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

		// load from intent
		final int elevation = getIntent().getIntExtra(ConfigureRunActivity.HILLINESS, 0);

		// load path
		Parcelable[] runPathArray = getIntent().getParcelableArrayExtra(RUNPATH_ARRAY);
		if (runPathArray == null) {
			//		setPath(aroundCsic);
			setPath(nearSouthCampus);
		} else {
			setPath(new RunPath(getIntent()));
		}

		// Basic gui code
		distanceDisplay = (TextView) findViewById(R.id.distanceDisplay);
		timeDisplay = (TextView) findViewById(R.id.timeDisplay);

		pauseRunButton = (Button) findViewById(R.id.pauseRunButton);
		pauseRunButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paused = !paused;
				if (paused) {
					pauseRunButton.setText("Resume Run");
				} else {
					pauseRunButton.setText("Pause Run");
				}
			}
		});

		Button finishRunButton = (Button) findViewById(R.id.finishRunButton);
		finishRunButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean runCompleted = false;
				Intent finishRun = new Intent(RunTrackerActivity.this, FinishRunActivity.class);
				finishRun.putExtra(RUNPATH_ARRAY, currentPath.getPath());
				finishRun.putExtra(DISTANCE_TRAVELLED, distanceTravelled);
				finishRun.putExtra(TIME_TAKEN, timeElapsedSeconds);
				finishRun.putExtra(RUN_COMPLETED, runCompleted); // TODO currently unused
				finishRun.putExtra(ConfigureRunActivity.HILLINESS, elevation);
				startActivity(finishRun);
			}
		});

		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		// time update
		new Thread(new UpdateTimeEverySecond()).start();   

		// debug, for path generation
		if (DEBUG_TOOLS_ENABLED) {
			showCoordinatesOnTap();
			clearPathOnLongPress();
		}
	}

	protected void updatePositionAndDistance(Location location) {
		LatLng curPos = getLatLng(location);
		int nextPathIndex = pathIndex + 1;
		// update path index
		LatLng nextPos = null;
		if (nextPathIndex == currentPath.getPath().length) {
			if (currentPath.getPath().length > 0) {
				nextPos = currentPath.getPath()[0];
			}
		} else if (nextPathIndex < currentPath.getPath().length) {
			nextPos = currentPath.getPath()[nextPathIndex];
		} else {
			nextPos = null;
		}
		if (nextPos != null && reachedPoint(nextPos, curPos)) {
			pathIndex++;
			updateNextCheckpoint();
		}
		// update distance
		updateDistance(location);
		boolean isComplete = pathIndex >= currentPath.getPath().length;
		if (isComplete) {
			paused = true;
			pauseRunButton.setEnabled(false);
			// TODO notify user that they completed
		}
	}

	private void updateDistance(Location curLocation) {
		if (paused) {
			return; // don't update distance when paused
		}
		float totalDistance = currentPath.getPathDistanceInMeters();
		float remainingDistance = getRemainingDistance(curLocation);
		// update distance travelled
		float newDistanceTravelled = Math.max(totalDistance - remainingDistance, 0);
		if (distanceTravelled < newDistanceTravelled) {
			distanceTravelled = newDistanceTravelled;
		}
		progressBar.setProgress((int) (distanceTravelled * 100 / totalDistance));
		distanceDisplay.setText(TextDisplayTools.getDistanceText(distanceTravelled, totalDistance));
	}

	private float getRemainingDistance(Location curLocation) {
		int nextPathIndex = pathIndex + 1;
		LatLng curPos = getLatLng(curLocation);
		float remainingDistance = currentPath.getRemainingDistanceInMeters(nextPathIndex, curPos);
		// sanity check
		if (currentPath.getPath().length == 0) {
			return 0;
		}
		float worstDistance;
		if (pathIndex >= 0 && pathIndex < currentPath.getPath().length) {
			LatLng exactPos = currentPath.getPath()[pathIndex];
			worstDistance = currentPath.getRemainingDistanceInMeters(nextPathIndex, exactPos);
		} else if (pathIndex < 0) {
			worstDistance = remainingDistance;
		} else {// if (pathIndex == currentPath.getPath().length) {
			worstDistance = 0;
		}
		Log.i("Dist", "Remaining: " + remainingDistance + ", Worst: " + worstDistance);
		remainingDistance = Math.min(worstDistance, remainingDistance);
		return remainingDistance;
	}

	private boolean reachedPoint(LatLng pos1, LatLng pos2) {
		if (paused) {
			return false; // can't go to checkpoint while paused
		}
		double distanceDifference = RunPath.getDistanceBetweenCoords(pos1, pos2);
		return distanceDifference <= DISTANCE_THRESHOLD_CHECKPOINT_REACHED;
	}

	public void setPath(RunPath run) {
		MapTools.drawPath(map, run);
		this.currentPath = run;
		// show path distance
		pathIndex = -1;
		distanceTravelled = 0;
		timeElapsedSeconds = 0;
		updateNextCheckpoint();
	}

	private void updateNextCheckpoint() {
		LatLng[] path = this.currentPath.getPath();
		if (path.length == 0) { // remove checkpoint
			setNextCheckpoint(null);
			return;
		}

		int nextPathIndex = pathIndex + 1;
		Log.i("Checkpoint", "Next path index: " + nextPathIndex + " of " + path.length);
		if (nextPathIndex == path.length) {
			Log.i("Checkpoint", "Final stretch!");
			nextPathIndex = 0;
		} else if (nextPathIndex > path.length) { // remove checkpoint - done
			Log.i("Checkpoint", "Done");
			setNextCheckpoint(null);
			return;
		}
		LatLng nextCheckpointCoords = path[nextPathIndex];
		setNextCheckpoint(nextCheckpointCoords);
	}

	private void setNextCheckpoint(LatLng nextCheckpointCoords) {
		if (nextCheckpoint != null) {
			nextCheckpoint.remove();
		}
		if (nextCheckpointCoords == null) {
			return;
		} else {
			Marker newCheckpoint = map.addMarker(new MarkerOptions()
	                .position(nextCheckpointCoords)
	                .draggable(false).visible(true));
			nextCheckpoint = newCheckpoint;
		}
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

//	private boolean isGooglePlayServicesAvailable() {
//		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//		if (ConnectionResult.SUCCESS == status) {
//			return true;
//		} else {
//			GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
//			return false;
//		}
//	}

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

	public void updateTime() {
		runOnUiThread(new Runnable() {
			public void run() {
				try {
					boolean runStarted = pathIndex >= 0; // || true for debug
					String curTime;
					if (!runStarted) {
						curTime = "Run not started";
					} else {
						if (!paused) {
							timeElapsedSeconds++;
						}
						curTime = TextDisplayTools.getTimeText(timeElapsedSeconds);
					}
					timeDisplay.setText(curTime);
				} catch (Exception e) {}
			}
		});
	}

	private class UpdateTimeEverySecond implements Runnable{
		private Timer timer;

		public void run() {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					updateTime();
				}
			}, 0, 1000);
		}
	}
}
