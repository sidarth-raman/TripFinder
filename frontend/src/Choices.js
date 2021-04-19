import './choices.css';
import './App.css';
import './Navbar'

import { render } from 'react-dom';
import axios from 'axios';
import React, {Component, useState, useEffect, useRef} from 'react';
import {act} from "@testing-library/react";

function Choices() {
    const [cityList, setCityList] = useState(["Select City"]);
    const [distList, setDistList] = useState(["Select Distance", "500 Miles", "1000 Miles", "2000 Miles", "4000 Miles", "8000 Miles"]);
    const [numList, setNumList] = useState(["Select Number", "2", "3", "4", "5", "6"]);
    const [firstRender, setFirsRender] = useState(true);
    var imageObj1 = new Image();
    imageObj1.src = 'https://i.imgur.com/fZCKrsO.png'

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
    const [activities, setActivities] = useState([]);
    const [activityCityMessage, setActCityMess] = useState([]);
    const [render, setRender] = useState(1);


    const [value, setValue] = useState("");
    const [valueFinal, setValueFinal] = useState("");

    const [dist, setDist] = useState("");
    const [distFinal, setDistFinal] = useState("");

    const [num, setNum] = useState("");
    const [numFinal, setNumFinal] = useState("");

    const [city, setCity] = useState("");
    const [cityFinal, setCityFinal] = useState("");

    const canvasRef = useRef(null)

    //Draws the Canvas Initially
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

        imageObj1.onload = function() {
            ctx.drawImage(imageObj1, 0, 0);
        }
    }, [])

    //Draws Canvas route
    useEffect(() =>  {


        const canvas = canvasRef.current
        const ctx = canvas.getContext('2d')

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


    }, [coordinates, output])

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
        setActCityMess("");
        setValueFinal(value);
        setDistFinal(dist);
        setNumFinal(num);
        setCityFinal(city);
        let c = render + 1;
        setRender(c);
        setActivities([]);
        // sendData();

        e.preventDefault();
    }

    //Requests the route
    useEffect(() => {
        if (!firstRender){
            sendData();
        }else {
            setFirsRender(false)
        }

    }, [numFinal, distFinal, cityFinal, valueFinal, render])

    //Request the City List Iniitally
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
        if (!firstRender){
            let time = Math.round(milesNum/60);
            setHours( time.toString() + " Hours")
        }else {
            setFirsRender(false)
        }


    }, [milesNum])

    const getActivities = async () => {
        const toSend = {
            cityName: activityCity
        };
        let config = {
            headers: {
                "Content-Type": "application/json",
                'Access-Control-Allow-Origin': '*',
            }
        }
        let response = await axios.post(
            "http://localhost:4567/activity",
            toSend,
            config
        )
        console.log("RAN")
        console.log(response.data["activities"])
        setActCityMess("Places to see in " + activityCity)
        setActivities(response.data["activities"]);
    }

    useEffect(() =>  {
        console.log("RUNNING")
        if (!firstRender){
            getActivities()
        }else {
            setFirsRender(false)
        }

    }, [activityCity])



    return (
        <>
            <div className="formbox">
                <div className="formboxSmall">
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

                        <div className="question" >Choose the ideal distance you want to travel</div>
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
                    </div>

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
                <br />
                <span>Click on the cities to find activities in each city! </span>
            </div>
            <br />

            <br />

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
                        <span>{activityCityMessage}</span>

                        <ol>
                            {activities.map((k)=>
                                <li>
                                    {k}
                                </li>)}
                        </ol>

                    </div>
                </div>
            </div>


            <br/>
            <hr className="line"></hr>
            <br />
            <div style = {{paddingLeft: 100} }>
                <canvas ref={canvasRef} />
            </div>
        </>
    );
}

export default Choices;