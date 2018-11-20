package scraper;

import java.time.LocalDate;
import java.util.ArrayList;

import utils.Utils;

public class AssignmentsList extends ArrayList<Assignment> {
	private static final long serialVersionUID = 8087266080203723362L;

	public AssignmentsList getAssignmentsByCourse(String courseName) {
		AssignmentsList assignments = new AssignmentsList();
		for(Assignment a : this) {
			if(a.getCourseName().equals(courseName)) {
				assignments.add(a);
			}
		}
		
		return assignments;
	}

	public AssignmentsList getAssignmentsByName(String assignmentName) {
		AssignmentsList assignments = new AssignmentsList();
		for(Assignment a : this) {
			if(a.getAssignmentName().equals(assignmentName)) {
				assignments.add(a);
			}
		}
		
		return assignments;
	}
	
	public AssignmentsList getAssignmentByType(String assignmentType) {
		AssignmentsList assignments = new AssignmentsList();
		for(Assignment a : this) {
			if(a.getAssignmentType().equals(assignmentType)) {
				assignments.add(a);
			}
		}
		
		return assignments;
	}
	
	public AssignmentsList getAssignmentsByScore(String score) {
		AssignmentsList assignments = new AssignmentsList();
		for(Assignment a : this) {
			if(a.getScore().equals(score)) {
				assignments.add(a);
			}
		}
		
		return assignments;
	}
	
	public AssignmentsList getAssignmentsByDueDate(String dueDate) {
		AssignmentsList assignments = new AssignmentsList();
		for(Assignment a : this) {
			if(a.getDue().equals(dueDate)) {
				assignments.add(a);
			}
		}
		
		return assignments;
	}
	
	public AssignmentsList getEarliestPostedAssignments() {
		return getAssignmentsByDueDate(this.stream().map(a -> LocalDate.parse(a.getDue(), Utils.DATE_FORMAT)).min(LocalDate::compareTo).get().format(Utils.DATE_FORMAT));
	}
	
	public AssignmentsList getAssignmentsByTeacher(String teacher) {
		AssignmentsList assignments = new AssignmentsList();
		for(Assignment a : this) {
			if(a.getTeacher().equals(teacher)) {
				assignments.add(a);
			}
		}
		
		return assignments;
	}
}
