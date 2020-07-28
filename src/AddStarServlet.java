import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet(name = "AddStarServlet", urlPatterns = "/api/add-star")
public class AddStarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
        
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");
	    	
			String name = request.getParameter("name");
			
			String sstarId = "";
			
			String query = "";
			String starIDQuery = "SELECT MAX(id) from stars;";
			
			PreparedStatement starstatement = dbcon.prepareStatement(starIDQuery);
			ResultSet rs = starstatement.executeQuery();
		    if(rs.next()){
		    	sstarId = rs.getString(1);
		    }
		    
		    starstatement.close();
	        
        	int starId = Integer.parseInt(sstarId.substring(2, sstarId.length()));
        	starId++;
        	
        	PreparedStatement statement;
        	
        	if (!request.getParameter("birthYear").equals(""))
        	{
        		String birthYear = request.getParameter("birthYear");
        		query = "INSERT INTO stars (id, name, birthYear)"
        				+ " VALUES(?, ?, ?)";
				statement = dbcon.prepareStatement(query);
				statement.setString(1, "nm" + String.valueOf(starId));
				statement.setString(2, name);
				statement.setString(3, birthYear);
        	}
        	else {
        		query = "INSERT INTO stars (id, name)"
        				+ " VALUES(?, ?)";
				statement = dbcon.prepareStatement(query);
				statement.setString(1, "nm" + String.valueOf(starId));
				statement.setString(2, name);
        	}
        	
        	System.out.println(statement);
        	
        	int result = statement.executeUpdate();
        	
        	if (result > 0) {
        		System.out.println("Insert Sucess");
				JsonArray jsonArray = new JsonArray();
			    JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("message", "Star " + name + " added to the database successfully");
				jsonArray.add(jsonObject);
				out.write(jsonArray.toString());
				System.out.println(jsonArray.toString());
			} else {
				JsonArray jsonArray = new JsonArray();
			    JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("message", "Fail to add star " + name + " to the database");
				jsonArray.add(jsonObject);
				out.write(jsonArray.toString());
				System.out.println(jsonArray.toString());
			}

    		response.setStatus(200);
    		
    		rs.close();
    		statement.close();
			dbcon.close();
		} catch(Exception e){
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			response.setStatus(500);
		}
		out.close();
	}

}