import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasypt.util.password.StrongPasswordEncryptor;

import com.google.gson.JsonObject;

@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	PrintWriter out = response.getWriter();
    	
    	try {
    		String username = request.getParameter("username");
            String password = request.getParameter("password");
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            
            RecaptchaVerifyUtils.verify(gRecaptchaResponse, RecaptchaConstants.SECRET_KEY);
            
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        	Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb",
                    "root", "password");

            String query = "SELECT e.email, e.password, e.fullname"
            		+ " FROM employees as e"
            		+ " WHERE e.email = ?;";
            
            PreparedStatement statement = dbcon.prepareStatement(query);
            
            statement.setString(1, username);
            
            ResultSet rs = statement.executeQuery();
            
            boolean success = false;
                   
            if (rs.next()) {
            	String employeeUsername = rs.getString("e.email");
            	String encryptedPassword = rs.getString("e.password");
            	
            	success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
            	if (success)
            	{
            		request.getSession().setAttribute("employee", new User(username));
            		request.getSession().setAttribute("employee_username", employeeUsername);
            		request.getSession().setAttribute("employee_password", encryptedPassword);
            		
            		JsonObject responseJsonObject = new JsonObject();
            		responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");

                    response.getWriter().write(responseJsonObject.toString());
            	}
            	else
            	{
            		JsonObject responseJsonObject = new JsonObject();
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect password");
                    response.getWriter().write(responseJsonObject.toString());
            	}
            }
            else {
            	JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
                response.getWriter().write(responseJsonObject.toString());
            }
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
