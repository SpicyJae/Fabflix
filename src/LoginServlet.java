import com.google.gson.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	PrintWriter out = response.getWriter();
    	
    	String username = request.getParameter("username");
        String password = request.getParameter("password");
    	
    	// determine request origin by HTTP Header User Agent string
        String userAgent = request.getHeader("User-Agent");
        System.out.println("recieved login request");
        System.out.println("userAgent: " + userAgent);
        
        if (userAgent != null && !userAgent.contains("Android")) {
        	String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            // verify recaptcha first
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse, RecaptchaConstants.SECRET_KEY);
            } catch (Exception e) {
                System.out.println("recaptcha success");
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", e.getMessage());
                response.getWriter().write(responseJsonObject.toString());
                return;
            }
        }
    	
    	try {         
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        	Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb",
                    "root", "password");

            String query = "SELECT c.id, c.email, c.password"
            		+ " FROM customers as c"
            		+ " WHERE c.email = ?;";
            
            PreparedStatement statement = dbcon.prepareStatement(query);
            
            statement.setString(1, username);
            
            ResultSet rs = statement.executeQuery();
            
            boolean success = false;
                   
            if (rs.next()) {
            	String customerId = rs.getString("c.id");
            	String customerUsername = rs.getString("c.email");
            	String encryptedPassword = rs.getString("c.password");
            	
            	success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
            	if (success)
            	{
            		request.getSession().setAttribute("user", new User(username));
            		request.getSession().setAttribute("customer_id", customerId);
            		request.getSession().setAttribute("customer_username", customerUsername);
            		request.getSession().setAttribute("customer_password", encryptedPassword);

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
