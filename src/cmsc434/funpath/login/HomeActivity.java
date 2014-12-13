package cmsc434.funpath.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cmsc434.funpath.R;

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
			hiUsernameView.setText(hiUsernameView.getText() + RegisterActivity.USERNAME);
		} 
//		else {
//		    // in theory, should have an else redirect to login screen
//			Intent i = new Intent(this, LoginActivity.class);
//			startActivity(i);
//		}
		
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
		
		
	}

}