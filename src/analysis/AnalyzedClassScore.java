package analysis;

import java.util.ArrayList;
import java.util.HashMap;

import scraper.Assignment;
import scraper.AssignmentsList;
import scraper.ClassScore;
import utils.Utils;

public class AnalyzedClassScore {
	private ArrayList<Category> categories;
	private HashMap<Category, Double> categoryScores;
	private String courseName;
	private double totalScore;
	
	public AnalyzedClassScore(ClassScore classScore, AssignmentsList assignments) {
		this.courseName = classScore.getCourseTitle();
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
	}
	
	public AnalyzedClassScore(ClassScore classScore) {
		this.courseName = classScore.getCourseTitle();
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
		for(Category c : categories) {
			double numerator = 0;
			double denominator = 0;
			for(Assignment a : sortedAssignments.get(c)) {
				if(!a.getScore().contains("/")) {
					continue;
				}
				
				String[] stringScore = a.getScore().split("/");
				numerator += Double.parseDouble(stringScore[0]);
				denominator += Double.parseDouble(stringScore[1]);
			}
					
			scores.put(c, numerator / denominator);
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
