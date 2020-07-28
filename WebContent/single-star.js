function getParameterByName(target) {
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleStarResult(resultData) {

    console.log("handleStarResult: populating star info from resultData");

    let starNameElement = jQuery("#star-name");

    starNameElement.append(resultData[0]["star_name"]);
    
    let starInfoElement = jQuery("#star-info");
    starInfoElement.append("<p>Date of Birth: " + resultData[0]["star_dob"] + "</p>");

    console.log("handleStarResult: populating movie table from resultData");

    let movieTableBodyElement = jQuery("#movie-table-body");
    
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td>" + '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
        			+ resultData[i]["movie_title"] + '</a>' + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";
        rowHTML += "</tr>";

        movieTableBodyElement.append(rowHTML);
    }
}

let starId = getParameterByName('id');

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-star?id=" + starId,
    success: (resultData) => handleStarResult(resultData)
});