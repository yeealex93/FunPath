package cmsc434.funpath.login;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cmsc434.funpath.R;

public class LoginActivity extends Activity {

	public static final String LOGIN_FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "files";
	public static final String LOGIN_FILENAME = "login.txt"; //Change if textfile name changes!
	
	
	public static final HashMap<String, String> loginMap = new HashMap<String, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		loadLoginData();
		
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
		
		Button loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				//TODO check credentials!
				
				Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
				startActivity(intent);
			}
			
		});
	}

	// Read in login data from login.txt and populate loginMap with username/password pairs
	private static void loadLoginData() {
		File file = new File(LOGIN_FILEPATH, LOGIN_FILENAME);
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String [] loginStr;
		    String line = br.readLine();
		    
		    while (line != null) {
		        loginStr = line.split("\t");
		        loginMap.put(loginStr[0], loginStr[1]);
		        line = br.readLine();
		    }
		    br.close();
		} catch (IOException e) {
			//TODO handle error... not sure what to do honestly. 
			//App should probably crash if it can't read login data...
			//Doing nothing for now.
			Log.i("FunPath", "IO EXCEPTION while reading login.txt.");
		} catch (Exception e) {
			//in case there's a typo/spacing error in text file and a NullPointerException is thrown
			//or other non-IO error...
			//Doing nothing again -- so playful!
			Log.i("FunPath", "Non-IO error while reading login.txt.");
		}
	}
	

}
