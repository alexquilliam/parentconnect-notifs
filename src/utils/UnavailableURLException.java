package utils;

import java.net.URL;

public class UnavailableURLException extends Exception {
	private static final long serialVersionUID = 8706007776473075357L;

	public UnavailableURLException(URL url) {
		super("The URL " + url.getPath() + " is not currently available.");
	}

	public UnavailableURLException(String url) {
		super("The URL " + url + " is not currently available.");
	}
}
