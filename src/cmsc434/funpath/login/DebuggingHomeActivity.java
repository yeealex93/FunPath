package cmsc434.funpath.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import cmsc434.funpath.R;

// This class is used as a temporary starting activity used to navigate to any other activity
// TODO Modify AndroidManifest to remove this as the starting point!!!
public class DebuggingHomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debugging_home);

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
						Intent intent = new Intent(DebuggingHomeActivity.this, Class.forName(className));
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
