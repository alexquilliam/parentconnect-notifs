package scraper;

import java.util.ArrayList;
import java.util.Arrays;

public class Assignment {
	private String courseName, period, assignmentName, assignmentType, score, due, remark, teacher;

	public Assignment() {}

	public Assignment(ArrayList<String> parameters) {
		courseName = parameters.get(0);
		period = parameters.get(1);
		assignmentName = parameters.get(2);
		assignmentType = parameters.get(3);
		score = parameters.get(4);
		due = parameters.get(5);
		remark = parameters.get(6);
		teacher = parameters.get(7);
	}

	public Assignment(String courseName, String period, String assignmentName, String assignmentType, String score, String due, String remark, String teacher) {
		this.courseName = courseName;
		this.period = period;
		this.assignmentName = assignmentName;
		this.assignmentType = assignmentType;
		this.score = score;
		this.due = due;
		this.remark = remark;
		this.teacher = teacher;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getAssignmentName() {
		return assignmentName;
	}

	public void setAssignmentName(String assignmentName) {
		this.assignmentName = assignmentName;
	}

	public String getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(String assignmentType) {
		this.assignmentType = assignmentType;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getDue() {
		return due;
	}

	public void setDue(String due) {
		this.due = due;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public ArrayList<String> toList() {
		return (ArrayList<String>) Arrays.asList(courseName, period, assignmentName, assignmentType, score, due, remark, teacher);
	}

	@Override
	public String toString() {
		return "[" + String.join(", ", Arrays.asList(courseName, period, assignmentName, assignmentType, score, due, remark, teacher)) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignmentName == null) ? 0 : assignmentName.hashCode());
		result = prime * result + ((assignmentType == null) ? 0 : assignmentType.hashCode());
		result = prime * result + ((courseName == null) ? 0 : courseName.hashCode());
		result = prime * result + ((due == null) ? 0 : due.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((score == null) ? 0 : score.hashCode());
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

		if (!(obj instanceof Assignment)) {
			return false;
		}

		Assignment other = (Assignment) obj;
		if (assignmentName == null) {
			if (other.assignmentName != null) {
				return false;
			}
		} else if (!assignmentName.equals(other.assignmentName)) {
			return false;
		}

		if (assignmentType == null) {
			if (other.assignmentType != null) {
				return false;
			}
		} else if (!assignmentType.equals(other.assignmentType)) {
			return false;
		}

		if (courseName == null) {
			if (other.courseName != null) {
				return false;
			}
		} else if (!courseName.equals(other.courseName)) {
			return false;
		}

		if (due == null) {
			if (other.due != null) {
				return false;
			}
		} else if (!due.equals(other.due)) {
			return false;
		}

		if (period == null) {
			if (other.period != null) {
				return false;
			}
		} else if (!period.equals(other.period)) {
			return false;
		}

		if (remark == null) {
			if (other.remark != null) {
				return false;
			}
		} else if (!remark.equals(other.remark)) {
			return false;
		}

		if (score == null) {
			if (other.score != null) {
				return false;
			}
		} else if (!score.equals(other.score)) {
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
