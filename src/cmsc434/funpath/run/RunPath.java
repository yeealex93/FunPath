package cmsc434.funpath.run;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

// FunPath
// stores a path as a list of (lat, lng) coords, paths loop with the starting point being the ending point
public class RunPath {
	private final LatLng[] path;

	public RunPath(LatLng[] path) {
		this.path = path;
	}

	public LatLng[] getPath() {
		return path;
	}

	public float getRemainingDistanceInMeters(int nextIndex, LatLng curLoc) { // distance to end of path, given next target & current location
		float totalDistance = 0;
		LatLng lastPoint = curLoc;
		for (int i = nextIndex; i < path.length; i++) {
			LatLng curPoint = path[i];
			totalDistance += getDistanceBetweenCoords(lastPoint, curPoint);
			lastPoint = curPoint;
		}
		// add dist to starting point
		totalDistance += getDistanceBetweenCoords(lastPoint, path[0]);
		return totalDistance;
	}

	public float getPathDistanceInMeters() {
		if (path.length > 2) { // need 3 points for a loop
			float totalDistance = 0;
			LatLng lastPoint = path[0];
			for (int i = 1; i < path.length; i++) {
				LatLng curPoint = path[i];
				totalDistance += getDistanceBetweenCoords(lastPoint, curPoint);
				lastPoint = curPoint;
			}
			// add dist to starting point
			totalDistance += getDistanceBetweenCoords(lastPoint, path[0]);
			return totalDistance;
		}
		return 0;
	}
	public static float getDistanceBetweenCoords(LatLng coords1, LatLng coords2) { // in meters
		float[] results = new float[1];
		Location.distanceBetween(coords1.latitude, coords1.longitude, coords2.latitude, coords2.longitude, results);
		float distanceInMeters = results[0];
		return distanceInMeters;
	}
}
