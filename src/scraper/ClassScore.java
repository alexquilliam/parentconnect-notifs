package scraper;

import java.util.ArrayList;
import java.util.Arrays;

public class ClassScore {
	private String courseNumber, courseTitle, currentGrade, currentScore, teacher;

	public ClassScore() {
	}

	public ClassScore(ArrayList<String> parameters) {
		courseNumber = parameters.get(0);
		courseTitle = parameters.get(1);
		currentGrade = parameters.get(2);
		currentScore = parameters.get(3);
		teacher = parameters.get(4);
	}

	public ClassScore(String courseNumber, String courseTitle, String currentGrade, String currentScore,
			String teacher) {
		this.courseNumber = courseNumber;
		this.courseTitle = courseTitle;
		this.currentGrade = currentGrade;
		this.currentScore = currentScore;
		this.teacher = teacher;
	}

	public String getCourseNumber() {
		return courseNumber;
	}

	public void setCourseNumber(String courseNumber) {
		this.courseNumber = courseNumber;
	}

	public String getCourseTitle() {
		return courseTitle;
	}

	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}

	public String getCurrentGrade() {
		return currentGrade;
	}

	public void setCurrentGrade(String currentGrade) {
		this.currentGrade = currentGrade;
	}

	public String getCurrentScore() {
		return currentScore;
	}

	public void setCurrentScore(String currentScore) {
		this.currentScore = currentScore;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public ArrayList<String> toList() {
		return (ArrayList<String>) Arrays.asList(courseNumber, courseTitle, currentGrade, currentScore, teacher);
	}

	@Override
	public String toString() {
		return "[" + String.join(", ", Arrays.asList(courseNumber, courseTitle, currentGrade, currentScore, teacher))
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((courseNumber == null) ? 0 : courseNumber.hashCode());
		result = prime * result + ((courseTitle == null) ? 0 : courseTitle.hashCode());
		result = prime * result + ((currentGrade == null) ? 0 : currentGrade.hashCode());
		result = prime * result + ((currentScore == null) ? 0 : currentScore.hashCode());
		result = prime * result + ((teacher == null) ? 0 : teacher.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof ClassScore)) {
			return false;
		}

		ClassScore other = (ClassScore) obj;
		if (courseNumber == null) {
			if (other.courseNumber != null) {
				return false;
			}
		} else if (!courseNumber.equals(other.courseNumber)) {
			return false;
		}

		if (courseTitle == null) {
			if (other.courseTitle != null) {
				return false;
			}
		} else if (!courseTitle.equals(other.courseTitle)) {
			return false;
		}

		if (currentGrade == null) {
			if (other.currentGrade != null) {
				return false;
			}
		} else if (!currentGrade.equals(other.currentGrade)) {
			return false;
		}

		if (currentScore == null) {
			if (other.currentScore != null) {
				return false;
			}
		} else if (!currentScore.equals(other.currentScore)) {
			return false;
		}

		if (teacher == null) {
			if (other.teacher != null) {
				return false;
			}
		} else if (!teacher.equals(other.teacher)) {
			return false;
		}

		return true;
	}
}
