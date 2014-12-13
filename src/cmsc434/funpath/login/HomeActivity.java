package cmsc434.funpath.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import cmsc434.funpath.R;

// TODO AFTER other classes are done, make this activity the FunPath homescreen
// currently used to test other activities
public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		//TODO probably will have to save the username in a content provider
//		Intent callingIntent = getIntent();
//		callingIntent.getStringExtra(name);
		
//		TextView hiUsernameView = (TextView) findViewById(R.id.hi_username);
		//TODO add the username to this field to read "Hi, "+username ^
		
		Button generateRunButton = (Button) findViewById(R.id.generate_custom_run_button);
		generateRunButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		Button viewRunsButton = (Button) findViewById(R.id.view_past_runs_button);
		viewRunsButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
		
		addDebugUiControl();
	}

	// Enable access to any other activity
	private void addDebugUiControl() {
		final Spinner selectActivity = (Spinner) findViewById(R.id.selectActivity);
		addDebugSpinnerChoices(selectActivity);

		Button viewActivityButton = (Button) findViewById(R.id.viewActivityButton);

		viewActivityButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Object selectedItem = selectActivity.getSelectedItem();
				if (selectedItem != null) {
					try {
						String className = selectedItem.toString();
						Intent intent = new Intent(HomeActivity.this, Class.forName(className));
						startActivity(intent);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void addDebugSpinnerChoices(final Spinner selectActivity) {
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.activity_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		selectActivity.setAdapter(adapter);
	}
}
