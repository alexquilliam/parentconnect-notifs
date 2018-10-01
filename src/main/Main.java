package main;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import notif.Notif;
import scraper.Assignment;
import scraper.CertificateInstaller;
import scraper.ClassScore;
import scraper.GradeScrapper;
import utils.Utils;

@SuppressWarnings("unused")
public class Main {
	private final String ASSIGNMENTS_PATH = "assignments";
	private final String CLASS_SCORES_PATH = "classscores";
	private final String CONFIG_PATH = "config";

	public Main() throws Exception {
		startup();

		Image icon = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/icon.png"));

		ArrayList<Assignment> oldAssignmentList = null;
		ArrayList<Assignment> newAssignmentList = null;

		while(true) {
			while(!Utils.urlIsAvailable("https://parentconnect.aacps.org")) {
				Thread.sleep(1800000);
			}

			oldAssignmentList = readAssignments();

			updateGrades();
			newAssignmentList = readAssignments();

			TreeMap<String, ArrayList<Assignment>> newAssignments = sortAssignmentsByClass(getNewAssignments(oldAssignmentList, newAssignmentList, true));

			for(String key : newAssignments.keySet()) {
				ArrayList<String> assignments = new ArrayList<String>();
				for(Assignment a : newAssignments.get(key)) {
					assignments.add(a.getAssignmentName() + " (" + a.getScore() + ")");
				}

				new Notif(icon, "", "New assignments posted for " + key, String.join("\n", assignments));

				System.out.println("New assignments posted for " + key + "\n" + String.join("\n", assignments) + "\n");
			}

			Thread.sleep(3600000);
		}
	}

	private void startup() throws Exception {
		if(!SystemTray.isSupported()) {
			System.err.println("System trays are not supported!");

			System.exit(1);
		}

		if(!new File(CONFIG_PATH).isFile()) {
			new UserSetupManager(CONFIG_PATH);

			CertificateInstaller certificateInstaller = new CertificateInstaller();
			certificateInstaller.getKeyStore().deleteEntry("parentconnect.aacps.org");
			if(!certificateInstaller.getKeyStore().containsAlias("parentconnect.aacps.org")) {
				certificateInstaller.install("parentconnect.aacps.org", 443);
			}

			updateGrades();
		}else {
			Configurations.readConfigurations(CONFIG_PATH);
		}
	}

	private TreeMap<String, ArrayList<Assignment>> sortAssignmentsByClass(ArrayList<Assignment> assignments) {
		TreeMap<String, ArrayList<Assignment>> sortedAssignments = new TreeMap<String, ArrayList<Assignment>>();

		for(Assignment a : assignments) {
			if(sortedAssignments.containsKey(a.getCourseName())) {
				sortedAssignments.get(a.getCourseName()).add(a);
			}else {
				sortedAssignments.put(a.getCourseName(), new ArrayList<Assignment>(Arrays.asList(a)));
			}
		}

		return sortedAssignments;
	}

	private ArrayList<Assignment> getNewAssignments(ArrayList<Assignment> oldAssignmentList, ArrayList<Assignment> newAssignmentList, boolean hasGrade) {
		ArrayList<Assignment> newAssignments = new ArrayList<Assignment>();

		for(Assignment a : newAssignmentList) {
			if(!oldAssignmentList.contains(a)) {
				if(hasGrade && !a.getScore().equals("-")) {
					newAssignments.add(a);
				}else if (!hasGrade){
					newAssignments.add(a);
				}
			}
		}

		return newAssignments;
	}

	private ArrayList<Assignment> readAssignments() {
		String unparsedAssignments = null;

		try {
			unparsedAssignments = new String(Files.readAllBytes(Paths.get(ASSIGNMENTS_PATH)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		unparsedAssignments = unparsedAssignments.replaceAll("\\[|\\]", "");
		String[] parsedAssignments = unparsedAssignments.split("\n");

		ArrayList<Assignment> assignments = new ArrayList<Assignment>();
		for (String s : parsedAssignments) {
			assignments.add(new Assignment(new ArrayList<String>(Arrays.asList(s.split(", ")))));
		}

		return assignments;
	}

	private ArrayList<ClassScore> readClassScores() {
		String unparsedClassScores = null;

		try {
			unparsedClassScores = new String(Files.readAllBytes(Paths.get(CLASS_SCORES_PATH)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		unparsedClassScores = unparsedClassScores.replaceAll("\\[|\\]", "");
		String[] parsedClassScores = unparsedClassScores.split("\n");

		ArrayList<ClassScore> classScores = new ArrayList<ClassScore>();
		for (String s : parsedClassScores) {
			classScores.add(new ClassScore(new ArrayList<String>(Arrays.asList(s.split(", ")))));
		}

		return classScores;
	}

	private void updateGrades() {
		ArrayList<String> parameters = new ArrayList<String>();

		try {
			Files.lines(Paths.get(CONFIG_PATH)).forEach(parameters::add);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<String> options = parameters.subList(6, parameters.size());

		GradeScrapper scrapper = null;

		try {
			scrapper = new GradeScrapper(
					Configurations.getSingleConfiguration("firstname"),
					Configurations.getSingleConfiguration("middlename"),
					Configurations.getSingleConfiguration("lastname"),
					Configurations.getSingleConfiguration("username"),
					Configurations.getSingleConfiguration("password"),
					Configurations.getSingleConfiguration("chromedriverpath"),
					Configurations.getMultiConfiguration("seleniumoptions").toArray(new String[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList<Assignment> assignments = scrapper.getAssignments();
		ArrayList<ClassScore> classScores = scrapper.getClassScores();

		String persistantAssignments = "";
		for (Assignment a : assignments) {
			persistantAssignments += a.toString() + "\n";
		}

		String persistantClassScores = "";
		for (ClassScore a : classScores) {
			persistantClassScores += a.toString() + "\n";
		}

		try {
			Files.write(Paths.get(ASSIGNMENTS_PATH), persistantAssignments.getBytes());
			Files.write(Paths.get(CLASS_SCORES_PATH), persistantClassScores.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

		scrapper.terminate();
	}

	public static void main(String[] args) throws Exception {
		new Main();
	}
}
