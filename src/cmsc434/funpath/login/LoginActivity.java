package cmsc434.funpath.login;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cmsc434.funpath.R;
import cmsc434.funpath.run.ConfettiActivity;

public class LoginActivity extends Activity {

	public static final String APP_FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "funpath";
	public static final String LOGIN_FILENAME = "login.txt"; //Change if textfile name changes!
	public static final String USERNAME_KEY = "USERNAME";
	public static final String LOGOUT_KEY = "LOGOUT";
	public static final String PREFERENCES_KEY = "FUNPATH_KEY";
	
	
	private static final String FAILED_LOGIN = "Invalid username/password combination.";
	
	public static final HashMap<String, String> loginMap = new HashMap<String, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		loadLoginData();
		
		boolean logout = getIntent().getBooleanExtra(LOGOUT_KEY, false);
		if (logout) {
			LogoutUserPreferences();
		} else {
			
			//Check if a user is logged in already from previous uses. If so, go immediately to HomeActivity.
			SharedPreferences settings = this.getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
			String loggedInUser = settings.getString(USERNAME_KEY, "");
			if (!loggedInUser.equals("")) {
				RegisterActivity.USERNAME = loggedInUser;
				
				//Redirect to home page
				startActivity(new Intent(this, HomeActivity.class));
			}
		}
		
		
		Button newUserButton = (Button) findViewById(R.id.new_user_register_button);
		newUserButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO just redirects to RegisterActivity for now
				// can set up to startActivityForResult and auto-fill in their username/password
				// when returned
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
			
		});
		
		final EditText usernameView = (EditText) findViewById(R.id.login_username);
		final EditText passwordView = (EditText) findViewById(R.id.login_password);
		
		usernameView.addTextChangedListener(new GenericTextWatcher(usernameView));
		//passwordView.addTextChangedListener(new GenericTextWatcher(passwordView));
		
		Button loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				final String username = usernameView.getText().toString();
				final String password = passwordView.getText().toString();
				
				Log.i("LOGIN", "usr: "+username+",  pwd: "+password); //TODO comment out
				
				// checking login credentials!
				if (loginMap.containsKey(username) && password.equals(loginMap.get(username))) {
					
					RegisterActivity.USERNAME = username;
					
					//Set the preferences USERNAME_KEY to indicate who is logged in
					SharedPreferences settings = LoginActivity.this.getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(USERNAME_KEY, username);
					editor.commit(); //Commit the edit!
					
					Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
					startActivity(intent);
					
				} else {
					Toast.makeText(getApplicationContext(), FAILED_LOGIN, Toast.LENGTH_LONG).show();
				}
				
			}
			
		});
		
		//TODO remove: testing for confetti activity
//		Intent i = new Intent(this, ConfettiActivity.class);
//		startActivity(i);
//				
	}

	
	
	public void LogoutUserPreferences(){
		RegisterActivity.USERNAME = ""; //just in case, sets USERNAME to empty string too. This is usually done manually as well
		SharedPreferences settings = this.getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(USERNAME_KEY, ""); //logs out the user
		editor.commit(); //Commit the edit!
	}
	
	
	
	// Read in login data from login.txt and populate loginMap with username/password pairs
	private void loadLoginData() {
		File directory = new File(APP_FILEPATH);

		// Create the storage directory if it does not exist
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				Log.d("LOGIN DIRECTORY", "failed to create directory");
				return;
			}
		}
		
		//create or access existing login.txt file
		File file = new File(APP_FILEPATH, LOGIN_FILENAME);
		Log.i("LOADING", "filepath: "+file.getPath()); //TODO comment out
		
		if (!file.exists()){
			try {
				new FileOutputStream(file);
				Log.i("Login.txt CREATE", "login.txt did not exist; was just created.");
			} catch (FileNotFoundException e) {
				Log.i("Login.txt CREATE", "Failed to create new login.txt file.");
				e.printStackTrace();
			}
		}
		
		try {
			Scanner kb = new Scanner(file);
			//BufferedReader br = new BufferedReader(new FileReader(file));
			
			String [] loginStr;
			while (kb.hasNextLine()){
				String line = kb.nextLine();
				if (!line.trim().equals("")){
					//In case there are random empty newlines, they are ignored
					
			        loginStr = line.split("\t");
			        Log.i("LOGIN LOOP", "usr: "+loginStr[0] +",  pw: "+loginStr[1]); //TODO comment out
			        loginMap.put(loginStr[0], loginStr[1]);
				}
		    }
			
		    kb.close();
		} catch (IOException e) {
			//TODO handle error... not sure what to do honestly. 
			//App should probably crash if it can't read login data...
			//Doing nothing for now.
			Log.i("FunPath", "IO EXCEPTION while reading login.txt.");
			e.printStackTrace();
			
		} catch (Exception e) {
			//in case there's a typo/spacing error in text file and a NullPointerException is thrown
			//or other non-IO error...
			//Doing nothing again -- so playful!
			Log.i("FunPath", "Non-IO error while reading login.txt.");
			e.printStackTrace();
		}
	}

//	@Override
//	public boolean onKey(View v, int keyCode, KeyEvent event) {
//		Toast.makeText(this, "Keypressed " +keyCode, Toast.LENGTH_SHORT).show();
//		
//		if(v.equals(this.findViewById(R.id.login_username)) && keyCode==KeyEvent.KEYCODE_ENTER) {
//			Toast.makeText(this, "enter pressed", Toast.LENGTH_SHORT).show();
//			this.findViewById(R.id.login_password).requestFocus();
//			return true;
//		}
//		return false;
//	}
	
	// http://stackoverflow.com/questions/5702771/how-to-use-single-textwatcher-for-multiple-edittexts/6172024#6172024
	private class GenericTextWatcher implements TextWatcher {

	    private View view;
	    private GenericTextWatcher(View view) {
	        this.view = view;
	    }

	    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
	    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

	    public void afterTextChanged(Editable editable) {
	        String text = editable.toString();
	        EditText t = (EditText) view;
	        
	        switch(view.getId()){
//	            case R.id.login_password:
//	                if(text.length() > 0 && text.substring(text.length()-1, text.length()).equals("\n")) {
//	                	t.setText(text.substring(0, text.length()-1));
//	                	Button login = (Button) findViewById(R.id.login_button);
//	                	login.performClick();
//	                }
//	            	break;
	            	
	            case R.id.login_username:
	                if(text.length() > 0 && text.substring(text.length()-1, text.length()).equals("\n")) {
	                	t.setText(text.substring(0, text.length()-1));
	                	findViewById(R.id.login_password).requestFocus();
	                }
	                break;
	        }
	    }
	}

}
