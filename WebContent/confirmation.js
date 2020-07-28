function handleResult(resultData) {
    let messageElement = jQuery("#result-message");
    for (var i = 0; i < resultData.length; i++) {
    	messageElement.append(resultData[i]["message"]);
    }
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/confirmation",
    success: (resultData) => handleResult(resultData)
});