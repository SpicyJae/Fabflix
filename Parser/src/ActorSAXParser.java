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

public class ActorSAXParser extends DefaultHandler {

	List<Actor> actorList;

	private String tempVal;
	private String actorName;

	private Actor tempActor;

	public ActorSAXParser() {
		actorList = new ArrayList<Actor>();
	}

	public void runActor() throws Exception {
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

			sp.parse("actors63.xml", this);

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	private void printData() {
		Iterator<Actor> it = actorList.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}
		System.out.println("No of Movies '" + actorList.size() + "'.");
	}

	private void insertData() throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");

			dbcon.setAutoCommit(false);

			Iterator<Actor> it = actorList.iterator();

			String sstarID = "";
			
			String starIDQuery = "SELECT MAX(id) from stars;";
			
			PreparedStatement idStatement = dbcon.prepareStatement(starIDQuery);
			ResultSet rs = idStatement.executeQuery();
		    if(rs.next()){
		    	sstarID = rs.getString(1);
		    }
		    
		    idStatement.close();
	        
        	int starID = Integer.parseInt(sstarID.substring(2, sstarID.length()));
        	starID++;

			String query = "INSERT INTO stars (id, name, birthYear)"
    				+ " VALUES(?, ?, ?)";
			PreparedStatement starStatement1 = dbcon.prepareStatement(query);

			query = "INSERT INTO stars (id, name)"
    				+ " VALUES(?, ?)";
			PreparedStatement starStatement2 = dbcon.prepareStatement(query);
		
			query = "SELECT name, birthYear FROM stars";
    		PreparedStatement statement = dbcon.prepareStatement(query);
    		rs = statement.executeQuery();
    		HashMap<String, Actor> actors = new HashMap<String, Actor>();
    		
    		while(rs.next()) {
    			String name = rs.getString("name");
    			int year = rs.getInt("birthYear");

    			Actor tempActor = new Actor();
    			tempActor.setName(name);
    			tempActor.setBirthYear(year);

    			actors.put(name, tempActor);
    		}

			while (it.hasNext()) {
				Actor act = it.next();

				if (act.getName().isEmpty()) {
					System.out.println("Actor name is missing.");
					continue;
				}

				if (actors.containsKey(act.getName())) {
            		Actor tempActor = new Actor();
            		tempActor = actors.get(act.getName());
            		
            		if (tempActor.getBirthYear() == act.getBirthYear()) {
            			System.out.println(act.getName() + " already exists (Same name, birth year)");
            			continue;
            		}
            	}
				
				if (act.getBirthYear() > 0) {
					starStatement1.setString(1, "nm" + String.valueOf(starID));
					starStatement1.setString(2, act.getName());
					starStatement1.setInt(3, act.getBirthYear());
					starStatement1.addBatch();
				}
				else {
					starStatement2.setString(1, "nm" + String.valueOf(starID));
					starStatement2.setString(2, act.getName());
					starStatement2.addBatch();
				}
				
				Actor newActor = new Actor();
            	newActor.setName(act.getName());
            	newActor.setBirthYear(act.getBirthYear());
            	actors.put(act.getName(), newActor);

				starID++;
			}

			starStatement1.executeBatch();
			starStatement2.executeBatch();

			dbcon.commit();
			starStatement1.close();
			starStatement2.close();

			System.out.println("Insertion succeed");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tempVal = "";
		if (qName.equalsIgnoreCase("actor")) {
			tempActor = new Actor();
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch, start, length);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (qName.equalsIgnoreCase("actor")) {
			actorList.add(tempActor);
		} else if (qName.equalsIgnoreCase("stagename")) {
			if (tempVal == null) {
				System.out.println("Actor name is invalid");
				tempVal = "null";
			}
			actorName = tempVal;
			tempActor.setName(actorName);
		} else if (qName.equalsIgnoreCase("dob")) {
			if (tempVal == null || tempVal.equals(" ")|| tempVal.equals("  ") || tempVal.equals("") || tempVal.equals("n.a.")) {
				System.out.println(tempVal + " is invalid");
				tempVal = "0";
			}
				
			
			char[] otherTempVal;
			for (int i = 0; i < tempVal.length(); i++) {
				if (!Character.isDigit(tempVal.charAt(i))) {
					System.out.println(tempVal + " needs to be converted to integer");
					otherTempVal = tempVal.toCharArray();
					otherTempVal[i] = '0';
					tempVal = String.valueOf(otherTempVal);
				}
			}
			tempActor.setBirthYear(Integer.parseInt(tempVal));       	
		}
	}

	public static void main(String[] args) throws Exception {
		ActorSAXParser spa = new ActorSAXParser();
		spa.runActor();
	}

}