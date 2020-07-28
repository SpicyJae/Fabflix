import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");

		HttpSession session = request.getSession();
		PrintWriter out = response.getWriter();

		@SuppressWarnings("unchecked")
		HashMap<String, String[]> previousItems = (HashMap<String, String[]>) session.getAttribute("previousItems");
		String userId = (String) request.getSession().getAttribute("customer_id");

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");
			
			String message = "";
			int saleId = 0;
			
			String saleIDQuery = "SELECT MAX(id) from sales;";

			PreparedStatement salestatement = dbcon.prepareStatement(saleIDQuery);
			ResultSet rs = salestatement.executeQuery();
			if (rs.next()) {
				saleId = rs.getInt(1);
			}

			salestatement.close();

			for (Object item : previousItems.keySet()) {
				String[] values = previousItems.get(item);
				String movie_id = (String) item;
				String movie_title = values[0];
				String movie_quantity = values[1];

				for (int i = 0; i < Integer.parseInt(movie_quantity); i++) {
					String query = "INSERT INTO sales (customerId, movieId, saleDate)" + " VALUES(?, ?, CURDATE())";
					PreparedStatement statement = dbcon.prepareStatement(query);
					statement.setString(1, userId);
					statement.setString(2, movie_id);

					int result = statement.executeUpdate();

					if (result > 0) {
						System.out.println("Insert Sucess");
					} else {
						System.out.println("Insert Fail");
					}
					saleId += 1;

					statement.close();
				}
				message += "Sale ID: " + saleId + " ";
				message += "movie title: " + movie_title + " added\n";
			}

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("message", message);

			JsonArray jsonArray = new JsonArray();
			jsonArray.add(jsonObject);

			out.write(jsonArray.toString());
			response.setStatus(200);
			previousItems.clear();

			rs.close();
			dbcon.close();
		} catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			System.out.println(jsonObject.toString());
			response.setStatus(500);
		}
		out.close();
	}

}