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

public class CastSAXParser extends DefaultHandler {

	List<Cast> castList;

	private String tempVal;
	private String tempMovie;
	private String tempActor;

	private Cast tempCast;

	public CastSAXParser() {
		castList = new ArrayList<Cast>();
	}

	public void runCast() throws Exception {
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

			sp.parse("casts124.xml", this);

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	private void printData() {
		Iterator<Cast> it = castList.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}
		System.out.println("No of Movies '" + castList.size() + "'.");
	}

	private void insertData() throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");

			dbcon.setAutoCommit(false);

			String sstarID = "";

			String starIDQuery = "SELECT MAX(id) from stars;";

			PreparedStatement idStatement = dbcon.prepareStatement(starIDQuery);
			ResultSet rs = idStatement.executeQuery();
			if (rs.next()) {
				sstarID = rs.getString(1);
			}

			idStatement.close();

			int intStarID = Integer.parseInt(sstarID.substring(2, sstarID.length()));
			intStarID++;
			
			String query = "INSERT INTO stars (id, name)"
    				+ " VALUES(?, ?)";
			PreparedStatement starStatement = dbcon.prepareStatement(query);
			
			query = "INSERT INTO stars_in_movies"
    				+ " VALUES(?, ?)";
			PreparedStatement simStatement = dbcon.prepareStatement(query);
			
		
			query = "SELECT id, name FROM stars";
    		PreparedStatement existStarstatement = dbcon.prepareStatement(query);
    		rs = existStarstatement.executeQuery();
    		HashMap<String, String> stars = new HashMap<String, String>();
    		
    		while(rs.next()) {
    			String id = rs.getString("id");
    			String name = rs.getString("name");

    			stars.put(name, id);
    		}
    		
    		query = "SELECT id, title FROM movies";
    		PreparedStatement existMoviestatement = dbcon.prepareStatement(query);
    		rs = existMoviestatement.executeQuery();
    		HashMap<String, String> movies = new HashMap<String, String>();
    		
    		while(rs.next()) {
    			String id = rs.getString("id");
    			String title = rs.getString("title");

    			movies.put(title, id);
    		}
    		
    		query = "SELECT starId, movieId FROM stars_in_movies";
    		PreparedStatement existSimstatement = dbcon.prepareStatement(query);
    		rs = existSimstatement.executeQuery();
    		HashMap<String, String> sims = new HashMap<String, String>();
    		
    		while(rs.next()) {
    			String existStarID = rs.getString("starId");
    			String existMovieID = rs.getString("movieId");

    			sims.put(existStarID, existMovieID);
    		}
    		
    		Iterator<Cast> it = castList.iterator();
    		
    		while (it.hasNext()) {
				Cast cast = it.next();
				
				if (cast.getMovie().isEmpty()) {
					System.out.println("Movie title is missing");
					continue;
				}
				if (cast.getActor().isEmpty()) {
					System.out.println("Actor name is missing");
					continue;
				}
				if (stars.containsKey(cast.getActor()) && movies.containsKey(cast.getMovie())) {
					String tempStarID = stars.get(cast.getActor());
					String tempMovieID = stars.get(cast.getMovie());
					
					if (sims.containsKey(tempStarID)) {
						if (sims.get(tempStarID).equals(tempMovieID)) {
							System.out.println(cast.getActor() + " and " + cast.getMovie() + " already exist");
							continue;
						}
					}
				}
				
				String starID = "";
				
				if (stars.containsKey(cast.getActor())) {
					starID = stars.get(cast.getActor());
				}
				else {
					starID = "nm" + String.valueOf(intStarID);
					starStatement.setString(1, starID);
					starStatement.setString(2, cast.getActor());
					starStatement.addBatch();
					
					stars.put(starID, cast.getActor());
					intStarID++;
				}
				
				String movieID = "";
				
				if (movies.containsKey(cast.getMovie())) {
					movieID = movies.get(cast.getMovie());
				}
				else {
					System.out.println("Movie does not exist in database");
					continue;
				}
				
				simStatement.setString(1, starID);
				simStatement.setString(2, movieID);
				simStatement.addBatch();
				
				sims.put(starID, movieID);
    		}
    		
    		
    		starStatement.executeBatch();
    		simStatement.executeBatch();
			dbcon.commit();

			System.out.println("Insertion succeed");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tempVal = "";
		if (qName.equalsIgnoreCase("f")) {
			tempCast = new Cast();
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch, start, length);
		
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("t")) {
			castList.add(tempCast);
			if (tempVal == null) {
				System.out.println("Movie title is invalid");
				tempVal = "null";
			}
			tempCast.setMovie(tempVal);
		} if (qName.equalsIgnoreCase("a")) {
			if (tempVal == null) {
				System.out.println("Actor name is invalid");
				tempVal = "null";
			}
			tempCast.setActor(tempVal);
		}
	}

	public static void main(String[] args) throws Exception {
		CastSAXParser spa = new CastSAXParser();
		spa.runCast();
	}

}