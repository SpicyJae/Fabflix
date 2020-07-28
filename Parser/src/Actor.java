public class Actor {
	private String name;
	private int birthYear;

	Actor() {
	}

	Actor(String name, int birthYear) {
		this.name = name;
		this.birthYear = birthYear;
	}

	public String getName() {
		return name;
	}

	public int getBirthYear() {
		return birthYear;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Actor Details - ");
		sb.append("Title: " + getName());
		sb.append(", ");
		sb.append("Year: " + getBirthYear());
		sb.append(".");

		return sb.toString();
	}
}
