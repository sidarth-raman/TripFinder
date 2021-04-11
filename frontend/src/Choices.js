import './choices.css';
import './App.css';
import './Navbar'

import { render } from 'react-dom';
import axios from 'axios';
import React, {Component, useState, useEffect, useRef} from 'react';

function Choices() {
    const [cityList, setCityList] = useState(["Select City"]);
    const [distList, setDistList] = useState(["Select Max Distance", "< 250 Miles", "500 Miles", "1000 Miles", "2000 Miles", "4000+ Miles"]);
    const [numList, setNumList] = useState(["Select Number", "1", "2", "3", "4", "5+"]);

    const [output, setOutput] = useState([]);
    const [coordinates, setCoordinates] = useState([]);


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
        ctx.canvas.width = 1024
        ctx.canvas.height = 633
        ctx.beginPath();
        ctx.rect(0, 0, 1024, 633);
        ctx.fillStyle = "white";
        ctx.fill();
        ctx.moveTo(0,0);
        ctx.lineTo(0, 633);
        ctx.moveTo(0,633);
        ctx.lineTo(1024,633);
        ctx.moveTo(1024,633);
        ctx.lineTo(1024,0);
        ctx.moveTo(1024,0);
        ctx.lineTo(0,0);
        ctx.stroke();
        var imageObj1 = new Image();
        imageObj1.src = 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/Blank_US_Map_%28states_only%29.svg/1024px-Blank_US_Map_%28states_only%29.svg.png'
        imageObj1.onload = function() {
            ctx.drawImage(imageObj1, 0, 0);
        }
    }, [])

    useEffect(() =>  {
        const canvas = canvasRef.current
        const ctx = canvas.getContext('2d')

        ctx.beginPath();
        ctx.rect(0, 0, 1024, 633);
        ctx.stroke();
        ctx.closePath();


        ctx.stroke();
        var imageObj1 = new Image();
        imageObj1.src = 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/Blank_US_Map_%28states_only%29.svg/1024px-Blank_US_Map_%28states_only%29.svg.png'
        ctx.drawImage(imageObj1, 0, 0);

        ctx.beginPath();


        for (const list of coordinates.entries()) {
            ctx.beginPath();
            ctx.arc((1024*(-125 - list[1][1]))/(-125 + 64.822), 633*(49 - list[1][0])/(49 - 25), 10, 0, 2 * Math.PI);

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
        setOutput(response.data["output"]);
        setCoordinates(response.data["latLong"]);

    }

    return (
        <>
            <div className="formbox">
                <form onSubmit={handleSubmit}>
                    <label>
                        <div className="question">Pick your origin city:</div>
                        <select className="dropdown" onChange={handleInputChangeOrigin} value={value}>                      >
                            {cityList.map((k)=>
                                <option key={k}>{k}</option>)}
                        </select>

                        <br />

                        <div className="question" >Pick your maximum distance:</div>
                        <select className="dropdown" onChange={handleInputChangeDist} value={dist}>
                            {distList.map((k)=>
                                <option value={k}>{k}</option>)}
                        </select>

                        <br />

                        <div className="question" >Select the number of cities you wish to vist:)</div>
                        <select className="dropdown" onChange={handleInputChangeNum} value={num}>
                            {numList.map((k)=>
                                <option value={k}>{k}</option>)}
                        </select>

                        <br />

                        <div className="question" >Select a City you want to visit: (If you have no specific city, leave this blank)</div>
                        <select className="dropdown" onChange={handleInputChangeCity} value={city}>
                            {cityList.map((k)=>
                                <option value={k}>{k}</option>)}
                        </select>

                    </label>
                    <input type="submit" value="Submit" />
                </form>
            </div>
            <div className="question">
                <br />
                Origin City: {valueFinal}    <br />
                Number of Cities: {numFinal}    <br />
                Specific Cities: {cityFinal}    <br />
                Max Dist: {distFinal}    <br />
                Output: {output}

            </div>
            <div style = {{paddingLeft: 200}}>
                <canvas ref={canvasRef} />
            </div>
        </>
    );
}

export default Choices;
