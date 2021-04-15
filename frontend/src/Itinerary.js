import React, {useEffect, useState} from "react";
import axios from "axios";

function Itinerary() {

    const [cityList, setCityList] = useState(["Select City"]);
    const [city, setCity] = useState("");
    //const [lat, setLat] = useState("");
    //const [lon, setLon] = useState("");
    let lat = 41.82
    let lon = 71.41

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

    const getActivities = async () => {
        //note: you might have to make sure getLat and getLong in this string turn to lat-longitude
        console.log(lat)
        console.log(lon)
        let url = "https://www.triposo.com/api/20210317/local_highlights.json?latitude="
            + lat + "&longitude=" + lon + "&fields=poi:id,name,coordinates,snippet&account=TAM6URYM&token=t1vzuahx7qoy0p45f1qidne3acik8e56"
        //if the above doesn't work try this, but I will leave it commented out for now
        // let params = {
        //     latitude: getLat,
        //     longitude: getLong,
        //     fields: "poi:id,name,coordinates,snippet",
        // }
        let config = {
            headers: {
                "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Credentials": "true",
                "Access-Control-Allow-Methods": "GET,HEAD,OPTIONS,POST,PUT",
                "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept, Authorization",
                //"Content-Type": "application/json",
                //'Access-Control-Allow-Origin': '*',
                //  "X-Triposo-Account":  "TAM6URYM",
                //  "X-Triposo-Token": "t1vzuahx7qoy0p45f1qidne3acik8e56",
            }
        }
        axios.post(
            url,
            //params,
            config)
                .then(response => {
                    //try to console log the response, not sure what it will be called but you want a field called poi, where it
                    //gives actual examples of places.
                    console.log(response);

                })
                .catch(function(error){
                    console.log(error);
                }
        )
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
