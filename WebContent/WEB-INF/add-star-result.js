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
    let messageElement = jQuery("#result-message");
    for (var i = 0; i < resultData.length; i++) {
    	messageElement.append(resultData[i]["message"]);
    }
}

let name = getParameterByName("name");
let birthYear = getParameterByName("birthYear");

jQuery.ajax({
	dataType: "json",
	method: "GET",
	url: "api/add-star?name=" + name + "&birthYear=" + birthYear,
	success: (resultData) => handleResult(resultData)
});