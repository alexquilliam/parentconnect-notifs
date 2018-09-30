package main;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class UserSetupManager {
	public UserSetupManager(String configFilePath) {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter your full name (the one used on ParentCONNECTxp), seperated by spaces: ");
		String name = scanner.nextLine();

		System.out.print("Enter your ParentCONNECTxp username: ");
		String username = scanner.nextLine();

		System.out.print("Enter your ParentCONNECTxp password: ");
		String password = scanner.nextLine();

		System.out.print("Enter the full path to the Chrome Driver: ");
		String chromeDriverPath = scanner.nextLine();

		scanner.close();

		String[] names = name.split(" ");

		String configData = String.join("\n",
				String.join("\n", names),
				username,
				password,
				chromeDriverPath,
				"disable-infobars",
				"start-maximized",
				"headless");

		try {
			Files.write(Paths.get(configFilePath), configData.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
