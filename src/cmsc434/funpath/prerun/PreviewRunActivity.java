package cmsc434.funpath.prerun;

import android.app.Activity;
import android.os.Bundle;
import cmsc434.funpath.R;
import cmsc434.funpath.run.RunPath;
import cmsc434.funpath.run.RunTrackerActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class PreviewRunActivity extends Activity {

	private GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_previewrun);
		RunPath[] paths = RunTrackerActivity.possiblePaths;
		
		double wantedDist = Integer.parseInt(getIntent().getStringExtra("Distance"));
		boolean inKm = getIntent().getBooleanExtra("Units",false);
		
		double wantedDistInMeters = convertToMeters(wantedDist, inKm);
		
		int indexBestPath = findBestPath(paths, wantedDistInMeters);
		
		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		map = mapFragment.getMap();
		map.setMyLocationEnabled(true);
		
		setPath(paths[indexBestPath]);
	}
	
	private int findBestPath(RunPath[] paths, double wantedDist) {
		double closestDiff = Integer.MAX_VALUE;
		int bestPos = 0;
		
		for(int i = 0; i < paths.length; i++) {
			double diff = Math.abs(wantedDist - paths[i].getPathDistanceInMeters());
			if(diff < closestDiff) {
				closestDiff = diff;
				bestPos = i;
			}
		}
		
		return bestPos;
	}
	
	private static double convertToMeters(double dist, boolean inKm) {
		if(inKm) {
			return dist * 1000;
		} else {
			return dist * 1609.34;
		}
	}
	
	public void setPath(RunPath run) {
		// clear old path
		map.clear();

		LatLng[] path = run.getPath();
		PolylineOptions pathLine = new PolylineOptions().geodesic(true).add(path);
		if (path.length > 0) {
			pathLine.add(path[0]);
		}
		map.addPolyline(pathLine);
		// show path distance
	}
}
