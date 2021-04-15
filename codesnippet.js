
const getActivities = () => {
	//note: you might have to make sure getLat and getLong in this string turn to lat-longitude 
	let url = "https://www.triposo.com/api/20210317/local_highlights.json?latitude=" + getLat + "&longitude=" + getLong + "&fields=poi:id,name,coordinates,snippet"
	

	//if the above doesn't work try this, but I will leave it commented out for now 
	// let params = {
	// 	latitude: getLat, 
	// 	longitude: getLong, 
	// 	fields: "poi:id,name,coordinates,snippet", 
	// }


	let config = {
		headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
                "X-Triposo-Account",  "TAM6URYM", 
                "X-Triposo-Token", "t1vzuahx7qoy0p45f1qidne3acik8e56", 
        }
	}


	axios.post(
		url, 
		//params,
		config
		.then(response => {
			//try to console log the response, not sure what it will be called but you want a field called poi, where it 
			//gives actual examples of places.
			console.log(response);

		})
		.catch(function(error){
			console.log(error); 
		})

	)
}