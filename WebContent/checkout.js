function handleResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    if (resultDataJson["status"] === "success") {
        window.location.replace("confirmation.html");
    } else {
        $("#error_message").text(resultDataJson["message"]);
    }
}

function submitForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    $.post(
        "api/checkout",
        $("#customer_form").serialize(),
        (resultDataString) => handleResult(resultDataString)
    );
}

$("#customer_form").submit((event) => submitForm(event));

