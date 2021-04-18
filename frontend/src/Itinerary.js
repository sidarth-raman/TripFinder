import React, {useEffect, useState} from "react";
import axios from "axios";
import './About.css';

function Itinerary() {

    const [cityList, setCityList] = useState(["Select City"]);
    const [city, setCity] = useState("");
    //const [lat, setLat] = useState("");
    //const [lon, setLon] = useState("");
    const [activities, setActivities] = useState("Loading...");

    const handleSubmit = (e) => {
        setCity(city);
        console.log(city);
        //getCoords();
        //console.log(lat)
        //console.log(lon)
        //if (lat !== 0) {
        getActivities();
        console.log(activities);
        //}
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

    const getActivities = () => {
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
            "http://localhost:4567/activity",
            toSend,
            config
        )
            .then(response => {
                // let activityList = [];
                // for (let i = 0; i < response.data.activities.length && i < 10; i++) {
                //     activityList.push(response.data.activities[i]);
                // }
                // for (let i = 0; i < activityList.length; i++) {
                //     console.log(i + ": " + activityList[i]);
                // }
                // let final = ""
                // final = activityList[1]
                setActivities(response.data.activities);
            })

            .catch(function (error) {
                console.log(error);

            });
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