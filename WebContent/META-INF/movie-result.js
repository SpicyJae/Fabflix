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
	let searchElement = jQuery("#searchResult");
    for (let i = 0; i < resultData.length; i++) {
    	searchElement.append(resultData[i]["movieTitle"]);
    	searchElement.append(resultData[i]["movieYear"]);
    	searchElement.append(resultData[i]["movieDirector"]);
    }
}

let title = getParameterByName('title');
let year = getParameterByName('year');
let director = getParameterByName('director');
let name = getParameterByName('name');

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/search?title=" + title + "&year=" + year + "&director=" + director + "name=" + name,
    success: (resultData) => handleResult(resultData)
});