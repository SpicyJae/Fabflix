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

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movies")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");

		PrintWriter out = response.getWriter();

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");
			
			PreparedStatement statement;
			
			String query = "SELECT m.id, title, year, director, rating, " + " GROUP_CONCAT(DISTINCT g.name) as glist"
					+ " FROM movies as m, ratings as r," + " genres as g, genres_in_movies as gim"
					+ " WHERE m.id = r.movieId AND"
					+ " m.id = gim.movieId AND gim.genreId = g.id"
					+ " GROUP BY m.id";
			String subQuery = "";
			
			if (request.getParameter("order") != null) {
				String order = request.getParameter("order");
				subQuery = " ORDER BY ";
				
				if (order.charAt(0) == 't') {
					subQuery += "title";
				} else {
					subQuery += "rating";
				}

				if (order.charAt(2) == 'a') {
					subQuery += " ASC";
				} else {
					subQuery += " DESC";
				}
			}
			
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
			subQuery += " LIMIT " + String.valueOf(ipp) + " OFFSET " + String.valueOf(pageVolume);
			
			if (request.getParameter("by") != null) {
				String by = request.getParameter("by");
				String arg = request.getParameter("arg");
				
				if (by.equals("genre")) {
					query += " HAVING FIND_IN_SET(?, glist)";
					query += subQuery;
					statement = dbcon.prepareStatement(query);
					statement.setString(1, arg);
				} else {
					query += " HAVING title LIKE ?";
					query += subQuery;
					statement = dbcon.prepareStatement(query);
					statement.setString(1, arg + "%");
				}
			}
			else {
				query += subQuery;
				statement = dbcon.prepareStatement(query);
			}
			
			ResultSet rs = statement.executeQuery();

			JsonArray jsonArray = new JsonArray();

			while (rs.next()) {
				JsonArray starList = new JsonArray();
				JsonArray starIdList = new JsonArray();
				JsonArray genreList = new JsonArray();
				String movieId = rs.getString("m.id");
				String movieTitle = rs.getString("title");
				String movieYear = rs.getString("year");
				String movieDirector = rs.getString("director");
				String rating = rs.getString("rating");

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
			out.write(jsonArray.toString());
			response.setStatus(200);

			rs.close();
			statement.close();
			dbcon.close();
		} catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			response.setStatus(500);
		}
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
