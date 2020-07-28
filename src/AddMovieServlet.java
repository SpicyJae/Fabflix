import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysql.jdbc.CallableStatement;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/add-movie")
public class AddMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
        
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");
			
			String title = request.getParameter("title");
			String year = request.getParameter("year");
			String director = request.getParameter("director");
			String star = request.getParameter("star");
			String genre = request.getParameter("genre");
					
			String message = "";
			int count = 0;
			
			if (title.equals("")) {
				message += "title ";
				count++;
			}
			if (year.equals("")) {
				message += "released year ";
				count++;
			}
			if (director.equals("")) {
				message += "director name ";
				count++;
			}
			if (star.equals("")) {
				message += "star name ";
				count++;
			}
			if (genre.equals("")) {
				message += "genre ";
				count++;
			}
			
			// Must include all the required fields
			if (count > 0) {
				JsonArray jsonArray = new JsonArray();
			    JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("message", "Submit all the required fields: " + message);
				jsonArray.add(jsonObject);
				out.write(jsonArray.toString());	
			}
			else {
		        String call = "CALL add_movie(?, ?, ?, ?, ?, ?)";
		        CallableStatement statement = (CallableStatement) dbcon.prepareCall(call);
		        statement.setString(1, title);
		        statement.setString(2, year);
		        statement.setString(3, director);
		        statement.setString(4, star);
		        statement.setString(5, genre);
		        statement.registerOutParameter(6, Types.VARCHAR);
		        	
		        int result = statement.executeUpdate();
		        message = statement.getString(6);
		       		        	
		        if (result > 0) {
					JsonArray jsonArray = new JsonArray();
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("message", message);
					jsonArray.add(jsonObject);
					out.write(jsonArray.toString());
					System.out.println(jsonArray.toString());
				} else {
					JsonArray jsonArray = new JsonArray();
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("message", "Fail to add movie " + title + " to the database");
					jsonArray.add(jsonObject);
					out.write(jsonArray.toString());
				}		    
			    statement.close();
			}

    		response.setStatus(200);
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