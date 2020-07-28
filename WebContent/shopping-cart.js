function getParameterByName(target) {
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleResult(resultData) {
    let cartElement = jQuery("#movie-list");
    for (let i = 0; i < resultData.length; i++) {
    	let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td>" + '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">'
					+ resultData[i]["movie_title"] + '</a>' + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_quantity"] + "</td>";
        rowHTML += "<td>" + '<input id="updateQty' + i + '" type="number" name="quantity" min="0">' + "</td>";
        rowHTML += "<td>" + '<button class="btn" id="updateItem' + i + '" type="submit" value="'+ resultData[i]["movie_id"] + '">Update</button>' + "</td>";
        rowHTML += "<td>" + '<button class="btn" id="removeItem' + i + '" type="submit" value="'+ resultData[i]["movie_id"] + '">Remove</button>' + "</td>"
        rowHTML += "</tr>";
        
        cartElement.append(rowHTML);
   	    	
    	jQuery("#updateItem" + i).click((event) => updateItem(event, i));
    	jQuery("#removeItem" + i).click((event) => removeItem(event, i));
    }
}
function updateItem(updateEvent, i) {
	updateEvent.preventDefault();

	var status = "Update";
	var movieid = document.getElementById("updateItem" + i).value;
	var quantity = document.getElementById("updateQty" + i).value;
	
	jQuery.get(
			"api/shopping-cart",
			{status: status, movieid: movieid, quantity: quantity});
	
	window.location.href = "shopping-cart.html?status=Load";
}

function removeItem(removeEvent, i) {
	removeEvent.preventDefault();
	
	var status = "Remove";
	var movieid = document.getElementById("removeItem" + i).value;
	
	jQuery.get(
			"api/shopping-cart",
			{status: status, movieid: movieid});
	
	window.location.href = "shopping-cart.html?status=Load";
}

let status = getParameterByName('status');

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/shopping-cart?status=" + status,
    success: (resultData) => handleResult(resultData)
});