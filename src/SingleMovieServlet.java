import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");

		String id = request.getParameter("id");

		PrintWriter out = response.getWriter();

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
        	Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb",
                    "root", "password");
			
			String query = "SELECT movieId, title, year, director, rating"
					+ " FROM ratings as t NATURAL JOIN movies as s NATURAL JOIN"
					+ " (SELECT movieId FROM ratings ORDER BY rating DESC) as movies(id)"
					+ " WHERE s.id = t.movieId AND movieId = ?;";
			
			PreparedStatement statement = dbcon.prepareStatement(query);

			statement.setString(1, id);

			ResultSet rs = statement.executeQuery();

			JsonArray jsonArray = new JsonArray();

			while (rs.next()) {
				String movieId = rs.getString("movieId");
				String movieTitle = rs.getString("title");
				String movieYear = rs.getString("year");
				String movieDirector = rs.getString("director");
				String rating = rs.getString("rating");
				JsonArray genreList = new JsonArray();
				JsonArray starList = new JsonArray();
				JsonArray starIdList = new JsonArray();
				
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
				
				genreRS.close();
				starRS.close();
				genreStatement.close();
				starStatement.close();
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
}