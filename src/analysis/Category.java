package analysis;

import java.util.Objects;

public class Category {
	private String name, alias;
	private int weight;
	private boolean hasAssignments;
	
	public Category(String name, String alias, int weight, boolean hasAssignments) {
		this.name = name;
		this.alias = alias;
		this.weight = weight;
	}
	
	public Category(String name, String alias, int weight) {
		this.name = name;
		this.alias = alias;
		this.weight = weight;
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
	}

	public int getWeight() {
		return weight;
	}

	public boolean hasAssignments() {
		return hasAssignments;
	}

	public void setHasAssignments(boolean hasAssignments) {
		this.hasAssignments = hasAssignments;
	}
	
	@Override
	public String toString() {
		return "[" + name + ", " + alias + ", " + weight + ", " + hasAssignments + "]";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(alias, hasAssignments, name, weight);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof Category)) {
			return false;
		}
		
		Category other = (Category) obj;
		return Objects.equals(alias, other.alias) && hasAssignments == other.hasAssignments
				&& Objects.equals(name, other.name) && weight == other.weight;
	}
}
