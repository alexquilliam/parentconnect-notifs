package main;

import java.awt.SystemTray;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import scraper.Assignment;
import scraper.AssignmentsList;
import scraper.CertificateInstaller;
import scraper.ClassScore;
import scraper.GradeScrapper;
import utils.Utils;

@SuppressWarnings("unused")
public class Main {
	private Logger logger = Logger.getLogger(Main.class);

	/*public Main() throws Exception {
		AnalyzedClassScore score = new AnalyzedClassScore(ASSIGNMENTS_PATH, CATEGORIES_PATH, "H PRE-CALCULUS S1");
		System.out.println(score.getTotalScore());
		
		System.exit(0);
		
		startup();

		Image icon = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/icon.png"));

		ArrayList<Assignment> oldAssignmentList = null;
		ArrayList<Assignment> newAssignmentList = null;

		while(true) {
			while(!Utils.urlIsAvailable("https://parentconnect.aacps.org")) {
				Thread.sleep(1800000);
			}

			oldAssignmentList = Utils.readAssignments(ASSIGNMENTS_PATH);
			
			logger.info("Updating assignments list...");

			updateGrades();
			newAssignmentList = Utils.readAssignments(ASSIGNMENTS_PATH);

			logger.info("Sorting new assignments...");
			
			TreeMap<String, ArrayList<Assignment>> newAssignments = sortAssignmentsByClass(getNewAssignments(oldAssignmentList, newAssignmentList, true));

			logger.info("Displaying new assignments.");
			for(String key : newAssignments.keySet()) {
				ArrayList<String> assignments = new ArrayList<String>();
				for(Assignment a : newAssignments.get(key)) {
					assignments.add(a.getAssignmentName() + " (" + a.getScore() + ")");
				}

				new Notif(icon, "Test", key, String.join("\n", assignments));

				logger.info(key + "\n" + String.join("\n", assignments));
			}

			Thread.sleep(3600000);
		}
	}*/

	private void startup() throws Exception {
		logger.info("Running startup code...");
		
		if(!SystemTray.isSupported()) {
			logger.info("System trays are not supported. Exiting.");

			System.exit(1);
		}

		if(!new File(ResourcePaths.CONFIG_PATH).isFile()) {
			logger.info("Setting up configurations...");
			
			new UserSetupManager();

			CertificateInstaller certificateInstaller = new CertificateInstaller();
			if(!certificateInstaller.getKeyStore().containsAlias("parentconnect.aacps.org")) {
				logger.info("Installing SSL certificate for parentconnect.aacps.org...");
				
				certificateInstaller.install("parentconnect.aacps.org", 443);
			}
			
			logger.info("Updating grades...");

			updateGrades();
		}else {
			logger.info("Reading configurations...");
			
			Configurations.readConfigurations();
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

	private void updateGrades() {
		ArrayList<String> parameters = new ArrayList<String>();

		try {
			Files.lines(Paths.get(ResourcePaths.CONFIG_PATH)).forEach(parameters::add);
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

		AssignmentsList assignments = scrapper.getAssignments();
		ArrayList<ClassScore> classScores = scrapper.getClassScores();

		Utils.updateAssignmentsFile(assignments);
		Utils.updateClassScoresFile(classScores);

		scrapper.terminate();
	}
}
