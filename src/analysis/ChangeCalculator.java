package analysis;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import scraper.AssignmentsList;
import scraper.ClassScore;
import utils.Utils;

public class ChangeCalculator {
	private double change;
	
	public ChangeCalculator(ClassScore classScore, int daysBackFromPresent) {
		String firstDueDate = Utils.getAssignments().getEarliestPostedAssignments().get(0).getDue();
		String previousClassScoreDate = LocalDate.now().minusDays(daysBackFromPresent).format(Utils.DATE_FORMAT);
		String currentClassScoreDate = LocalDate.now().format(Utils.DATE_FORMAT);
		
		AnalyzedClassScore previousClassScore = new AnalyzedClassScore(classScore, getAssignmentsInDateRange(firstDueDate, previousClassScoreDate));
		AnalyzedClassScore currentClassScore = new AnalyzedClassScore(classScore, getAssignmentsInDateRange(firstDueDate, currentClassScoreDate));
		
		change = currentClassScore.getTotalScore() - previousClassScore.getTotalScore();
	}
	
	public ChangeCalculator(ClassScore classScore, LocalDate startDate, LocalDate endDate) {
		String firstDueDate = Utils.getAssignments().getEarliestPostedAssignments().get(0).getDue();
		String previousClassScoreDate = startDate.format(Utils.DATE_FORMAT);
		String currentClassScoreDate = endDate.format(Utils.DATE_FORMAT);
		
		AnalyzedClassScore previousClassScore = new AnalyzedClassScore(classScore, getAssignmentsInDateRange(firstDueDate, previousClassScoreDate));
		AnalyzedClassScore currentClassScore = new AnalyzedClassScore(classScore, getAssignmentsInDateRange(firstDueDate, currentClassScoreDate));
		
		change = currentClassScore.getTotalScore() - previousClassScore.getTotalScore();
	}
	
	public double getChange() {
		return change;
	}
	
	private AssignmentsList getAssignmentsInDateRange(String startDate, String endDate) {
		AssignmentsList assignments = new AssignmentsList();
		for(String date : getDatesInRange(startDate, endDate)) {
			assignments.addAll(Utils.getAssignments().getAssignmentsByDueDate(date));
		}
		
		return assignments;
	}
	
	private ArrayList<String> getDatesInRange(String startDate, String endDate) {
		LocalDate start = LocalDate.parse(startDate, Utils.DATE_FORMAT);
		LocalDate end = LocalDate.parse(endDate, Utils.DATE_FORMAT);
		
		long numberOfDays = ChronoUnit.DAYS.between(start, end);
		ArrayList<LocalDate> datesInRange = new ArrayList<LocalDate>(Stream.iterate(start, date -> date.plusDays(1)).limit(numberOfDays == 0 ? 1 : numberOfDays).collect(Collectors.toList()));
		
		return new ArrayList<String>(datesInRange.stream().map(date -> date.format(Utils.DATE_FORMAT)).collect(Collectors.toList()));
	}
}
