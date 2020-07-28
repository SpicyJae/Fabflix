function handleResult(resultData) {
	let messageElement = jQuery("#result-message");
    for (var i = 0; i < resultData.length; i++) {
    	messageElement.append(resultData[i]["message"]);
    }
}

function submitForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    $.get(
        "api/add-star",
        $("#star-form").serialize(),
        (resultData) => handleResult(resultData)
    );
}

$("#star-form").submit((event) => submitForm(event));

