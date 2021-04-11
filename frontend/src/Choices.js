import './choices.css';

import { render } from 'react-dom';
import axios from 'axios';
import React, {Component, useState, useEffect, useRef} from 'react';

function Choices() {
    const [cityList, setCityList] = useState(["Select City", "Los Angeles", "San Francisco", "Chicago"]);
    const [distList, setDistList] = useState(["Select Max Distance", "< 250 Miles", "500 Miles", "1000 Miles", "2000 Miles", "4000+ Miles"]);
    const [numList, setNumList] = useState(["Select Number", "1", "2", "3", "4", "5+"]);

    const [output, setOutput] = useState([]);

    const [value, setValue] = useState("");
    const [valueFinal, setValueFinal] = useState("");

    const [dist, setDist] = useState("");
    const [distFinal, setDistFinal] = useState("");

    const [num, setNum] = useState("");
    const [numFinal, setNumFinal] = useState("");

    const [city, setCity] = useState("");
    const [cityFinal, setCityFinal] = useState("");


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
        setCityList(response.data["cityList"]);
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
        setOutput(response.data["output"]);
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
        </>
    );
}

export default Choices;
