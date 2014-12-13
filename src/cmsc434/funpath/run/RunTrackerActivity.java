package cmsc434.funpath.run;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import cmsc434.funpath.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

// display map of run & user position using a MapView
public class RunTrackerActivity extends Activity implements OnMapReadyCallback {
	private static final float INITIAL_ZOOM = 20f;

	private FollowMeLocationSource locationSource;
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_runtracker);

		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		Toast.makeText(RunTrackerActivity.this, "onCreate", Toast.LENGTH_SHORT).show();
		map = mapFragment.getMap();
		map.setMyLocationEnabled(true);
		addLocationChangeListener(map);

		locationSource = new FollowMeLocationSource(this);
	}

	// Adapted from: http://stackoverflow.com/a/23943908/364154
	private void addLocationChangeListener(final GoogleMap map) {
		map.setLocationSource(locationSource);
		//		GoogleMap.OnMyLocationChangeListener locationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
		//			@Override
		//			public void onMyLocationChange(Location location) {
		//				Toast.makeText(RunTrackerActivity.this, "onMyLocationChange", Toast.LENGTH_SHORT).show();
		//				LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
		//				map.addMarker(new MarkerOptions().position(loc));
		//				if (map != null) {
		//					map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, INITIAL_ZOOM));
		//				}
		//				map.setOnMyLocationChangeListener(null);
		//			}
		//		};
		//		map.setOnMyLocationChangeListener(locationChangeListener);
	}

	@Override
	public void onMapReady(GoogleMap map) {
		//		map.moveCamera(CameraUpdateFactory.newLatLngZoom(
		//				new LatLng(-18.142, 178.431), 2));
		//
		//		// Polylines are useful for marking paths and routes on the map.
		//		map.addPolyline(new PolylineOptions().geodesic(true)
		//				.add(new LatLng(-33.866, 151.195))  // Sydney
		//				.add(new LatLng(-18.142, 178.431))  // Fiji
		//				.add(new LatLng(21.291, -157.821))  // Hawaii
		//				.add(new LatLng(37.423, -122.091))  // Mountain View
		//				);
		//		zoomToCurrentLocation(map);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Code adapted from: http://stackoverflow.com/a/14305851/364154
	/* Our custom LocationSource. 
	 * We register this class to receive location updates from the Location Manager
	 * and for that reason we need to also implement the LocationListener interface. */
	private class FollowMeLocationSource implements LocationSource, LocationListener {

		private OnLocationChangedListener mListener;
		private LocationManager locationManager;
		private final Criteria criteria = new Criteria();
		private String bestAvailableProvider;
		/* Updates are restricted to one every 10 seconds, and only when
		 * movement of more than 10 meters has been detected.*/
		private final int minTime = 10000;     // minimum time interval between location updates, in milliseconds
		private final int minDistance = 10;    // minimum distance between location updates, in meters
		private Context context;

		public FollowMeLocationSource(Context context) {
			this.context = context;
			// Get reference to Location Manager
			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

			// Specify Location Provider criteria
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			criteria.setAltitudeRequired(true);
			criteria.setBearingRequired(true);
			criteria.setSpeedRequired(true);
			criteria.setCostAllowed(true);
		}

		private void getBestAvailableProvider() {
			/* The preffered way of specifying the location provider (e.g. GPS, NETWORK) to use 
			 * is to ask the Location Manager for the one that best satisfies our criteria.
			 * By passing the 'true' boolean we ask for the best available (enabled) provider. */
			bestAvailableProvider = locationManager.getBestProvider(criteria, true);
		}

		/* Activates this provider. This provider will notify the supplied listener
		 * periodically, until you call deactivate().
		 * This method is automatically invoked by enabling my-location layer. */
		@Override
		public void activate(OnLocationChangedListener listener) {
			Log.i("LocationSource", "activate");
			// We need to keep a reference to my-location layer's listener so we can push forward
			// location updates to it when we receive them from Location Manager.
			mListener = listener;

			// Request location updates from Location Manager
			if (bestAvailableProvider != null) {
				locationManager.requestLocationUpdates(bestAvailableProvider, minTime, minDistance, this);
			} else {
				Toast.makeText(context, "No Location Providers currently available.", Toast.LENGTH_SHORT).show();
			}
		}

		/* Deactivates this provider.
		 * This method is automatically invoked by disabling my-location layer. */
		@Override
		public void deactivate() {
			Log.i("LocationSource", "deactivate");
			// Remove location updates from Location Manager
			locationManager.removeUpdates(this);

			mListener = null;
		}

		@Override
		public void onLocationChanged(Location location) {
			/* Push location updates to the registered listener..
			 * (this ensures that my-location layer will set the blue dot at the new/received location) */
			if (mListener != null) {
				mListener.onLocationChanged(location);
			}

			/* ..and Animate camera to center on that location !
			 * (the reason for we created this custom Location Source !) */
			map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
		}

		@Override
		public void onStatusChanged(String s, int i, Bundle bundle) { }
		@Override
		public void onProviderEnabled(String s) { }
		@Override
		public void onProviderDisabled(String s) { }
	}
}
