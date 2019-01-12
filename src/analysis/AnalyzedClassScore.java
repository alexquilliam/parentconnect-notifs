package analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.util.Pair;
import scraper.Assignment;
import scraper.AssignmentsList;
import scraper.ClassScore;
import utils.Utils;

public class AnalyzedClassScore {
	private ArrayList<Category> categories;
	private HashMap<Category, Double> categoryScores;
	private String courseName;
	private boolean hasUndiplayedAssignments;
	private double totalScore;
	private ClassScore classScore;
	private ArrayList<Pair<Double, Double>> fractions;
	
	public AnalyzedClassScore(ClassScore classScore, AssignmentsList assignments) {
		this.courseName = classScore.getCourseTitle();
		this.classScore = classScore;
		this.hasUndiplayedAssignments = false;
		this.categories = Utils.getCategories(assignments).get(courseName);
		
		HashMap<Category, ArrayList<Assignment>> sortedAssignments = sortAssignments(courseName, categories, assignments);
		HashMap<Category, Double> scores = calculateCategoryScores(categories, sortedAssignments);
		
		categoryScores = new HashMap<Category, Double>(scores);
		
		double distributedWeight = calculateDistributedWeight(categories);
		
		for(Category c : categories) {
			if(!c.hasAssignments()) {
				continue;
			}
			
			scores.replace(c, ((scores.get(c) * (c.getWeight() + distributedWeight)) / 100) * 100);
		}
		
		totalScore = calculateTotalScore(scores);
		
		if(totalScore != Double.parseDouble(Utils.getClassScores().get(Utils.getClassScores().indexOf(classScore)).getCurrentScore())) {
			hasUndiplayedAssignments = true;
		}
	}
	
	public AnalyzedClassScore(ClassScore classScore) {
		this.courseName = classScore.getCourseTitle();
		this.classScore = classScore;
		this.categories = Utils.getCategories().get(courseName);
		
		ArrayList<Assignment> assignments = Utils.getAssignments();
		
		HashMap<Category, ArrayList<Assignment>> sortedAssignments = sortAssignments(courseName, categories, assignments);
		HashMap<Category, Double> scores = calculateCategoryScores(categories, sortedAssignments);
		
		categoryScores = new HashMap<Category, Double>(scores);
		
		double distributedWeight = calculateDistributedWeight(categories);
		
		for(Category c : categories) {
			if(!c.hasAssignments()) {
				continue;
			}
			
			scores.replace(c, ((scores.get(c) * (c.getWeight() + distributedWeight)) / 100) * 100);
		}
		
		totalScore = calculateTotalScore(scores);
	}
	
	private double calculateTotalScore(HashMap<Category, Double> scores) {
		double sum = 0;
		boolean hasNoAssignments = true;
		for(Category c : categories) {
			if(!c.hasAssignments()) {
				continue;
			}
			
			hasNoAssignments = false;
			sum += scores.get(c);
		}
		
		if(hasNoAssignments) {
			sum = 100.0;
		}
		
		Iterator<Pair<Double, Double>> it = fractions.iterator();
		for(Category c : categories) {
			if(c.hasGFE()) {
				Pair<Double, Double> fraction = it.next();
				double numerator = fraction.getKey();
				double denominator = fraction.getValue();
				
				double p = numerator / denominator;
				double y = ((numerator + 1) / p) - denominator;
				double x = 1;
				double delta = 0.5;
				if(Math.abs(((sum - p) + ((numerator + x) / (denominator + y))) - Double.parseDouble(classScore.getCurrentScore())) <= delta) {
					scores.put(c, (numerator + x) / (denominator + y));
				}else {
					while (x < 100) {
						y = ((numerator + x) / p) - denominator;

						if(Math.abs(((sum - p) + ((numerator + x) / (denominator + y))) - Double.parseDouble(classScore.getCurrentScore())) <= delta) {
							scores.put(c, (numerator + x) / (denominator + y));

							break;
						}

						x++;
					}
				}
			}
		}
		
		return sum;
	}
	
	private double calculateDistributedWeight(ArrayList<Category> categories) {
		double distributedWeight = 0;
		int numNoAssignmentCategories = 0;
		for(Category c : categories) {
			if(!c.hasAssignments()) {
				distributedWeight += c.getWeight();
				numNoAssignmentCategories++;
			}
		}
		
		distributedWeight /= categories.size() -  numNoAssignmentCategories;
			
		return distributedWeight;
	}
	
	private HashMap<Category, Double> calculateCategoryScores(ArrayList<Category> categories, HashMap<Category, ArrayList<Assignment>> sortedAssignments) {
		HashMap<Category, Double> scores = new HashMap<Category, Double>();
		fractions = new ArrayList<Pair<Double, Double>>();
		for (Category c : categories) {
			double numerator = 0.0;
			double denominator = 0.0;
			for (Assignment a : sortedAssignments.get(c)) {
				if(!a.getScore().contains("/")) {
					if(a.getScore().equals("Good Faith Effort") || a.getScore().equals("GFE")) {
						c.setHasGFE(true);
					}
					continue;
				}
				
				String[] stringScore = a.getScore().split("/");
				numerator += Double.parseDouble(stringScore[0]);
				denominator += Double.parseDouble(stringScore[1]);
			}
			
			scores.put(c, numerator / denominator);
			fractions.add(new Pair<Double, Double>(numerator, denominator));
		}

		return scores;
	}
	
	private HashMap<Category, ArrayList<Assignment>> sortAssignments(String courseName, ArrayList<Category> categories, ArrayList<Assignment> assignments) {
		ArrayList<Assignment> classAssignments = new ArrayList<Assignment>();
		for(Assignment a : assignments) {
			if(a.getCourseName().equals(courseName)) {
				classAssignments.add(a);
			}
		}
		
		HashMap<Category, ArrayList<Assignment>> sortedAssignments = new HashMap<Category, ArrayList<Assignment>>();
		for(Category c : categories) {
			ArrayList<Assignment> categoryAssignments = new ArrayList<Assignment>();
			for(Assignment a : classAssignments) {
				if(a.getAssignmentType().equals(c.getName())) {
					categoryAssignments.add(a);
				}
			}
			
			sortedAssignments.put(c, categoryAssignments);
		}
		
		return sortedAssignments;
	}
	
	//it is possible for parentconnect to have an assignment grade affect
	//a class score without showing the actual assignment. this method indicates
	//the presence of these undisplayed grades.
	public boolean hasUndisplayedScores() {
		return hasUndiplayedAssignments;
	}
	
	public HashMap<Category, Double> getCategoryScores() {
		return categoryScores;
	}
	
	public double getTotalScore() {
		return totalScore;
	}
	
	@Override
	public String toString() {
		return courseName + "=" + categoryScores;
	}
}
