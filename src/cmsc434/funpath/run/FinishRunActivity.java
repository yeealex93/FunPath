package cmsc434.funpath.run;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import cmsc434.funpath.R;
import cmsc434.funpath.prerun.ConfigureRunActivity;

public class FinishRunActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finishrun);
		
		
		Intent runTrackerIntent = getIntent();
		runTrackerIntent.getParcelableArrayExtra(RunTrackerActivity.RUNPATH_ARRAY);
		runTrackerIntent.getLongExtra(RunTrackerActivity.DISTANCE_TRAVELLED, 0);
		runTrackerIntent.getLongExtra(RunTrackerActivity.TIME_TAKEN_MILLISECONDS, 0);
		runTrackerIntent.getIntExtra(ConfigureRunActivity.HILLINESS, 0);
		
		//TODO save the distance, time, elevation change
		
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
