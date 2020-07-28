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

@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
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
			
			String check = "SELECT *"
					+ " FROM stars AS s, stars_in_movies AS sim, movies AS m"
					+ " WHERE m.id = sim.movieId AND sim.starId = s.id AND s.id = ?";
			PreparedStatement checkStatement = dbcon.prepareStatement(check);
			checkStatement.setString(1, id);

			ResultSet checkRS = checkStatement.executeQuery();
			
			if (checkRS.next()) {
				String query = "SELECT *"
						+ " FROM stars AS s, stars_in_movies AS sim, movies AS m"
						+ " WHERE m.id = sim.movieId AND sim.starId = s.id AND s.id = ?";

				PreparedStatement statement = dbcon.prepareStatement(query);

				statement.setString(1, id);

				ResultSet rs = statement.executeQuery();

				JsonArray jsonArray = new JsonArray();

				while (rs.next()) {

					String starId = rs.getString("starId");
					String starName = rs.getString("name");
					String starDob = rs.getString("birthYear");

					String movieId = rs.getString("movieId");
					String movieTitle = rs.getString("title");
					String movieYear = rs.getString("year");
					String movieDirector = rs.getString("director");

					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("star_id", starId);
					jsonObject.addProperty("star_name", starName);
					jsonObject.addProperty("star_dob", starDob);
					jsonObject.addProperty("movie_id", movieId);
					jsonObject.addProperty("movie_title", movieTitle);
					jsonObject.addProperty("movie_year", movieYear);
					jsonObject.addProperty("movie_director", movieDirector);

					jsonArray.add(jsonObject);
				}
				
	            out.write(jsonArray.toString());
	            
	            rs.close();
				statement.close();
			}
			else {
				String query = "SELECT *"
						+ " FROM stars AS s"
						+ " WHERE s.id = ?";

				PreparedStatement statement = dbcon.prepareStatement(query);

				statement.setString(1, id);

				ResultSet rs = statement.executeQuery();

				JsonArray jsonArray = new JsonArray();

				while (rs.next()) {

					String starId = rs.getString("id");
					String starName = rs.getString("name");
					String starDob = rs.getString("birthYear");

					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("star_id", starId);
					jsonObject.addProperty("star_name", starName);
					jsonObject.addProperty("star_dob", starDob);

					jsonArray.add(jsonObject);
				}
				
	            out.write(jsonArray.toString());
	            
	            rs.close();
				statement.close();
			}
			
			checkRS.close();
            checkStatement.close();
            response.setStatus(200);
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