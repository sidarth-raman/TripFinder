 const getActivities = async () =>{
    for(const list of coordinates.entries()){
        let xhr = new XMLHttpRequest()
        xhr.overrideMimeType("application/json");
        xhr.responseType = 'json';
        let resp = null
            // get a callback when the server responds
        xhr.addEventListener('load', () => {
            // update the state of the component with the result here
            resp = xhr.response;
            //this is the list of points of interests  --> do something here
            console.log(resp.results[0].pois);
            //this is the list of ids that correspond to info
            console.log(resp.results[0].poi_division);

            //TODO: this is where you set ur variables and stuff and update states in order to get stuff showing 


        })
        let url = "https://www.triposo.com/api/20210317/local_highlights.json?latitude="
            + list[1][1] + "&longitude=" + list[1][0] + "&fields=poi:id,name,coordinates,snippet&account=TAM6URYM&token=t1vzuahx7qoy0p45f1qidne3acik8e56"
        // open the request with the verb and the url
        xhr.open('GET', url);
        // send the request
        xhr.send();
    }
}