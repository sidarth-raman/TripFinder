import './choices.css';
import './App.css';
import './Navbar'

import { render } from 'react-dom';
import axios from 'axios';
import React, {Component, useState, useEffect, useRef} from 'react';

function Choices() {
    const [cityList, setCityList] = useState(["Select City"]);
    const [distList, setDistList] = useState(["Select Distance", "250 Miles", "500 Miles", "1000 Miles", "2000 Miles", "8000 Miles"]);
    const [numList, setNumList] = useState(["Select Number", "1", "2", "3", "4", "5"]);
    const [firstRender, setFirsRender] = useState(true);

    const CANVAS_HEIGHT = 584;
    const CANVAS_WIDTH = 1228;
    const TOP_LEFT_LAT = 52;
    const TOP_LEFT_LON = -129.2;
    const BOT_RIGHT_LAT = 24.4;
    const BOT_RIGHT_LON = -59.5;

    const [output, setOutput] = useState([]);
    const [coordinates, setCoordinates] = useState([]);
    const [error, setError] = useState();
    const [milesNum, setMilesNum] = useState();
    const [miles, setMiles] = useState();
    const [hours, setHours] = useState();
    const [activityCity, setActCity] = useState();
    const [activities, setActivities] = useState();

    let activitiesList = []


    const [value, setValue] = useState("");
    const [valueFinal, setValueFinal] = useState("");

    const [dist, setDist] = useState("");
    const [distFinal, setDistFinal] = useState("");

    const [num, setNum] = useState("");
    const [numFinal, setNumFinal] = useState("");

    const [city, setCity] = useState("");
    const [cityFinal, setCityFinal] = useState("");

    const canvasRef = useRef(null)

    useEffect(() => {
        const canvas = canvasRef.current
        const ctx = canvas.getContext('2d')
        ctx.canvas.width = CANVAS_WIDTH
        ctx.canvas.height = CANVAS_HEIGHT
        ctx.beginPath();
        ctx.rect(0, 0, 1024, CANVAS_HEIGHT);
        ctx.fillStyle = "white";
        ctx.fill();
        ctx.moveTo(0,0);
        ctx.lineTo(0, CANVAS_HEIGHT);
        ctx.moveTo(0,CANVAS_HEIGHT);
        ctx.lineTo(CANVAS_WIDTH,CANVAS_HEIGHT);
        ctx.moveTo(CANVAS_WIDTH,CANVAS_HEIGHT);
        ctx.lineTo(CANVAS_WIDTH,0);
        ctx.moveTo(CANVAS_WIDTH,0);
        ctx.lineTo(0,0);
        ctx.stroke();
        var imageObj1 = new Image();
        imageObj1.src = 'https://i.imgur.com/fZCKrsO.png'
        imageObj1.onload = function() {
            ctx.drawImage(imageObj1, 0, 0);
        }
    }, [])

    useEffect(() =>  {
        const canvas = canvasRef.current
        const ctx = canvas.getContext('2d')

        ctx.beginPath();
        ctx.rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        ctx.stroke();
        ctx.closePath();


        ctx.stroke();
        var imageObj1 = new Image();
        console.log("NEW DATA");
        imageObj1.src = 'https://i.imgur.com/fZCKrsO.png'
        ctx.drawImage(imageObj1, 0, 0);

        ctx.beginPath();

        console.log(coordinates)
        let counter = 1;
        for (const list of coordinates.entries()) {
            ctx.lineTo((CANVAS_WIDTH*(TOP_LEFT_LON - list[1][1]))/(TOP_LEFT_LON - BOT_RIGHT_LON),
                CANVAS_HEIGHT*(TOP_LEFT_LAT - list[1][0])/(TOP_LEFT_LAT - BOT_RIGHT_LAT));

            ctx.strokeStyle = 'red';

            ctx.arc((CANVAS_WIDTH*(TOP_LEFT_LON - list[1][1]))/(TOP_LEFT_LON - BOT_RIGHT_LON),
                CANVAS_HEIGHT*(TOP_LEFT_LAT - list[1][0])/(TOP_LEFT_LAT - BOT_RIGHT_LAT),
                3, 0, 2 * Math.PI);

            ctx.stroke();
            ctx.lineWidth = 2;

            ctx.moveTo((CANVAS_WIDTH*(TOP_LEFT_LON - list[1][1]))/(TOP_LEFT_LON - BOT_RIGHT_LON),
                CANVAS_HEIGHT*(TOP_LEFT_LAT - list[1][0])/(TOP_LEFT_LAT - BOT_RIGHT_LAT));
            counter++;

        }
        ctx.closePath();


    }, [coordinates])

    const handleInputChangeOrigin = (event) => {
        event.persist();
        setValue(event.target.value);
    }

    const handleInputChangeDist = (event) => {
        event.persist();
        setDist(event.target.value);
    }

    const handleInputChangeNum = (event) => {
        event.persist();
        setNum(event.target.value);
    }

    const handleInputChangeCity = (event) => {
        event.persist();
        setCity(event.target.value);
    }

    const handleSubmit = (e) => {
        const canvas = canvasRef.current
        const ctx = canvas.getContext('2d')

        var imageObj1 = new Image();
        imageObj1.src = 'https://i.imgur.com/fZCKrsO.png'
        ctx.drawImage(imageObj1, 0, 0);

        setValueFinal(value);
        setDistFinal(dist);
        setNumFinal(num);
        setCityFinal(city);


        e.preventDefault();
    }

    useEffect(() => {
        if (!firstRender){
            sendData();
        }else {
            setFirsRender(false)
        }

    }, [numFinal, distFinal, cityFinal, valueFinal])

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

    useEffect(() => {
        requestCity()
    }, [])

    const sendData = async () => {

        const toSend = {
            origin : valueFinal,
            maxDist : distFinal,
            numberOfCities : numFinal,
            city : cityFinal
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        let response = await axios.post(
            "http://localhost:4567/route",
            toSend,
            config
        )

        setOutput(response.data["output"]);

        setCoordinates(response.data["latLong"]);
        setError(response.data["error"]);
        setMilesNum(response.data["routeDist"]);
        setMiles(response.data["routeDist"] + " Miles");



    }
    useEffect(()=> {
        getActivities();
    }, [coordinates])

    useEffect(() =>  {
        if (!firstRender){
            setActivities("Places to see near " + activityCity);
        }else {
            setFirsRender(false)
        }

    }, [activityCity])


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
                let counter = 0;

                if (xhr.status == 429 || xhr.status ==400) {
                    resp = null;
                }

                if (resp !== null) {
                    for (const element of resp.results[0].pois.entries()) {
                        if (counter < 3) {
                            let activity = element[1].name + " - " + element[1].snippet;
                            activitiesList.push(activity)
                        } else {
                            break;
                        }
                    }
                }

                //this is the list of ids that correspond to info
                //TODO: this is where you set ur variables and stuff and update states in order to get stuff showing

            })


            let url = "https://www.triposo.com/api/20210317/local_highlights.json?latitude="
                + list[1][0] + "&longitude=" + list[1][1] + "&fields=poi:id,name,coordinates,snippet&account=TAM6URYM&token=t1vzuahx7qoy0p45f1qidne3acik8e56"
            // open the request with the verb and the url
            xhr.open('GET', url);
            // send the request

            xhr.send();
        }
    }

    useEffect(()=> {
        if (!firstRender){
            let time = Math.round(milesNum/60);
            setHours( time.toString() + " Hours")
        }else {
            setFirsRender(false)
        }


    }, [milesNum])


    return (
        <>
            <div className="formbox">
                <form onSubmit={handleSubmit}>
                    <label>
                        <div className="question">Pick your origin city (Start typing your city's name)</div>
                        <select className="dropdown" onChange={handleInputChangeOrigin} value={value}>                      >
                            {cityList.map((k)=>
                                <option key={k}>
                                    {k}
                                </option>)}
                        </select>

                        <br />

                        <div className="question" >Choose the maximum distance you can travel</div>
                        <select className="dropdown" onChange={handleInputChangeDist} value={dist}>
                            {distList.map((k)=>
                                <option value={k}>{k}</option>)}
                        </select>

                        <br />

                        <div className="question" >Select the number of cities you wish to vist</div>
                        <select className="dropdown" onChange={handleInputChangeNum} value={num}>
                            {numList.map((k)=>
                                <option value={k}>{k}</option>)}
                        </select>

                        <br />

                        <div className="question" >Mark a City you want to visit: (If you have no specific city, leave this blank)</div>
                        <select className="dropdown" onChange={handleInputChangeCity} value={city}>
                            {cityList.map((k)=>
                                <option value={k}>{k}</option>)}
                        </select>

                    </label>
                    <input type="submit" value="Submit" />
                </form>
                <div className="error">{error}</div>
            </div>

            <br />


            <div className="tripInfo">
                Trip Information
            </div>
            <br/>
            <div className="info">
                The total length of your trip is: {miles}<br/>
                The total time of your trip is roughly: {hours}<br/>
            </div>
            <br />
            <br />
            <br />

            <div className="bigBox">
                <div className = "tripData">
                    <ol className="list">
                        {output.map((k)=>
                            <li onClick={() => setActCity(k)}>
                                {k}
                            </li>)}
                    </ol>


                    <div className="activities">
                        {activities}
                    </div>
                </div>
            </div>


            <br/>

            <div style = {{paddingLeft: 100} }>
                <canvas ref={canvasRef} />
            </div>
        </>
    );
}

export default Choices;