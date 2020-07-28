function getParameterByName(target) {
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleMovieResult(resultData) {

    console.log("handleMovieResult: populating movie info from resultData");

    let movieTitleElement = jQuery("#movieTitle");

    movieTitleElement.append(resultData[0]["movie_title"]);
    
    let movieInfoElement = jQuery("#movieInfo");
    
    movieInfoElement.append("<li>Movie ID: " + resultData[0]["movie_id"] + "</li>");
    movieInfoElement.append("<li>Release Year: " + resultData[0]["movie_year"] + "</li>")
    movieInfoElement.append("<li>Director: " + resultData[0]["movie_director"] + "</li>");
    
    console.log("handleMovieResult: populating genre info from resultData");
    let movieGenreElement = jQuery("#movieGenre");
    
    for (let i = 0; i < resultData[0]["genre_list"].length; i++) {
    	movieGenreElement.append("<span>" + '<a href="movies.html?by=genre&arg=' + resultData[0]["genre_list"][i] + '&order=t_asc&page=1&ipp=5">'
				+ resultData[0]["genre_list"][i] + '</a>' + "</span>");
    }
    
    console.log("handleMovieResult: populating star info from resultData");
    let starListElement = jQuery("#starList");
    
    for (let i = 0; i < resultData[0]["star_list"].length; i++) {
    	starListElement.append("<li>" + '<a href="single-star.html?id=' + resultData[0]['star_id_list'][i] + '">'
								+ resultData[0]["star_list"][i] + '</a>' + "</li>");
    }
    
    console.log("handleMovieResult: populating rating info from resultData");
    let rateElement = jQuery("#movieRating");
    
    rateElement.append(resultData[0]["rating"] + "/10");
    
    let addElement = jQuery("#add-item");
    addElement.append('<button class="btn" id="addItem" type="submit"'
    		+ 'value="'+ resultData[0]["movie_id"] + '|' + resultData[0]["movie_title"]
    		+ '"> Add to Cart </button>');
    jQuery("#addItem").click((event) => addItem(event));
}

function addItem(addEvent) {
	addEvent.preventDefault();
	var items = document.getElementById("addItem").value;
	var itemList = new Array();
	itemList = items.split("|");
	
	var movie_id = itemList[0];
	var movie_title = itemList[1];
	
	jQuery.get(
			"api/add-cart",
			{movie_id: movie_id, movie_title: movie_title});
}

let movieId = getParameterByName('id');

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-movie?id=" + movieId,
    success: (resultData) => handleMovieResult(resultData)
});