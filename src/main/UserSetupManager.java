package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class UserSetupManager {
	public UserSetupManager() {
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

		Configurations.addSingleConfiguration("firstname", names[0]);
		Configurations.addSingleConfiguration("middlename", names[1]);
		Configurations.addSingleConfiguration("lastname", names[2]);
		Configurations.addSingleConfiguration("username", username);
		Configurations.addSingleConfiguration("password", password);
		Configurations.addSingleConfiguration("chromedriverpath", chromeDriverPath);

		Configurations.addMultiConfiguration("seleniumoptions", new ArrayList<String>(Arrays.asList("disable-infobars", "start-maximized", "headless")));

		Configurations.writeConfigurations();
	}
}
