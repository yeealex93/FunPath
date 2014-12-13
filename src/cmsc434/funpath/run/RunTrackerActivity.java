package cmsc434.funpath.run;

import android.app.Activity;
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

		//        if (!isGooglePlayServicesAvailable()) {
		//            finish();
		//        }
		fusedLocationService = new FusedLocationService(this);
		fusedLocationService.setOnLocationChangedListener(new OnLocationChangedListener() {
			@Override
			public void onLocationChanged(Location location) {
//				Log.i("LocationChanged", location.toString());
				zoomToLocation(location);
			}
		});

		setPath(new RunPath(new LatLng[]{new LatLng(-33.866, 151.195),new LatLng(-18.142, 178.431),new LatLng(21.291, -157.821),new LatLng(37.423, -122.091)}));
	}

	public void setPath(RunPath run) {
		// clear old path
		map.clear();
		// draw new path
		this.currentPath = run;
		LatLng[] path = run.getPath();
		map.addPolyline(new PolylineOptions().geodesic(true).add(path));
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.options_logout) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
