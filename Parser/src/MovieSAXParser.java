import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class MovieSAXParser extends DefaultHandler {

	List<Movie> movieList;

	private String tempVal;
	private String dirName;

	private Movie tempMovie;

	public MovieSAXParser() {
		movieList = new ArrayList<Movie>();
	}

	public void runMovie() throws Exception {
		long startTime = System.currentTimeMillis();
		parseDocument();
		insertData();
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time spent: " + estimatedTime + "ms");
	}

	private void parseDocument() {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();

			sp.parse("mains243.xml", this);

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	private void printData() {
		Iterator<Movie> it = movieList.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}
		System.out.println("No of Movies '" + movieList.size() + "'.");
	}

	private void insertData() throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");

			dbcon.setAutoCommit(false);

			Iterator<Movie> it = movieList.iterator();

			String smovieID = "";
			String movieIDQuery = "SELECT MAX(id) from movies;";

			PreparedStatement idStatement = dbcon.prepareStatement(movieIDQuery);
			ResultSet rs = idStatement.executeQuery();
			if (rs.next()) {
				smovieID = rs.getString(1);
			}

			idStatement.close();

			int movieID = Integer.parseInt(smovieID.substring(2, smovieID.length()));
			movieID++;

			idStatement.close();

			String query = "INSERT INTO movies VALUES(?, ?, ?, ?);";
			PreparedStatement movieStatement = dbcon.prepareStatement(query);

			query = "INSERT INTO ratings VALUES(?, 0.0, 0);";
			PreparedStatement ratingStatement = dbcon.prepareStatement(query);

			query = "INSERT INTO genres (name)"
					+ " SELECT * FROM (SELECT ?) AS inputGenres WHERE NOT EXISTS (SELECT * FROM genres WHERE name = ?);";
			PreparedStatement genreStatement = dbcon.prepareStatement(query);

			query = "INSERT INTO genres_in_movies VALUES((SELECT id FROM genres WHERE name = ?), ?);";
			PreparedStatement gimStatement = dbcon.prepareStatement(query);
			
			query = "SELECT id, title, director, year FROM movies";
    		PreparedStatement statement = dbcon.prepareStatement(query);
    		rs = statement.executeQuery();
    		HashMap<String, Movie> movies = new HashMap<String, Movie>();
    		
    		while(rs.next()) {
    			String id = rs.getString("id");
    			String title = rs.getString("title");
    			String year = rs.getString("year");
    			String director = rs.getString("director");

    			Movie tempMovie = new Movie();
    			tempMovie.setId(id);
    			tempMovie.setYear(Integer.parseInt(year));
    			tempMovie.setDirector(director);

    			movies.put(title, tempMovie);
    		}

			while (it.hasNext()) {
				Movie mv = it.next();

				if (mv.getTitle().isEmpty()) {
					System.out.println("Movie Title is missing.");
					continue;
				}
				if (mv.getDirector().isEmpty()) {
					System.out.println("Movie Director is missing.");
					continue;
				}
				if (mv.getYear() == 0) {
					System.out.println("Movie Year is missing.");
					continue;
				}
				if (movies.containsKey(mv.getTitle())) {
            		Movie tempMovie = new Movie();
            		tempMovie = movies.get(mv.getTitle());
            		
            		if (tempMovie.getDirector().equals(mv.getDirector()) && tempMovie.getYear() == mv.getYear()) {
            			System.out.println(mv.getTitle() + " already exists (Same title, year, director)");
            			continue;
            		}
            	}

				movieStatement.setString(1, "tt0" + String.valueOf(movieID));
				movieStatement.setString(2, mv.getTitle());
				movieStatement.setInt(3, mv.getYear());
				movieStatement.setString(4, mv.getDirector());
				movieStatement.addBatch();

				ratingStatement.setString(1, "tt0" + String.valueOf(movieID));
				ratingStatement.addBatch();
				
				Movie newMovie = new Movie();
            	newMovie.setId("tt0" + String.valueOf(movieID));
            	newMovie.setYear(mv.getYear());
            	newMovie.setDirector(mv.getDirector());
            	movies.put(mv.getTitle(), newMovie);

				ArrayList<String> genres = mv.getGenres();
				if (!genres.isEmpty()) {
					for (int j = 0; j < genres.size(); j++) {
						String genre = genres.get(j);
						genreStatement.setString(1, genre);
						genreStatement.setString(2, genre);
						genreStatement.addBatch();

						gimStatement.setString(1, genre);
						gimStatement.setString(2, "tt0" + String.valueOf(movieID));
						gimStatement.addBatch();
					}
				}
				movieID++;
			}

			movieStatement.executeBatch();
			ratingStatement.executeBatch();
			genreStatement.executeBatch();
			gimStatement.executeBatch();

			dbcon.commit();
			movieStatement.close();
			ratingStatement.close();
			genreStatement.close();
			gimStatement.close();
			System.out.println("Insertion succeed");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tempVal = "";
		if (qName.equalsIgnoreCase("film")) {
			tempMovie = new Movie();
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch, start, length);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (qName.equalsIgnoreCase("film")) {
			tempMovie.setDirector(dirName);
			movieList.add(tempMovie);
		} else if (qName.equalsIgnoreCase("t")) {
			tempMovie.setTitle(tempVal);
		} else if (qName.equalsIgnoreCase("dirn")) {
			if (tempVal == null) {
				System.out.println("Director name is invalid");
				tempVal = "null";
			}
			dirName = tempVal;
			tempMovie.setDirector(dirName);
		} else if (qName.equalsIgnoreCase("year")) {
			try {
				tempMovie.setYear(Integer.parseInt(tempVal));
			} catch (Exception e) {
				System.out.println(tempVal + " needs to be converted to integer");
				char[] otherTempVal;
				for (int i = 0; i < tempVal.length(); i++) {
					if (!Character.isDigit(tempVal.charAt(i))) {
						otherTempVal = tempVal.toCharArray();
						otherTempVal[i] = '0';
						tempVal = String.valueOf(otherTempVal);
					}
				}
				tempMovie.setYear(Integer.parseInt(tempVal));
			}
		} else if (qName.equalsIgnoreCase("cat")) {
			if (tempVal == null || tempVal.contains(" ")) {
				System.out.println("Genre name is invalid");
				tempVal = "null";
			}
			tempMovie.setGenres(tempVal);
		}
	}

	public static void main(String[] args) throws Exception {
		MovieSAXParser spm = new MovieSAXParser();
		spm.runMovie();
	}

}