package cmsc434.funpath.prerun;

import cmsc434.funpath.R;
import cmsc434.funpath.run.RunTrackerActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SavedRunsActivity extends FragmentActivity {
	// Scrollable detailed image view
	private SavedRunsCollectionAdapter mDemoCollectionPagerAdapter;
	private ViewPager mViewPager;
	private String units;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery_pager);

		mDemoCollectionPagerAdapter = new SavedRunsCollectionAdapter(this, getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mDemoCollectionPagerAdapter);

		if (getIntent().hasExtra(RunTrackerActivity.UNITS_STRING)) {
			units = getIntent().getStringExtra(RunTrackerActivity.UNITS_STRING);
			mDemoCollectionPagerAdapter.setUnits(units);
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
