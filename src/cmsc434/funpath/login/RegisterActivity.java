package cmsc434.funpath.login;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cmsc434.funpath.R;

public class RegisterActivity extends Activity {

	public static String USERNAME;
	
	//TODO Are these error messages correct for design principles?
	private	final static String PASSWORDS_DONT_MATCH = "Passwords don't match!";
	private	final static String USERNAME_BLANK = "Please enter a username.";
	private	final static String DUPLICATE_USERNAME = "Username already exists.";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		//TODO after a success registration, should have either an activity or textual confirmation
		//saying "registration successful" or something to that effect.
		
		final EditText usernameView = (EditText) findViewById(R.id.register_username);
		final EditText passwordView = (EditText) findViewById(R.id.register_password);
		final EditText passwordConfirmView = (EditText) findViewById(R.id.register_password_confirm);
		
		Button returnButton = (Button) findViewById(R.id.return_to_login_button);
		returnButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
				startActivity(intent);
				//If we assume this is only called from LoginActivitiy, I think "finish();" may suffice...
			}
			
		});
		
		Button registerButton = (Button) findViewById(R.id.register_button);
		registerButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//TODO error checking all set?
				String username = usernameView.getText().toString();
				if (LoginActivity.loginMap.containsKey(username)) {
					
					//if the username already exists
					Toast.makeText(RegisterActivity.this, DUPLICATE_USERNAME, Toast.LENGTH_LONG).show();
					
				} else if (username.equals("")){
					
					//if username is empty
					Toast.makeText(RegisterActivity.this, USERNAME_BLANK, Toast.LENGTH_LONG).show();
					
				} else if (!passwordView.getText().toString().equals(passwordConfirmView.getText().toString())) {
					
					//if passwords are not the same
					Toast.makeText(RegisterActivity.this, PASSWORDS_DONT_MATCH, Toast.LENGTH_LONG).show();
					
				} else {
					
					//successful registration
					
					//1. set global USERNAME variable (indicating the logged in user)
					//2. update loginMap 
					//3. update login.txt file
					
					USERNAME = username;
					
					//TODO check that "LoginActivity" is the proper name string - not working yet!
					//Set the preferences USERNAME_KEY to indicate who is logged in
					SharedPreferences settings = RegisterActivity.this.getSharedPreferences(LoginActivity.PREFERENCES_KEY, MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(LoginActivity.USERNAME_KEY, username);
					editor.commit(); //Commit the edit!
					
					LoginActivity.loginMap.put(username, passwordView.getText().toString());
					updateLoginData(username, passwordView.getText().toString());
					
					Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
					startActivity(intent);
				}
			}
			
		});
	}
	
	// Write newUsername/newPassword pair to login.txt
	public void updateLoginData(String newUsername, String newPassword) {
		//File file = new File(getApplicationContext().getFilesDir(), LoginActivity.LOGIN_FILENAME);
		File dir = new File(LoginActivity.APP_FILEPATH);

		// Create the storage directory if it does not exist (it should for the login.txt file...
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				return; //PROBLEM!!
			}
		}
		File file = new File(dir.getPath(), LoginActivity.LOGIN_FILENAME);
		
		try {
			//TODO: test which of these is correct:
			FileOutputStream outputStream;
			if (file.exists()){
				outputStream = new FileOutputStream(file, true);
				Log.i("REGISTER FILE", "login.txt exists, open for appending.");
			} else {
				outputStream = new FileOutputStream(file);
				Log.i("REGISTER FILE", "login.txt does NOT exist, creating it.");
			}
			//FileOutputStream outputStream = openFileOutput(LoginActivity.LOGIN_FILENAME, Context.MODE_APPEND);
			
			final String toWrite = newUsername + "\t" + newPassword + "\n";
			outputStream.write(toWrite.getBytes());
			
			//outputStream.flush(); //may not be necessary
			outputStream.close();
		} catch (IOException e) {
			//TODO handle error...
			Log.i("FunPath", "Error updating login.txt with new registered user.");
		}
	}
}
