function handleResult(resultData) {
    let metadataElement = jQuery("#meta-table-body");

    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<h2>" + resultData[i]["attribute"] + ": </h2>";
        
        for (let j = 0; j < resultData[i]["field"].length; j++) {
        	rowHTML += "<p>" + resultData[i]["field"][j] + " " + resultData[i]["type"][j] + "</p>";
        }
        metadataElement.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/metadata",
    success: (resultData) => handleResult(resultData)
});