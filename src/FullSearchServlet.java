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


@WebServlet(name = "FullSearchServlet", urlPatterns = "/api/full-search")
public class FullSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		
		String title = request.getParameter("title");

		String[] titles = title.split(" ");
	
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");
			
			JsonArray jsonArray = new JsonArray();
			String titleQuery = "";

			if (titles.length == 0 || title.trim().isEmpty()) {
				titleQuery = "";
			} else if (titles.length == 1) {
				titleQuery = "+" + titles[0] + "*";
			} else {
				for (int i = 0; i < titles.length; i++)
					titleQuery += "+" + titles[i] + "*";
			}
			
			String query = "SELECT * FROM movies WHERE MATCH (title)"
					+ " AGAINST ('" + titleQuery + "' IN BOOLEAN MODE)";
			
			/*
			String spage = "1";
			int page = 1;
			
			String sipp = "10";
			int ipp = 10;
			
			
			if (request.getParameter("page") != null) {
				spage = request.getParameter("page");
				page = Integer.parseInt(spage);
			}
			
			if (request.getParameter("ipp") != null) {
				sipp = request.getParameter("ipp");
				ipp = Integer.parseInt(sipp);
			}
			
			int pageVolume = (page - 1) * ipp;
			query += " LIMIT " + String.valueOf(ipp) + " OFFSET " + String.valueOf(pageVolume);
			*/
			
			PreparedStatement statement = dbcon.prepareStatement(query);
			
			System.out.println(statement);
			ResultSet rs = statement.executeQuery();
			
			while (rs.next()) {
				JsonArray starList = new JsonArray();
				JsonArray starIdList = new JsonArray();
				JsonArray genreList = new JsonArray();
				String movieId = rs.getString("id");
				String movieTitle = rs.getString("title");
				String movieYear = rs.getString("year");
				String movieDirector = rs.getString("director");
				String rating = "";
				
				String genreQuery = "SELECT name FROM genres, genres_in_movies " + 
									"WHERE genreId = id AND movieId = " + "'" + movieId +"';";
				PreparedStatement genreStatement = dbcon.prepareStatement(genreQuery);
				ResultSet genreRS = genreStatement.executeQuery(genreQuery);
				while (genreRS.next()) {
					genreList.add(genreRS.getString("name"));
				}
				
				String starQuery = "SELECT id, name FROM stars, stars_in_movies " + 
						"WHERE starId = id AND movieId = " + "'" + movieId +"';";
				
				PreparedStatement starStatement = dbcon.prepareStatement(starQuery);
				ResultSet starRS = starStatement.executeQuery(starQuery);
				while (starRS.next()) {
					starList.add(starRS.getString("name"));
					starIdList.add(starRS.getString("id"));
				}
				
				String ratingQuery = "SELECT rating FROM ratings"
						+ " WHERE movieId = " + "'" + movieId + "';";
				PreparedStatement ratingStatement = dbcon.prepareStatement(ratingQuery);
				ResultSet ratingRS = ratingStatement.executeQuery(ratingQuery);
				while (ratingRS.next()) {
					rating = ratingRS.getString("rating");
				}
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_id", movieId);
				jsonObject.addProperty("movie_title", movieTitle);
				jsonObject.addProperty("movie_year", movieYear);
				jsonObject.addProperty("movie_director", movieDirector);
				jsonObject.addProperty("rating", rating);
				jsonObject.add("genre_list", genreList);
				jsonObject.add("star_list", starList);
				jsonObject.add("star_id_list", starIdList);
				
				jsonArray.add(jsonObject);
				
				genreStatement.close();
				starStatement.close();
				genreRS.close();
				starRS.close();
			}			
			
			rs.close();
			statement.close();
			dbcon.close();
			
			out.write(jsonArray.toString());
			response.setStatus(200);
		} catch(Exception e){
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			response.setStatus(500);
		}
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}