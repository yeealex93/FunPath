package cmsc434.funpath.map.utils;

import java.util.Locale;

// Used for time display and distance display
public class TextDisplayTools {
	public static String getDistanceText(double totalDistance) {
		String totalDistString = String.format(Locale.US, "%.1f", totalDistance);
		return "Distance (m): " + totalDistString;
	}
	public static String getDistanceText(double distanceTravelled, double totalDistance) {
		String distTravelledString = String.format(Locale.US, "%.1f", distanceTravelled);
		String totalDistString = String.format(Locale.US, "%.1f", totalDistance);
		return "Distance (m): " + distTravelledString + " / " + totalDistString;
	}

	public static String getElevationText(int hilliness) {
		String ret = "Elevation change: ";
		switch(hilliness) {
		case 0:
			return ret + "Low";
		case 1:
			return ret + "Medium";
		case 2:
			return ret + "High";
		}
		return "Unknown elevation.";
	}
	
	// Ex: 4:13 for 4 minutes 13 seconds
	public static String getTimeText(long timeSeconds) {
		final String prefix = "Time: ";

		final double secondsPerMinute = 1.0/60;
		final double minutesPerHour = 1.0/60;
		int hours = (int) (timeSeconds * secondsPerMinute * minutesPerHour);
		int minutes = (int) (timeSeconds * secondsPerMinute) % 60;
		int seconds = (int) (timeSeconds % 60);
		String hourStr = hours + "";
		String minuteStr = minutes + "";
		String secondStr = seconds + "";
		if (seconds < 10) {
			secondStr = "0" + seconds;
		}
		if (minutes < 10 && hours > 0) {
			minuteStr = "0" + minutes;
		}
		if (hours == 0) {
			return prefix + minuteStr + ":" + secondStr;
		}
		return prefix + hourStr + ":" + minuteStr + ":" + secondStr;
	}
}
