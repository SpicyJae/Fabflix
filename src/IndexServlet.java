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

@WebServlet(name = "MovieServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try {
        	Class.forName("com.mysql.jdbc.Driver").newInstance();
        	Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb",
                    "root", "password");
         
            String query = "SELECT DISTINCT(g.name) as gname"
        			+ " FROM genres as g";
            
            PreparedStatement statement = dbcon.prepareStatement(query);

            ResultSet rs = statement.executeQuery();
            
            JsonArray jsonArray = new JsonArray();
            JsonArray genreNameList = new JsonArray();
                    	
        	while (rs.next()) {
        		genreNameList.add(rs.getString("gname"));
        	}
        	
        	JsonObject genreObject = new JsonObject();
        	genreObject.add("genre_name_list", genreNameList);
        	jsonArray.add(genreObject);
        	
        	out.write(jsonArray.toString());
            response.setStatus(200);
        	
        	statement.close();
        	rs.close();
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