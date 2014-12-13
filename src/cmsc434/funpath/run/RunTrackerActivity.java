package cmsc434.funpath.run;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import cmsc434.funpath.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

// display map of run & user position using a MapView
public class RunTrackerActivity extends Activity implements OnMapReadyCallback {
	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_runtracker);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap map) {
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(-18.142, 178.431), 2));

		// Polylines are useful for marking paths and routes on the map.
		map.addPolyline(new PolylineOptions().geodesic(true)
				.add(new LatLng(-33.866, 151.195))  // Sydney
				.add(new LatLng(-18.142, 178.431))  // Fiji
				.add(new LatLng(21.291, -157.821))  // Hawaii
				.add(new LatLng(37.423, -122.091))  // Mountain View
				);
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
}
