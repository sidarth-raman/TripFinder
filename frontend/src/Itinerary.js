import React, {useEffect, useState} from "react";
import axios from "axios";
import './About.css';

function Itinerary() {

    const [cityList, setCityList] = useState(["Select City"]);
    const [city, setCity] = useState("");
    const [lat, setLat] = useState("");
    const [lon, setLon] = useState("");
    const [activities, setActivities] = useState("Loading...");

    const handleSubmit = (e) => {
        setCity(city);
        console.log(city);
        getCoords();
        console.log(lat)
        console.log(lon)
        if (lat !== 0) {
            getActivities();
            console.log(activities);
        }
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
        setCityList([cityList, ...response.data["cityList"]]);
    }

    const getCoords = () => {
        const toSend = {
            cityName: city
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        axios.post(
            "http://localhost:4567/coords",
            toSend,
            config
        )
            .then(response => {
                setLat(response.data.latitude);
                setLon(response.data.longitude);
                //console.log(response.data.latitude);
                //console.log(response.data.longitude);
            })

            .catch(function (error) {
                console.log(error);

            });
    }
    const getActivities = () => {
        let xhr = new XMLHttpRequest()
        xhr.overrideMimeType("application/json");
        xhr.responseType = 'json';
        let resp = null
        // get a callback when the server responds
        xhr.addEventListener('load', () => {
            // update the state of the component with the result here
            resp = xhr.response;
            //this is the list of points of interests  --> do something here
            console.log(resp.results[0].pois[0].name);
            console.log(resp.results[0].pois[0].snippet);
            let str = "";
            for (let i = 0; i < 10; i++) {
                str += resp.results[0].pois[i].name + ": " + resp.results[0].pois[i].snippet + "\n";
            }
            setActivities(str);
            //this is the list of ids that correspond to info
            console.log(resp.results[0].poi_division);

        })
        //4h6knbkgydzn9zf03sgua2dmf9c45cg5
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
        <div>
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
            <p>{activities}</p>
        </div>
    );
}

export default Itinerary;
