package cmsc434.funpath.login;

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
import cmsc434.funpath.prerun.ConfigureRunActivity;
import cmsc434.funpath.prerun.SavedRunsActivity;
import cmsc434.funpath.run.RunTrackerActivity;

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		//TODO probably will have to save the username in a content provider to keep them logged in
		// do this through startup screen (loginActivity?)
		
		// add the username to this field to read "Hi, "+username ^
		TextView hiUsernameView = (TextView) findViewById(R.id.hi_username);
		if (RegisterActivity.USERNAME != null) {
			hiUsernameView.setText(hiUsernameView.getText() + " " + RegisterActivity.USERNAME + ",");
		} 
//		else {
//		    // in theory, should have an else redirect to login screen
//			Intent i = new Intent(this, LoginActivity.class);
//			startActivity(i);
//		}
		final String units;
		if (getIntent().hasExtra(RunTrackerActivity.UNITS_STRING)) {
			units = getIntent().getStringExtra(RunTrackerActivity.UNITS_STRING);
		} else {
			units = null;
		}
		
		Button generateRunButton = (Button) findViewById(R.id.generate_custom_run_button);
		generateRunButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, ConfigureRunActivity.class);
				startActivity(intent);
			}
			
		});
		
		Button viewRunsButton = (Button) findViewById(R.id.view_past_runs_button);
		viewRunsButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, SavedRunsActivity.class);
				if (units != null) {
					intent.putExtra(RunTrackerActivity.UNITS_STRING, units);
				}
				startActivity(intent);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.options_logout) {
			RegisterActivity.USERNAME = "";
			Intent logoutIntent = new Intent(getApplicationContext(), cmsc434.funpath.login.LoginActivity.class);
			logoutIntent.putExtra(LoginActivity.LOGOUT_KEY, true);
			startActivity(logoutIntent);
		}
		return super.onOptionsItemSelected(item);
	}
	
}