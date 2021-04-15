import './choices.css';
import './App.css';
import './Navbar'

import { render } from 'react-dom';
import axios from 'axios';
import React, {Component, useState, useEffect, useRef} from 'react';

function Choices() {
    const [cityList, setCityList] = useState(["Select City"]);
    const [distList, setDistList] = useState(["Select Max Distance", "250 Miles", "500 Miles", "1000 Miles", "2000 Miles", "4000 Miles"]);
    const [numList, setNumList] = useState(["Select Number", "1", "2", "3", "4", "5"]);

    const CANVAS_HEIGHT = 584;
    const CANVAS_WIDTH = 1228;
    const TOP_LEFT_LAT = 49.9328;
    const TOP_LEFT_LON = -129.105;
    const BOT_RIGHT_LAT = 24.02;
    const BOT_RIGHT_LON = -59.048;

    const [output, setOutput] = useState([]);
    const [coordinates, setCoordinates] = useState([]);
    const [error, setError] = useState("Error: Please Select Origin City");
    const [miles, setMiles] = useState();



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
        imageObj1.src = 'https://i.imgur.com/fZCKrsO.png'
        ctx.drawImage(imageObj1, 0, 0);

        ctx.beginPath();


        for (const list of coordinates.entries()) {
            ctx.beginPath();
            ctx.strokeStyle = 'red';
            ctx.arc((CANVAS_WIDTH*(TOP_LEFT_LON - list[1][1]))/(TOP_LEFT_LON + BOT_RIGHT_LON),
                CANVAS_HEIGHT*(TOP_LEFT_LAT - list[1][0])/(TOP_LEFT_LAT - BOT_RIGHT_LAT),
                5, 0, 2 * Math.PI);

            console.log(633*(49 - list[1][0])/(49 - 25));

            ctx.stroke()
            ctx.closePath();
        }



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
        setError("");
        setMiles("");
        setValueFinal(value);
        setDistFinal(dist);
        setNumFinal(num);
        setCityFinal(city);
        sendData();
        e.preventDefault();
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

    useEffect(() => {
        requestCity()
    }, [])

    const sendData = async () => {
        console.log(valueFinal);
        console.log(distFinal)
        console.log(numFinal)
        console.log(cityFinal)

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
        console.log(response.data["latLong"])
        console.log(response.data["output"])

        setOutput(response.data["output"]);
        setCoordinates(response.data["latLong"]);
        setError(response.data["error"]);
        setMiles(response.data["routeDist"]);


    }


    const getData = async() => {
        for (const list of coordinates.entries()) {
            let config = {
                headers: {
                    "Content-Type": "application/json",
                    'Access-Control-Allow-Origin': '*',
                    "X-Triposo-Account": "TAM6URYM",
                    "X-Triposo-Token": "t1vzuahx7qoy0p4561gidne3acik8e56",
                }
            }
            let url = "https://www.triposo.com/api/20210317/local_highlights.json?latitude=" + list[1][0] + "&longitude=" + list[1][1] + "&fields=poi:id, name, coordinates,snippet"
            let response = await axios.post(
                url,
                config
            )
            console.log(response.data);
        }
    }


    return (
        <>
            <div className="formbox">
                <form onSubmit={handleSubmit}>
                    <label>
                        <div className="question">Pick your origin city</div>
                        <select className="dropdown" onChange={handleInputChangeOrigin} value={value}>                      >
                            {cityList.map((k)=>
                                <option key={k}>{k}</option>)}
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
            <div className="question">

                <br />
                Origin City: {valueFinal}    <br />
                Number of Cities: {numFinal}    <br />
                Specific Cities: {cityFinal}    <br />
                Max Dist: {distFinal}    <br />
                Output: {output}

            </div>
            <div className='message'>
                Your Road Trip: <br/>
                {output.map((k)=>
                    <li>{k}</li>)}
                The total length of your trip is: {miles}<br/>
                The total time of your trip is roughly: {}<br/>
            </div>
            <div style = {{paddingLeft: 100} }>
                <canvas ref={canvasRef} />
            </div>
        </>
    );
}

export default Choices;