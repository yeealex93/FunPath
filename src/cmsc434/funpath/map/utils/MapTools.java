package cmsc434.funpath.map.utils;

import cmsc434.funpath.run.RunPath;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class MapTools {
	
	public static RunPath run;
	public static GoogleMap map;
	
	public static void setMap(RunPath in) {
		run = in;
	}
	
	// Calculate average latitude and longitude of a path.
	private static LatLng averagePathPoints() {
		double totalLat = 0;
		double totalLon = 0;
		
		LatLng[] path = run.getPath();
		for(int i = 0; i < path.length; i++) {
			totalLat += path[i].latitude;
			totalLon += path[i].longitude;
		}
		
		return new LatLng(totalLat/path.length, totalLon/path.length);
	}
	
	// Zoom to a given point on the map.
	private static void zoomToLocation() {
		LatLng point = averagePathPoints();
		double lat = point.latitude;
		double lon = point.longitude;
		
		if (map != null) {
			LatLng coordinates = new LatLng(lat, lon);
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 17f));
		}
	}
}
