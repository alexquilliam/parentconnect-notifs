package notif;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

public class Notif {
	private TrayIcon tray = null;

	public Notif(Image icon, String toolTip, String title, String message) {
		tray = new TrayIcon(icon, toolTip);
		tray.setImageAutoSize(true);
		tray.setToolTip(toolTip);

		SystemTray sysTray = SystemTray.getSystemTray();

		try {
			sysTray.add(tray);
		} catch (Exception e) {
			e.printStackTrace();
		}

		tray.displayMessage(title, message, MessageType.INFO);
	}

	public void display() {

	}
}
