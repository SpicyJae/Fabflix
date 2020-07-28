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

import com.google.gson.JsonObject;

@WebServlet(name = "CheckoutServlet", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		try {
			String firstname = request.getParameter("firstname");
			String lastname = request.getParameter("lastname");
			String ccId = request.getParameter("ccId");
			String date = request.getParameter("date");
			String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
			
			RecaptchaVerifyUtils.verify(gRecaptchaResponse, RecaptchaConstants.SECRET_KEY);

			// get customer info parameter from js
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "root", "password");

			String query = "SELECT cc.id, c.firstName, c.lastName, cc.expiration FROM creditcards as cc, customers as c "
					+ "WHERE cc.id = c.ccId AND c.id = ?;";

			String userId = (String) request.getSession().getAttribute("customer_id");
			
			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			String errorMessage = new String("please check your ");
			boolean correct = true;
			if (rs.next()) {
				String db_firstname = rs.getString("c.firstName");
				String db_lastname = rs.getString("c.lastName");
				String db_ccId = rs.getString("cc.id");
				String db_date = rs.getString("cc.expiration");
				if (!firstname.equals(db_firstname)) {
					errorMessage += "first name ";
					correct = false;
				}
				if (!lastname.equals(db_lastname)) {
					errorMessage += "last name ";
					correct = false;
				}
				if (!ccId.equals(db_ccId)) {
					errorMessage += "credit card number ";
					correct = false;
				}
				if (!date.equals(db_date)) {
					errorMessage += "credit card expiration date ";
					correct = false;
				}

				if (correct == true) {
					JsonObject responseJsonObject = new JsonObject();
					responseJsonObject.addProperty("status", "success");
					responseJsonObject.addProperty("message", "success");
					response.getWriter().write(responseJsonObject.toString());
				} else {
					JsonObject responseJsonObject = new JsonObject();
					responseJsonObject.addProperty("status", "fail");
					responseJsonObject.addProperty("message", errorMessage);
					response.getWriter().write(responseJsonObject.toString());
				}
			}
			rs.close();
			statement.close();
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
