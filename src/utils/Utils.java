package utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import analysis.Category;
import main.ResourcePaths;
import scraper.Assignment;
import scraper.AssignmentsList;
import scraper.ClassScore;

public class Utils {
	private static AssignmentsList assignments = readAssignments();
	private static ArrayList<ClassScore> classScores = readClassScores();
	private static HashMap<String, ArrayList<Category>> categories = readCategories(Utils.getAssignments());
	
	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/uuuu");
	
	public static AssignmentsList getAssignments() {
		return assignments;
	}
	
	public static ArrayList<ClassScore> getClassScores() {
		return classScores;
	}
	
	public static HashMap<String, ArrayList<Category>> getCategories() {
		categories = Utils.checkForCategoryAssignments(categories, Utils.getAssignments());
		return categories;
	}
	
	public static HashMap<String, ArrayList<Category>> getCategories(AssignmentsList assignments) {
		categories = Utils.checkForCategoryAssignments(categories, assignments);
		return categories;
	}
	
	public static void updateAssignments() {
		assignments = readAssignments();
	}
	
	public static void updateClassScores() {
		classScores = readClassScores();
	}

	public static void updateCategories() {
		categories = readCategories(Utils.getAssignments());
	}
	
	public static void updateAssignmentsFile(AssignmentsList assignments) {
		writeAssignments(assignments);
	}
	
	public static void updateClassScoresFile(ArrayList<ClassScore> classScores) {
		writeClassScores(classScores);
	}

	public static void updateCategoriesFile(HashMap<String, ArrayList<Category>> categories) {
		writeCategories(categories);
	}
	
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
	
	private static AssignmentsList readAssignments() {
		String unparsedAssignments = null;

		try {
			unparsedAssignments = new String(Files.readAllBytes(Paths.get(ResourcePaths.ASSIGNMENTS_PATH)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		unparsedAssignments = unparsedAssignments.replaceAll("\\[|\\]", "");
		String[] parsedAssignments = unparsedAssignments.split("\n");

		AssignmentsList assignments = new AssignmentsList();
		for (String s : parsedAssignments) {
			if(s.contains("$")) {
				Assignment a = new Assignment(new ArrayList<String>(Arrays.asList(s.split(", (?=(?:[^\\$]*\\$[^\\$]*\\$)*[^\\$]*$)"))));
				a.setAssignmentName(a.getAssignmentName().replace("$", ""));
				
				assignments.add(a);
			}else {
				assignments.add(new Assignment(new ArrayList<String>(Arrays.asList(s.split(", ")))));
			}
		}

		return assignments;
	}
	
	private static void writeAssignments(AssignmentsList assignments) {
		String data = "";
		for (Assignment a : assignments) {
			if(a.getAssignmentName().contains(",")) {
				ArrayList<String> assignment = a.toList();
				assignment.set(2, "$" + assignment.get(2) + "$");
				data += assignment.toString() + "\n";
			}else {
				data += a.toString() + "\n";
			}
		}
		
		try {
			Files.write(Paths.get(ResourcePaths.ASSIGNMENTS_PATH), data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static ArrayList<ClassScore> readClassScores() {
		String unparsedClassScores = null;

		try {
			unparsedClassScores = new String(Files.readAllBytes(Paths.get(ResourcePaths.CLASS_SCORES_PATH)));
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
	
	private static void writeClassScores(ArrayList<ClassScore> classScores) {
		String data = "";
		for (ClassScore c : classScores) {
			data += c.toString() + "\n";
		}
		
		try {
			Files.write(Paths.get(ResourcePaths.CLASS_SCORES_PATH), data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static HashMap<String, ArrayList<Category>> readCategories(AssignmentsList assignments) {
		String unnparsedCategories = null;
		
		try {
			unnparsedCategories = new String(Files.readAllBytes(Paths.get(ResourcePaths.CATEGORIES_PATH)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		HashMap<String, ArrayList<Category>> categories = new HashMap<String, ArrayList<Category>>();
		
		String[] lines = unnparsedCategories.split("\n");
		for(String s : lines) {
			String[] sections = s.split("=");
			sections[1] = sections[1].substring(1, sections[1].length() - 1);
			String[] categoryParameters = sections[1].split(", (?![^\\[\\]]*+\\])");
			
			ArrayList<Category> classCatagories = new ArrayList<Category>();
			for(String a : categoryParameters) {
				a = a.replaceAll("[\\[\\]]", "");
				String[] parameters = a.split(", ");
				classCatagories.add(new Category(parameters[0], parameters[1], Integer.parseInt(parameters[2]), Boolean.parseBoolean(parameters[3])));
			}
			
			categories.put(sections[0], classCatagories);
		}
		
		categories = Utils.checkForCategoryAssignments(categories, assignments);
		
		return categories;
	}
	
	private static HashMap<String, ArrayList<Category>> checkForCategoryAssignments(HashMap<String, ArrayList<Category>> categories, AssignmentsList assignments) {
		for(String courseName : categories.keySet()) {
			ArrayList<Assignment> classAssignments = new ArrayList<Assignment>();
			for(Assignment a : assignments) {
				if(a.getCourseName().equals(courseName)) {
					classAssignments.add(a);
				}
			}
			
			HashMap<Category, ArrayList<Assignment>> sortedAssignments = new HashMap<Category, ArrayList<Assignment>>();
			for(Category c : categories.get(courseName)) {
				ArrayList<Assignment> categoryAssignments = new ArrayList<Assignment>();
				for(Assignment a : classAssignments) {
					if(a.getAssignmentType().equals(c.getName())) {
						categoryAssignments.add(a);
					}
				}
				
				if(categoryAssignments.size() == 0) {
					c.setHasAssignments(false);
				}else {
					c.setHasAssignments(true);
				}
				
				sortedAssignments.put(c, categoryAssignments);
			}
		}
		
		return categories;
	}
	
	private static void writeCategories(HashMap<String, ArrayList<Category>> categories) {
		String data = "";
		for(String s : categories.keySet()) {
			data += s + "=" + categories.get(s) + "\n";
		}
		
		try {
			Files.write(Paths.get(ResourcePaths.CATEGORIES_PATH), data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
