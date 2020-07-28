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

@WebServlet(name = "Autocomplete", urlPatterns = "/api/autocomplete")
public class Autocomplete extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");
			
			JsonArray jsonArray = new JsonArray();
			
			String title = request.getParameter("title");
			String titleQuery = "";
			
			String[] titles = title.split(" ");
			
			if (title == null || title.trim().isEmpty()) {
				out.write(jsonArray.toString());
				return;
			}
			
			for (int i = 0; i < titles.length; i++)
				titleQuery += "+" + titles[i] + "*";
			
			String fullSearchQuery = "SELECT * FROM movies WHERE MATCH (title)"
					+ " AGAINST ('" + titleQuery + "' IN BOOLEAN MODE) LIMIT 10";
			
			PreparedStatement statement = dbcon.prepareStatement(fullSearchQuery);
			ResultSet rs = statement.executeQuery();
			
			while (rs.next()) {
				String movieID = rs.getString("id");
				String movieTitle = rs.getString("title");
				jsonArray.add(generateJsonObject(movieID, movieTitle));
			}
			
			out.write(jsonArray.toString());
			
		} catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			response.setStatus(500);
		}

	}
	private JsonObject generateJsonObject(String movieID, String movieTitle) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", movieTitle);
		
		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("movieID", movieID);
		
		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}

}