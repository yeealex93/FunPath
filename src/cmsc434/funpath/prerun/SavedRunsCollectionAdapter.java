package cmsc434.funpath.prerun;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import cmsc434.funpath.R;


public class SavedRunsCollectionAdapter extends FragmentStatePagerAdapter{
	//TODO set the filepath
	public static final String APP_FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "funpath";
	private List<File> files = new ArrayList<File>(5);

	public SavedRunsCollectionAdapter(FragmentManager fm) {
		super(fm);
		this.files = getAllFiles();
	}

	private List<File> getAllFiles() {
		File dir = new File(APP_FILEPATH);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".png") || filename.endsWith(".jpg")
						|| filename.endsWith(".gif") || filename.endsWith(".jpeg");
			}
		});
		if (files == null) {
			return new ArrayList<File>();
		}
		ArrayList<File> fileList = new ArrayList<File>(10);
		fileList.addAll(Arrays.asList(files));
		return fileList;
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
			View rootView = inflater.inflate(R.layout.activity_savedruns, container, false);
			
			displayRunData(rootView);
			

			Button deleteButton = (Button) rootView.findViewById(R.id.saved_delete_button);
			deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					adapter.remove(file);
				}
			});
			return rootView;
		}

		private void displayRunData(View rootView) {
			//TODO display map
//			ImageView imageView = ((ImageView) rootView.findViewById(R.id.sa));
//			imageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));

			
		}

		
		
	}

}
