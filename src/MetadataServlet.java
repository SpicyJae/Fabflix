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

@WebServlet(name = "MetadataServlet", urlPatterns = "/api/metadata")
public class MetadataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");

		PrintWriter out = response.getWriter();

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
        	Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb",
                    "root", "password");
        	
        	String query = "SHOW TABLES";
        	PreparedStatement statement = dbcon.prepareStatement(query);
        	ResultSet rs = statement.executeQuery();
        	JsonArray tables = new JsonArray();

        	while (rs.next()) {
        		String table = rs.getString("Tables_in_moviedb");
        		tables.add(table);
        	}
        	
        	JsonArray jsonArray = new JsonArray();
        	
        	for (int i = 0; i < tables.size(); i++) {
        		String tableName = tables.get(i).toString().replaceAll("\"", "");
        		query = "DESCRIBE " + tableName + "; ";
        		
        		statement = dbcon.prepareStatement(query);
    			rs = statement.executeQuery();
    			
    			JsonObject jsonObject = new JsonObject();
    			jsonObject.addProperty("attribute", tableName);
    			
    			JsonArray field = new JsonArray();
				JsonArray type = new JsonArray();
				
    			while (rs.next()) {
    				field.add(rs.getString("Field"));
    				type.add(rs.getString("Type"));
    			}
    			
    			jsonObject.add("field", field);
    			jsonObject.add("type", type);
    			jsonArray.add(jsonObject);
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