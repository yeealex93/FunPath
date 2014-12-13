package cmsc434.funpath.run;

import com.google.android.gms.maps.model.LatLng;

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
