import React, {useEffect, useState} from "react";
import axios from "axios";

function Itinerary() {

    const [cityList, setCityList] = useState(["Select City"]);
    const [city, setCity] = useState("");
    //const [lat, setLat] = useState("");
    //const [lon, setLon] = useState("");
    let lat = -22.983611
    let lon = -43.204444

    const handleSubmit = (e) => {
        setCity(city);
        console.log(city);
        getActivities();
        e.preventDefault();
    }

    const handleInputChangeOrigin = (event) => {
        event.persist();
        setCity(event.target.value);
    }

    const requestCity = async () => {
        const toSend = {
            //Nothing To send
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        let response = await axios.post(
            "http://localhost:4567/city",
            toSend,
            config
        )
        setCityList([cityList,...response.data["cityList"]]);
    }

    const getActivities = async () =>{
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

        })
        let url = "https://www.triposo.com/api/20210317/local_highlights.json?latitude="
            + lat + "&longitude=" + lon + "&fields=poi:id,name,coordinates,snippet&account=TAM6URYM&token=t1vzuahx7qoy0p45f1qidne3acik8e56"
        // open the request with the verb and the url
        xhr.open('GET', url);
        // send the request
        xhr.send();
    }


    useEffect(() => {
        requestCity()
    }, [])

    return (
        <div className="formbox">
            <form onSubmit={handleSubmit}>
                <label>
                    <div className="question">Select the city you wish to visit:</div>
                    <select className="dropdown" onChange={handleInputChangeOrigin} value={city}> >
                        {cityList.map((k) =>
                            <option key={k}>{k}</option>)}
                    </select>
                </label>
                <input type="submit" value="Find me something to do!"/>
            </form>
        </div>
    );
}

export default Itinerary;