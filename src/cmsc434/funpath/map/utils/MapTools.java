package cmsc434.funpath.map.utils;

import android.graphics.Color;
import cmsc434.funpath.run.RunPath;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapTools {

	// Draw the path on the map.
	public static void drawPath(GoogleMap map, RunPath run) {
		// clear old path
		map.clear();

		LatLng[] path = run.getPath();
		PolylineOptions pathLine = new PolylineOptions().geodesic(true).add(path);
		if (path.length > 0) {
			pathLine.add(path[0]);
		}
		map.addPolyline(pathLine);
	}

	// Draw the path on the map. Display current progress.
	public static void drawPath(GoogleMap map, RunPath run, int index) {
		// clear old path
		map.clear();

		LatLng[] path = run.getPath();
		// draw completed
		int i;
		PolylineOptions pathLine = new PolylineOptions().geodesic(true).color(Color.RED);
		for (i = 0; i < path.length && i <= index; i++) {
			pathLine = pathLine.add(path[i]);
		}
		if (index >= path.length) {
			if (path.length > 0) {
				pathLine.add(path[0]);
			}
		}
		map.addPolyline(pathLine);
		if (index >= path.length) return;
		if (i > 0) {
			i--;
		}
		// draw incomplete
		PolylineOptions pathLine2 = new PolylineOptions().geodesic(true);
		for (int j = i; j < path.length; j++) {
			pathLine2 = pathLine2.add(path[j]);
		}
		if (path.length > 0) {
			pathLine2.add(path[0]);
		}
		map.addPolyline(pathLine2);
	}

	// Zoom to a given point on the map.
	public static void zoomToLocation(GoogleMap map, RunPath run) {
		LatLng point = averagePathPoints(run);
		double lat = point.latitude;
		double lon = point.longitude;
		
		if (map != null) {
			LatLng coordinates = new LatLng(lat, lon);
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 17f));
		}
	}
	
	// Calculate average latitude and longitude of a path.
	private static LatLng averagePathPoints(RunPath run) {
		double totalLat = 0;
		double totalLon = 0;
		
		LatLng[] path = run.getPath();
		for(int i = 0; i < path.length; i++) {
			totalLat += path[i].latitude;
			totalLon += path[i].longitude;
		}
		
		return new LatLng(totalLat/path.length, totalLon/path.length);
	}
}
