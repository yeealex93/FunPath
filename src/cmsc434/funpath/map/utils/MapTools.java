package cmsc434.funpath.map.utils;

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
