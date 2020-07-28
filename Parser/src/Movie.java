import java.util.ArrayList;

public class Movie {
	private String id;
	private String title;
	private int year;
	private String director;
	private ArrayList<String> genres;
	
	Movie() {
		this.genres = new ArrayList<String>();
	}
	
	Movie(String id, String title, int year, String director) {
		this.id = id;
		this.title = title;
		this.year = year;
		this.director = director;
		this.genres = new ArrayList<String>();
	}
	
	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getYear() {
		return year;
	}
		
	public String getDirector() {
		return director;
	}
	
	public ArrayList<String> getGenres() {
		return genres;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
		
	public void setDirector(String director) {
		this.director = director;
	}
	
	public void setGenres(String genre) {
		this.genres.add(genre);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Movie Details - ");
		sb.append("Title: " + getTitle());
		sb.append(", ");
		sb.append("Year: " + getYear());
		sb.append(", ");
		sb.append("Director: " + getDirector());
		sb.append(", ");
		sb.append("Genres: " + getGenres());
		sb.append(".");
		
		return sb.toString();
	}
}
