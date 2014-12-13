package cmsc434.funpath.run;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;

// Code adapted from: http://javapapers.com/android/android-location-fused-provider/
public class FusedLocationService implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	private static final long INTERVAL = 1000 * 30;
	private static final long FASTEST_INTERVAL = 1000 * 5;

	private LocationRequest locationRequest;
	private GoogleApiClient googleApiClient;
	private Location location;
	private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;

	private OnLocationChangedListener listener;

	public FusedLocationService(Context context) {
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(INTERVAL);
		locationRequest.setFastestInterval(FASTEST_INTERVAL);

		googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

		if (googleApiClient != null) {
			googleApiClient.connect();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i("FusedLocationService", "Connected to Google Play Services");
		Location currentLocation = fusedLocationProviderApi.getLastLocation(googleApiClient);
		location = currentLocation;
		fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
		if (listener != null) {
			listener.onLocationChanged(location); // notify listener
		}
	}

	public void setOnLocationChangedListener(OnLocationChangedListener listener) {
		this.listener = listener;
	}

	public Location getLocation() {
		return this.location;
	}

	@Override
	public void onConnectionSuspended(int i) { }
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) { }
}
