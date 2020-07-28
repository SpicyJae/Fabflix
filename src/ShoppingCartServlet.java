import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;

@WebServlet(name = "shoppingCart", urlPatterns = "/api/shopping-cart")
public class ShoppingCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String status = request.getParameter("status");
    	
        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
		HashMap<String, String[]> previousItems = (HashMap<String, String[]>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new HashMap<String, String[]>();
            session.setAttribute("previousItems", previousItems);
        }

        if(status.equals("Load")){
        	loadPreviousItems(request, response, previousItems, session);
        }
        
        if(status.equals("Update")) {
        	updateItems(request, response, previousItems, session);
        }
        
        if(status.equals("Remove")) {
        	removeItems(request, response, previousItems, session);
        }
        
    }
    
    public void loadPreviousItems(HttpServletRequest request, HttpServletResponse response, HashMap<String, String[]> previousItems, HttpSession session) throws IOException {

        synchronized(previousItems) {
        	
        	JsonArray jsonArray = new JsonArray();
        	
        	for (Object item : previousItems.keySet()) {
        		String[] values = previousItems.get(item);
        		String movie_id = (String) item;
        		String movie_title = values[0];
        		String movie_quantity = values[1];
        		
        		JsonObject jsonObject = new JsonObject();
        		jsonObject.addProperty("movie_id", movie_id);
        		jsonObject.addProperty("movie_title", movie_title);
        		jsonObject.addProperty("movie_quantity", movie_quantity);
        		
        		jsonArray.add(jsonObject);
        	}
        	session.setAttribute("previousItems", previousItems);
        	response.getWriter().write(jsonArray.toString());            
            response.setStatus(200);
        }

    }
    public void updateItems(HttpServletRequest request, HttpServletResponse response, HashMap<String, String[]> previousItems, HttpSession session)throws IOException, NullPointerException { 

        String movie_id = request.getParameter("movieid"); 
        String movie_quantity = request.getParameter("quantity");
   		String[] values = previousItems.get(movie_id);
		String movie_title = values[0];
                  	
        synchronized (previousItems) {
        	String[] update = {movie_title, movie_quantity};
        	previousItems.put(movie_id, update);
        }
    	loadPreviousItems(request, response, previousItems, session);
    }
    
    public void removeItems(HttpServletRequest request, HttpServletResponse response, HashMap<String, String[]> previousItems, HttpSession session)throws IOException, NullPointerException {

        String movie_id = request.getParameter("movieid"); 
        
        synchronized (previousItems) {       		
        	if (previousItems.containsKey(movie_id)) {
        		previousItems.remove(movie_id);
        	}
        }
        loadPreviousItems(request, response, previousItems, session);
    }
}