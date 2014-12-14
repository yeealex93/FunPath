package cmsc434.funpath.prerun;

import cmsc434.funpath.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class SavedPhotoActivity extends FragmentActivity {
	// Scrollable detailed image view
	private SavedRunsCollectionAdapter mDemoCollectionPagerAdapter;
	private ViewPager mViewPager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery_pager);

		mDemoCollectionPagerAdapter = new SavedRunsCollectionAdapter(this, getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mDemoCollectionPagerAdapter);

	}
}
