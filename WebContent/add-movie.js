function handleResult(resultData) {
	let messageElement = jQuery("#result-message");
    for (var i = 0; i < resultData.length; i++) {
    	messageElement.append(resultData[i]["message"]);
    }
}

function submitForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    $.get(
        "api/add-movie",
        $("#movie-form").serialize(),
        (resultData) => handleResult(resultData)
    );
}

$("#movie-form").submit((event) => submitForm(event));

