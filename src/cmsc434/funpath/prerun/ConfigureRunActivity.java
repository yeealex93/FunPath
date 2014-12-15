package cmsc434.funpath.prerun;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;
import android.location.LocationListener;
import android.widget.TextView;
import cmsc434.funpath.R;
import cmsc434.funpath.login.HomeActivity;
import cmsc434.funpath.login.RegisterActivity;

public class ConfigureRunActivity extends Activity implements LocationListener {

	public static final String DISTANCE = "DISTANCE";
	public static final String HILLINESS = "HILLINESS";
	public static final String UNITS = "UNITS";
	public static final String DISTANCE_METERS = "DISTANCE_METERS";

	public static final String DISTANCE_UNITS = "DISTANCE_UNITS";
	public static final String UNITS_KEY = "_UNITS";

	private LocationManager mLocationManager;
	private double lat = 0;
	private double lon = 0;
	private TextView zip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configurerun);

		mLocationManager = (LocationManager) this.getSystemService(Service.LOCATION_SERVICE);


		final TextView distance = (TextView) findViewById(R.id.distanceField);

		final Switch kmMiSwitch = (Switch) findViewById(R.id.kmMiSwitch);
		final boolean inMetric = kmMiSwitch.isChecked(); //km = on, mi = off

		zip = (TextView) findViewById(R.id.locationField);
		zip.setText("20740");

		final SeekBar hillinessBar = (SeekBar) findViewById(R.id.hillySeekBar);
		hillinessBar.setMax(2); // 3 states for low, med, high hilliness

		Button next = (Button) findViewById(R.id.nextButton);
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				SharedPreferences settings = ConfigureRunActivity.this.getSharedPreferences(RegisterActivity.USERNAME+UNITS_KEY, MODE_PRIVATE);
				Editor editor = settings.edit();
				if (inMetric){
					editor.putString(DISTANCE_UNITS, "km");
				}else {
					editor.putString(DISTANCE_UNITS, "mi");
				}
				editor.commit();
				//String unitsPreference = settings.getString(DISTANCE_UNITS, "mi");


				Intent i = new Intent(ConfigureRunActivity.this, PreviewRunActivity.class);
				Toast.makeText(ConfigureRunActivity.this, "distance: "+distance.getText().toString(), Toast.LENGTH_LONG).show();
				i.putExtra(DISTANCE, distance.getText().toString());
				//i.putExtra(UNITS, kmMiSwitch.isChecked());
				i.putExtra(HILLINESS, hillinessBar.getProgress());
				startActivity(i);
			}

		});
	}

	protected void onResume() {
		super.onResume();

		//Location networkLocation = this.mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		//Toast.makeText(this, "resuming", Toast.LENGTH_LONG).show();
		this.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10.0f, this);		
	}

	protected void onPause() {
		mLocationManager.removeUpdates(this);
		super.onPause();
	}


	public void getAndSetZip() {
		// http://stackoverflow.com/questions/2227292/how-to-get-latitude-and-longitude-of-the-mobiledevice-in-android
		Geocoder geo = new Geocoder(this, Locale.getDefault());
		String zipCode = "";

		if((int)lat == 0 && (int)lon == 0) {
			zipCode = "";
		}

		try {
			List<Address> addresses = geo.getFromLocation(lat, lon, 1);
			zipCode = addresses.get(0).getPostalCode().toString();
		} catch (IOException e) {
			zipCode = "";
		}

		zip.setText(zipCode);

	}


	@Override
	public void onLocationChanged(Location location) {
		//		Toast.makeText(this, "changed!!", Toast.LENGTH_LONG).show();
		// TODO Auto-generated method stub
		if(location != null) {
			lon = location.getLongitude();
			lat = location.getLatitude();
			getAndSetZip();
		}
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	private class GenericTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			
			
			
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

