package gui;

import java.util.HashMap;
import java.util.Objects;

import analysis.AnalyzedClassScore;
import analysis.Category;
import analysis.ChangeCalculator;
import scraper.ClassScore;

public class DisplayableClassScore {
	private String courseName;
	private String courseGrade;
	private String courseScore;
	private String courseChange;
	private String courseTeacher;
	private HashMap<Category, Double> categoryScores;

	public DisplayableClassScore(ClassScore classScore) {
		AnalyzedClassScore analyzedClassScore = new AnalyzedClassScore(classScore);
		
		courseName = classScore.getCourseTitle();
		courseGrade = classScore.getCurrentGrade();
		courseScore = String.format("%.2f", analyzedClassScore.getTotalScore());
		
		//double change =  new ChangeCalculator(classScore, LocalDate.parse("11/01/2018", Utils.DATE_FORMAT), LocalDate.parse("11/09/2018", Utils.DATE_FORMAT)).getChange();
		double change =  new ChangeCalculator(classScore, 7).getChange();
		courseChange = change > 0 ? String.format("+%.2f", change) : String.format("%.2f", change);
		
		courseTeacher = classScore.getTeacher().toUpperCase();
		
		categoryScores = analyzedClassScore.getCategoryScores();
	}

	public String getCourseName() {
		return courseName;
	}

	public String getCourseGrade() {
		return courseGrade;
	}

	public String getCourseScore() {
		return courseScore;
	}

	public String getCourseChange() {
		return courseChange;
	}

	public String getCourseTeacher() {
		return courseTeacher;
	}
	
	public HashMap<Category, Double> getCategoryScores() {
		return categoryScores;
	}

	@Override
	public String toString() {
		return "[courseName = " + courseName + ", courseGrade = " + courseGrade + ", courseScore = " + courseScore + ", courseChange = " + courseChange + ", courseTeacher = " + courseTeacher + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(courseChange, courseGrade, courseName, courseScore, courseTeacher);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof DisplayableClassScore)) {
			return false;
		}
		
		DisplayableClassScore other = (DisplayableClassScore) obj;
		return Objects.equals(courseChange, other.courseChange) && Objects.equals(courseGrade, other.courseGrade)
				&& Objects.equals(courseName, other.courseName) && Objects.equals(courseScore, other.courseScore)
				&& Objects.equals(courseTeacher, other.courseTeacher);
	}
}
