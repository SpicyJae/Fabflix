import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;

@WebServlet(name = "AddCartServlet", urlPatterns = "/api/add-cart")
public class AddCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
		HashMap<String, String[]> previousItems = (HashMap<String, String[]>) session.getAttribute("previousItems");
        
        if (previousItems == null) {
            previousItems = new HashMap<String, String[]>();
            session.setAttribute("previousItems", previousItems);
        }        

        String movie_id = request.getParameter("movie_id"); 
        String movie_title = request.getParameter("movie_title");
        
        synchronized (previousItems) {
        		
        	if (!previousItems.containsKey(movie_id)) {
        		String[] values = {movie_title, "1"};
        		previousItems.put(movie_id, values);
            } else {
                String[] values = previousItems.get(movie_id);
                int movieQuantity = Integer.parseInt(values[1]);
                movieQuantity += 1;
               	String[] update = {movie_title, String.valueOf(movieQuantity)};
                previousItems.put(movie_id, update);
       		}
        	session.setAttribute("previousItems", previousItems);
       		JsonObject responseJsonObject = new JsonObject();
    		responseJsonObject.addProperty("status", "success");
    		response.getWriter().write(responseJsonObject.toString());                

        }
    }
}