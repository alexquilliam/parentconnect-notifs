package utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Utils {
	public static void sleep(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean urlIsAvailable(String website) {
		try {
			URL url = new URL(website);
			URLConnection connection = url.openConnection();
			connection.connect();
			connection.getInputStream().close();

			return true;
		} catch (MalformedURLException malformedURL) {
			malformedURL.printStackTrace();
		} catch (IOException io) {
			return false;
		}

		return true;
	}
}
