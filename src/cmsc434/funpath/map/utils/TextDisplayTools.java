package cmsc434.funpath.map.utils;

public class TextDisplayTools {
	public static String getDistanceText(double distanceTravelled, double totalDistance) {
		return "Distance (m): " + distanceTravelled + " / " + totalDistance;
	}

	// Ex: 4:13 for 4 minutes 13 seconds
	public static String getTimeText(long timeSeconds) {
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
			return minuteStr + ":" + secondStr;
		}
		return hourStr + ":" + minuteStr + ":" + secondStr;
	}
}
