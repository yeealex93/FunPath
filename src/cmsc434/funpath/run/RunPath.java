package cmsc434.funpath.run;

import com.google.android.gms.maps.model.LatLng;

//		// Polylines are useful for marking paths and routes on the map.
//		map.addPolyline(new PolylineOptions().geodesic(true)
//				.add(new LatLng(-33.866, 151.195))  // Sydney
//				.add(new LatLng(-18.142, 178.431))  // Fiji
//				.add(new LatLng(21.291, -157.821))  // Hawaii
//				.add(new LatLng(37.423, -122.091))  // Mountain View
//				);

// FunPath
// stores a path as a list of (lat, lng) coords
public class RunPath {
	private final LatLng[] path;

	public RunPath(LatLng[] path) {
		this.path = path;
	}

	public LatLng[] getPath() {
		return path;
	}
}
