package cmsc434.funpath.run;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
public class RunTrackerActivity extends Activity {
	private static final float INITIAL_ZOOM = 20f;

	private GoogleMap map;
	private FusedLocationService fusedLocationService; // gets location updates

	private RunPath currentPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_runtracker);

		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		//		mapFragment.getMapAsync(this);

		map = mapFragment.getMap();
		map.setMyLocationEnabled(true);

		fusedLocationService = new FusedLocationService(this);
		fusedLocationService.setOnLocationChangedListener(new OnLocationChangedListener() {
			private boolean firstCall = true;
			@Override
			public void onLocationChanged(Location location) {
				//				Log.i("LocationChanged", location.toString());
				if (firstCall) {
					firstCall = false;
					zoomToLocation(location);
				}
			}
		});

		RunPath aroundCsic = new RunPath(new LatLng[]{new LatLng(38.990175,-76.9365), new LatLng(38.98965,-76.93645), new LatLng(38.98967624772949, -76.93633887916803), new LatLng(38.98966399969287, -76.93621147423983), new LatLng(38.98968693218526, -76.9360800459981), new LatLng(38.98988133687924, -76.93588323891163), new LatLng(38.98997775723984, -76.93586379289627), new LatLng(38.99008355888978, -76.93588189780712), new LatLng(38.99019613584117, -76.9359677284956), new LatLng(38.99017346410841, -76.93600829690695), new LatLng(38.99016199794195, -76.93609949201345)});
		setPath(aroundCsic);

		// debug, for path generation
		showCoordinatesOnTap();
		clearPathOnLongPress();
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
			LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, INITIAL_ZOOM));
		}
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
