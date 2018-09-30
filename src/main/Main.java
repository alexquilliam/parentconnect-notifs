package main;

import java.awt.Image;
import java.awt.Toolkit;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import notif.Notif;
import scraper.Assignment;
import scraper.CertificateInstaller;
import scraper.ClassScore;
import scraper.GradeScrapper;

@SuppressWarnings("unused")
public class Main {
	private final String ASSIGNMENTS_PATH = "assignments.txt";
	private final String CLASS_SCORES_PATH = "classscores.txt";
	private final String CONFIG_PATH = "config.txt";

	public Main() throws Exception {
		//new UserSetupManager(CONFIG_PATH);

		//CertificateInstaller certificateInstaller = new CertificateInstaller();
		//certificateInstaller.install("parentconnect.aacps.org", 443);

		Image icon = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/icon.png"));

		updateGrades();

		ArrayList<Assignment> oldAssignmentList = readAssignments();
		ArrayList<Assignment> newAssignmentList = oldAssignmentList;

		while(true) {
			updateGrades();

			oldAssignmentList = newAssignmentList;
			newAssignmentList = readAssignments();

			ArrayList<Assignment> newAssignments = getNewAssignments(oldAssignmentList, newAssignmentList, false);

			for(Assignment a : newAssignments) {
				new Notif(icon, "Recently posted assignment", "New assignment posted for " + a.getCourseName(), a.getAssignmentName() + " - " + a.getScore());

				System.out.println("New assignment posted for " + a.getCourseName() + ": " + a.getAssignmentName() + " - " + a.getScore());

				Thread.sleep(60000);
			}

			Thread.sleep(3600000);
		}
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
			scrapper = new GradeScrapper(parameters.get(0), parameters.get(1), parameters.get(2),
					parameters.get(3), parameters.get(4), parameters.get(5),
					Arrays.copyOf(options.toArray(), options.size(), String[].class));
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
