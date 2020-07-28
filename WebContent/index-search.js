function handleLookup(query, doneCallback) {
	console.log("autocomplete initiated")
	console.log("sending AJAX request to backend Java Servlet")
	
	if (sessionStorage.getItem(query) != null) {
		console.log("Query is in cache")
		data = sessionStorage.getItem(query);
		handleLookupAjaxSuccess(data, query, doneCallback)
	} else {
		console.log("New query: send the query to ajax")
		jQuery.ajax({
			"method": "GET",
			"url": "api/autocomplete?title=" + escape(query),
			"success": function(data) {
				sessionStorage.setItem(query, data)
				handleLookupAjaxSuccess(data, query, doneCallback) 
			},
			"error": function(errorData) {
				console.log("lookup ajax error")
				console.log(errorData)
			}
		})
	}
}

function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("lookup ajax successful")
	
	var jsonData = JSON.parse(data);
	console.log(jsonData)
	
	doneCallback( { suggestions: jsonData } );
}

function handleSelectSuggestion(suggestion) {
	// TODO: jump to the specific result page based on the selected suggestion
	console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieID"])
	window.location.replace("single-movie.html?id=" + suggestion["data"]["movieID"]);
}

$('#autocomplete').autocomplete({
	// documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
    		handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
    minChars: 3
});

function handleNormalSearch(query) {
	console.log("doing normal search with query: " + query);
	// TODO: you should do normal search here
	window.location.replace("full-result.html?title=" + query);
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
	// keyCode 13 is the enter key
	if (event.keyCode == 13) {
		// pass the value of the input box to the handler function
		handleNormalSearch($('#autocomplete').val())
	}
})
