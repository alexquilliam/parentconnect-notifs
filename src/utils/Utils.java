package utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Utils {
	public static boolean urlIsAvailable(String url) throws MalformedURLException {
		try {
			URL site = new URL(url);
			URLConnection connection = site.openConnection();
			connection.connect();
			connection.getInputStream().close();

			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
