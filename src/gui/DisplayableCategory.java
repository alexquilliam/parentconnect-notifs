package gui;

public class DisplayableCategory {
	private String category;
	private String score;
	private String grade;
	private String weight;

	public DisplayableCategory(String category, String score, String grade, String weight) {
		this.category = category;
		this.score = score;
		this.grade = grade;
		this.weight = weight;
	}

	public String getCategory() {
		return category;
	}

	public String getScore() {
		return score;
	}

	public String getGrade() {
		return grade;
	}

	public String getWeight() {
		return weight;
	}
}
