package cmsc434.funpath.login;

import android.app.Activity;
import android.os.Bundle;
import cmsc434.funpath.R;

public class RegisterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		//TODO after a success registration, should have either an activity or textual confirmation
		//saying "registration successful" or something to that effect.
		
	}
}
