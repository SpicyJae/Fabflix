function getParameterByName(target) {
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleListResult(resultData) {
	let genreElement = jQuery("#genre-list");
	for (let i = 0; i < resultData[0]["genre_name_list"].length; i++) {
		genreElement.append("<span>" + '<a href="movies.html?by=genre&arg=' + resultData[0]["genre_name_list"][i]
							+ '&order=t_asc&page=1&ipp=5">'
							+ resultData[0]["genre_name_list"][i] + '</a>' + "</span>");
	}
	
	let titleElement = jQuery("#title-list");
	for (let i = 0; i < 10; i++) {
		titleElement.append("<span>" + '<a href="movies.html?by=title&arg=' + i
							+ '&order=t_asc&page=1&ipp=5">'
							+ i + '</a>' + "</span>");
	}
	titleElement.append("<br>");
	for (let i = 65; i <= 90; i++) {
		titleElement.append("<span>" + '<a href="movies.html?by=title&arg=' + String.fromCharCode(i)
							+ '&order=t_asc&page=1&ipp=5">'
							+ String.fromCharCode(i) + '</a>' + "</span>");
	}
}

let by = getParameterByName("by");
let arg = getParameterByName("arg");
let order = getParameterByName("order");
let page = getParameterByName("page");
let ipp = getParameterByName("ipp");

function handleMovieResult(resultData) {
	let titleAscElement = jQuery("#title-asc");
	titleAscElement.append('<a href="movies.html?by=' + by + '&arg=' + arg
							+ '&order=t_asc&page=' + page + '&ipp=' + ipp
							+ '">Title <i class="fa fa-angle-down"></i></a>');
	
	let titleDescElement = jQuery("#title-desc");
	titleDescElement.append('<a href="movies.html?by=' + by + '&arg=' + arg
							+ '&order=t_desc&page=' + page + '&ipp=' + ipp
							+ '">Title <i class="fa fa-angle-up"></i></a>');
	
	let rateAscElement = jQuery("#rate-asc");
	rateAscElement.append('<a href="movies.html?by=' + by + '&arg=' + arg
							+ '&order=r_asc&page=' + page + '&ipp=' + ipp
							+ '">Rating <i class="fa fa-angle-down"></i></a></a>');
	
	let rateDescElement = jQuery("#rate-desc");
	rateDescElement.append('<a href="movies.html?by=' + by + '&arg=' + arg
							+ '&order=r_desc&page=' + page + '&ipp=' + ipp
							+ '">Rating <i class="fa fa-angle-up"></i></a>');
	
	let movieElement = jQuery("#movie-list");
    for (let i = 0; i < resultData.length; i++) {
    	let rowHTML = "";
        rowHTML += "<h2>" + '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">'
        			+ resultData[i]["movie_title"] + '</a>' + "</h2>";
        rowHTML += "<div class=row>";
        rowHTML += "<div class=col-sm-4>";
        rowHTML += "<ul>";
        rowHTML += "<li>Release Year: " + resultData[i]["movie_year"] + "</li>";
        rowHTML += "<li>Director: " + resultData[i]["movie_director"] + "</li>";
        rowHTML += "<li>Rating: " + resultData[i]["rating"] + "</li>";
        rowHTML += "</ul>";
        
        if (resultData[i]["genre_list"].length > 0) {
        	for (let j = 0; j < resultData[i]["genre_list"].length; j++) {
            	rowHTML += "<span>" + '<a href="movies.html?by=genre&arg=' + resultData[i]["genre_list"][j] + '&order=t_asc&page=1&ipp=5">'
    			+ resultData[i]["genre_list"][j] + '</a>' + "</span>";
            }
        }
        rowHTML += "</div>";
        
        rowHTML += "<div class=col-sm-4>";
        rowHTML += "<ul>Stars: ";
        
        if (resultData[i]["star_list"].length > 0) {
        	for (let j = 0; j < resultData[i]["star_list"].length; j++) {
            	rowHTML += "<li>" + '<a href="single-star.html?id=' + resultData[i]['star_id_list'][j] + '">'
            			+ resultData[i]["star_list"][j] +  '</a>' + "</li>";
            }
        }

        rowHTML += "</ul>";
        rowHTML += "</div>";
        rowHTML += "</div>";
        
        rowHTML += '<button class="btn" id="addItem' + i + '" type="submit"'
    		+ 'value="'+ resultData[i]["movie_id"] + '|' + resultData[i]["movie_title"]
    		+ '"> Add to Cart </button>';
        
        rowHTML += "<div class=gap></div>";
        
        movieElement.append(rowHTML);
        
        jQuery("#addItem" + i).click((event) => addItem(event, i));
    }
    
    let prevElement = jQuery("#prev-btn");
    var prevPageNum = parseInt(page, 10);
    prevPageNum--;
    var prevPage = prevPageNum.toString();
    
    if (prevPageNum > 0) {
    	prevElement.append('<span><a href="movies.html?by=' + by + '&arg=' + arg
    			+ '&order=' + order + '&page=' + prevPage + '&ipp=' + ipp
    			+ '"><i class="fa fa-angle-left"></i></a></span>');
    }
    else {
    	prevElement.append('<span style="background: #808080;"><i class="fa fa-angle-left"></i></span>')
    }
    
    let nextElement = jQuery("#next-btn");
    var nextPageNum = parseInt(page, 10);
    nextPageNum++;
    var nextPage = nextPageNum.toString();
    
    nextElement.append('<span><a href="movies.html?by=' + by + '&arg=' + arg
    		+ '&order=' + order + '&page=' + nextPage + '&ipp=' + ipp
    		+ '"><i class="fa fa-angle-right"></i></a></span>');
    
    let resultPageElement = jQuery("#result-page");
    resultPageElement.append('<span><a href="movies.html?by=' + by + '&arg=' + arg
			+ '&order=' + order + '&page=' + page + '&ipp=5'
			+ '">5</a></span>');
    resultPageElement.append('<span><a href="movies.html?by=' + by + '&arg=' + arg
			+ '&order=' + order + '&page=' + page + '&ipp=10'
			+ '">10</a></span>');
    resultPageElement.append('<span><a href="movies.html?by=' + by + '&arg=' + arg
			+ '&order=' + order + '&page=' + page + '&ipp=15'
			+ '">15</a></span>');
    resultPageElement.append('<span><a href="movies.html?by=' + by + '&arg=' + arg
			+ '&order=' + order + '&page=' + page + '&ipp=20'
			+ '">20</a></span>');
    resultPageElement.append('<span><a href="movies.html?by=' + by + '&arg=' + arg
			+ '&order=' + order + '&page=' + page + '&ipp=25'
			+ '">25</a></span>');
}

function addItem(addEvent, i) {
	addEvent.preventDefault();
	var items = document.getElementById("addItem" + i).value;
	var itemList = new Array();
	itemList = items.split("|");
	
	var movie_id = itemList[0];
	var movie_title = itemList[1];
	
	jQuery.get(
			"api/add-cart",
			{movie_id: movie_id, movie_title: movie_title});
}

//Underline effect
const target = document.querySelector(".target");
const links = document.querySelectorAll(".list-select a");
const colors = ["#92CDCF"];

for (let i = 0; i < links.length; i++) {
	links[i].addEventListener("click", (e) => e.preventDefault());
	links[i].addEventListener("mouseenter", mouseenterFunc);
}

function mouseenterFunc() {
	for (let i = 0; i < links.length; i++) {
		if (links[i].parentNode.classList.contains("active")) {
			links[i].parentNode.classList.remove("active");
		}
		links[i].style.opacity = "0.25";
	}
	
	this.parentNode.classList.add("active");
	this.style.opacity = "1";
	   
	const width = this.getBoundingClientRect().width;
	const height = this.getBoundingClientRect().height;
	const left = this.getBoundingClientRect().left;
	const top = this.getBoundingClientRect().top;
	const color = colors[Math.floor(Math.random() * colors.length)];
	 
	target.style.width = `${width}px`;
	target.style.height = `${height}px`;
	target.style.left = `${left}px`;
	target.style.top = `${top}px`;
	target.style.borderColor = color;
	target.style.transform = "none";
}

//Modal
var genreModal = document.getElementById('genre-modal');
var genreBtn = document.getElementById("genre-btn");
var genreClose = document.getElementsByClassName("close genre-close")[0];

var titleModal = document.getElementById('title-modal');
var titleBtn = document.getElementById("title-btn");
var titleClose = document.getElementsByClassName("close title-close")[0];

genreBtn.onclick = function() {
	titleModal.style.display = "none";
	genreModal.style.display = "block";
}
genreClose.onclick = function() {
	genreModal.style.display = "none";
}

titleBtn.onclick = function() {
	genreModal.style.display = "none";
	titleModal.style.display = "block";
}
titleClose.onclick = function() {
	titleModal.style.display = "none";
}
window.onclick = function(event) {
	if (event.target == genreModal) {
		genreModal.style.display = "none";
	}
	if (event.target == titleModal) {
		titleModal.style.display = "none";
	}
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/index",
    success: (resultData) => handleListResult(resultData)
});

jQuery.ajax({
	dataType: "json",
	method: "GET",
	url: "api/movies",
	data: "by=" + by + "&arg=" + arg + "&order=" + order + "&page=" + page + "&ipp=" + ipp,
	success: (resultData) => handleMovieResult(resultData)
});