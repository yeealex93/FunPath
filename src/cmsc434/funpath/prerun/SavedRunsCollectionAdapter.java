package cmsc434.funpath.prerun;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
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
import cmsc434.funpath.map.utils.MapTools;
import cmsc434.funpath.map.utils.TextDisplayTools;
import cmsc434.funpath.prerun.UndoBarController.UndoListener;
import cmsc434.funpath.run.RunPath;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

// TODO allow undo delete
public class SavedRunsCollectionAdapter extends FragmentStatePagerAdapter implements UndoListener {
	private Activity activity; // used to show ActivityToast for undo buttom message
	private List<File> files = new ArrayList<File>();

	// for undo
	private File lastDeletedFile;
	private int lastDeletedFilePosition;

	public SavedRunsCollectionAdapter(Activity activity, FragmentManager fm) {
		super(fm);
		this.activity = activity;
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
				Log.i("FileList", filename + " owned by " + username);
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

	public void remove(File file, String wholeFileText) {
//		UndoBarController mUndoBarController = new UndoBarController(activity.findViewById(R.id.undobar), SavedRunsCollectionAdapter.this);
//		mUndoBarController.showUndoBar(true, "Deleted run.", wholeFileText);
		lastDeletedFile = file;
		lastDeletedFilePosition = files.indexOf(file);

		files.remove(file);
		file.delete();
		this.notifyDataSetChanged();
	}

	@Override
	public void onUndo(Serializable token) {
		Log.i("Undo", "Undo file delete!");
		try {
			rewriteFile(lastDeletedFile, token.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		files.add(lastDeletedFilePosition, lastDeletedFile);
		this.notifyDataSetChanged();
		lastDeletedFilePosition = -1;
		lastDeletedFile = null;
	}
	
	private void rewriteFile(File file, String entireFileContents) throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.append(entireFileContents);
		writer.close();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return (position + 1) + "";
	}

	public class SavedRunFragment extends Fragment implements OnMapReadyCallback {
		private SavedRunsCollectionAdapter adapter; // used to delete
		private File file;
		private String wholeFileText; // used to restore file on undo

		private RunPath run;
		private long timeElapsed;
		private String elevationText;
		private int ele;

		public SavedRunFragment(SavedRunsCollectionAdapter adapter, File file) {
			this.adapter = adapter;
			this.file = file;
		}

		public File getFile() {
			return file;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_run, container, false);
			
			// load data
			readData(file.getPath());
			displayData(rootView);
			displayRunMap(rootView);

			// delete button
			Button deleteButton = (Button) rootView.findViewById(R.id.saved_delete_button);
			deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					adapter.remove(file, wholeFileText);
					// TODO show undo toast
				}
			});

			return rootView;
		}

		private void readData(String path) {
			
			Log.i("Reading run", "filepath: "+file.getPath());
			
			try {
				Scanner reader = new Scanner(file);
			    String line;

			    // load distance (discarded)
				line = reader.nextLine();
				wholeFileText += line + "\n";
				Log.i("Reading run", line);

				// load time
			    line = reader.nextLine();
				wholeFileText += line + "\n";
				Log.i("Reading run", line);
			    this.timeElapsed = Long.parseLong(line);

			    // load elevation
				line = reader.nextLine();
				wholeFileText += line + "\n";
				Log.i("Reading run", line);
			    ele = Integer.parseInt(line); //elevation
			    if (ele == 0) {
			    	this.elevationText = "LOW";
			    } else if (ele == 1) {
			    	this.elevationText = "MEDIUM";
			    } else { //ele == 2
			    	this.elevationText = "HIGH";
			    }
			    
			    // load runpath
			    LatLng[] rundata = getLatLngArray(reader);
			    reader.close();
			    this.run = new RunPath(rundata);
			    
			} catch (Exception e) {
				Log.i("FunPath", "EXCEPTION while reading "+file.getPath());
				e.printStackTrace();
			}
		}

		private LatLng[] getLatLngArray(Scanner reader) {
			ArrayList<LatLng> rundata = new ArrayList<LatLng>();
			String line;
			while (reader.hasNextLine()) {
				line = reader.nextLine();
				wholeFileText += line + "\n";
				Log.i("Reading run", line);
				String[] latlong = line.split("\t");
			    rundata.add(new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1])));
			}
			LatLng[] array = new LatLng[rundata.size()];
			return rundata.toArray(array);
		}

		private void displayData(View rootView) {
			TextView distView = (TextView) rootView.findViewById(R.id.saved_distance);
			distView.setText(TextDisplayTools.getDistanceText(run.getPathDistanceInMeters()));

			TextView timeView = (TextView) rootView.findViewById(R.id.saved_time);
			timeView.setText(TextDisplayTools.getTimeText(timeElapsed));

			TextView elevationView = (TextView) rootView.findViewById(R.id.saved_elevation);
			elevationView.setText(TextDisplayTools.getElevationTextShort(ele));
		}

		private void displayRunMap(View rootView) {
			// create map fragment programmatically
		    FragmentManager fm = getChildFragmentManager();
		    SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
		    if (mapFragment == null) {
		    	mapFragment = SupportMapFragment.newInstance();
		        fm.beginTransaction().replace(R.id.map, mapFragment).commit();
		    }

		    mapFragment.getMapAsync(this);
		}

		@Override
		public void onMapReady(GoogleMap map) {
			map.getUiSettings().setScrollGesturesEnabled(false); // prevent map panning, users can still zoom
			MapTools.drawPath(map, run);
			MapTools.zoomToLocation(map, run);
		}
	}
}
