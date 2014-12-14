package cmsc434.funpath.prerun;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import cmsc434.funpath.R;
import cmsc434.funpath.login.LoginActivity;
import cmsc434.funpath.login.RegisterActivity;

import com.google.android.gms.maps.model.LatLng;


public class SavedRunsCollectionAdapter extends FragmentStatePagerAdapter{
	private List<File> files = new ArrayList<File>();

	public SavedRunsCollectionAdapter(FragmentManager fm) {
		super(fm);
		this.files = getAllFiles();
	}

	private List<File> getAllFiles() {
		File[] files = getFilesArray();
		Log.i("FileList", "total files = " + files.length);
		ArrayList<File> fileList = new ArrayList<File>();
		fileList.addAll(Arrays.asList(files));
		return fileList;
	}

	private File[] getFilesArray() {
		Log.i("UserName", RegisterActivity.USERNAME);
		File dir = new File(LoginActivity.APP_FILEPATH);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				//only get files that have this user's USERNAME in the name and are .txt files
				String username = getUsernameFromFileName(filename);
				Log.i("FileList", filename + " = " + username);
				return RegisterActivity.USERNAME.equals(username) && filename.endsWith(".txt");
				
			}

			private String getUsernameFromFileName(String filename) {
				final Pattern regexFileName = Pattern.compile("(.+)_\\d+_\\d+\\..+"); // username
				Matcher matcher = regexFileName.matcher(filename);
				if (matcher.matches()) {
					return matcher.group(1);
				}
				return null;
			}
		});
		if (files == null) {
			files = new File[0];
		}
		return files;
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment = new SavedRunFragment(this, files.get(i));
		return fragment;
	}

	// Allows delete to change view
    public int getItemPosition(Object item) {
    	SavedRunFragment fragment = (SavedRunFragment)item;
        int position = files.indexOf(fragment.getFile());

        if (position >= 0) {
            return position;
        } else {
            return POSITION_NONE;
        }
    }

	@Override
	public int getCount() {
		return this.files.size();
	}

	public void remove(File file) {
		files.remove(file);
		file.delete();
		this.notifyDataSetChanged();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return (position + 1) + "";
	}

	public static class SavedRunFragment extends Fragment {
		private SavedRunsCollectionAdapter adapter; // used to delete
		private File file;

		public SavedRunFragment(SavedRunsCollectionAdapter adapter, File file) {
			this.adapter = adapter;
			this.file = file;
		}

		public File getFile() {
			return file;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_photo, container, false);
			
			
			LatLng[] run = displayData(rootView, file.getPath());
			
			//TODO load map from run!!!
			//displayRunData(rootView, run);

			Button deleteButton = (Button) rootView.findViewById(R.id.saved_delete_button);
			deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					adapter.remove(file);
				}
			});
			return rootView;
		}

//		private void displayRunMap(View rootView) {
//			//TODO display map
//			//ImageView imageView = ((ImageView) rootView.findViewById(R.id.saved_map));
//			//imageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
//
//			MapFragment mapFragment = rootView.findViewById(R.id.saved_map);
//			GoogleMap map = mapFragment.getMap();
//
//			GoogleMap map.clear();
//
//			LatLng[] path = run.getPath();
//			PolylineOptions pathLine = new PolylineOptions().geodesic(true).add(path);
//			if (path.length > 0) {
//				pathLine.add(path[0]);
//			}
//			map.addPolyline(pathLine);
//		}
		
		private LatLng[] displayData(View rootView, String photoPath) {
			//read in distance, elapsed time, elevation change 
			TextView distView = (TextView) rootView.findViewById(R.id.saved_distance);
			TextView timeView = (TextView) rootView.findViewById(R.id.saved_time);
			TextView elevationView = (TextView) rootView.findViewById(R.id.saved_elevation);
			
			Log.i("LOADING RUN", "filepath: "+file.getPath()); //TODO comment out
			
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String [] latlong;
			    
				//Reading in distance, time, and elevation outside of loop
				String line = br.readLine();  //distance
			    distView.setText(distView.getText()+" " + line + "m"); //TODO check units
			    
			    line = br.readLine();  //time
			    timeView.setText(timeView.getText() + " " + line + "s"); //TODO convert units/format for readability?
			    
			    int ele = Integer.parseInt(br.readLine()); //elevation
			    if (ele == 0) {
			    	line = "LOW";
			    } else if (ele == 1) {
			    	line = "MEDIUM";
			    } else { //ele == 2
			    	line = "HIGH";
			    }
			    elevationView.setText(elevationView.getText() + " " + line);
			    
			    ArrayList<LatLng> rundata = new ArrayList<LatLng>(20);
			    //LatLng [] rundata = new LatLng[];
			    while ((line = br.readLine()) != null) {
			        latlong = line.split("\t");
			        rundata.add(new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1])));
			    }
			    br.close();
			    return (LatLng[]) rundata.toArray();
			    
			} catch (Exception e) {
				Log.i("FunPath", "EXCEPTION while reading "+file.getPath());
				e.printStackTrace();
				return null; //TODO errors will break things!!!
			}

		}

		
		
	}

}
