import React, { Component } from 'react';
import './choices.css';

import { render } from 'react-dom';
import {useState, useEffect} from 'react'


class Choices extends Component {
    cityList = ["Select City", "Chicago", "New York", "Los Angeles"];
    distList = ["Ideal Distance", "0-100 Miles", "100-250 Miles", "250+ Miles"];
    paramList = ["Select Param", "Param 1", "Param 2", "Param 3"];
    origin = "Null";

    constructor(props) {
        super(props);
        this.state = {value: 'coconut',
            valueFinal: '',
            dist: '',
            distFinal: '',
            param: '',
            paramFinal: ''
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleChangeDist = this.handleChangeDist.bind(this);
        this.handleChangeParam = this.handleChangeParam.bind(this);

        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        this.setState({value: event.target.value});
    }

    handleChangeDist(event) {
        this.setState({dist: event.target.value});
    }

    handleChangeParam(event) {
        this.setState({param: event.target.value});
    }

    handleSubmit(event) {
        this.setState({valueFinal: this.state.value})
        this.setState({distFinal: this.state.dist})
        this.setState({paramFinal: this.state.param})

        event.preventDefault();
    }

    render() {
        return (
            <>
            <div className="formbox">
            <form onSubmit={this.handleSubmit}>
                <label>
                    <div className="question">Pick your origin city:</div>
                    <select className="dropdown" value={this.state.value} onChange={this.handleChange}>
                            {this.cityList.map((k)=>
                                <option value={k}>{k}</option>)}
                    </select>
                    <br />
                    <div className="question" >Pick your ideal distance:</div>
                    <select className="dropdown" value={this.state.dist} onChange={this.handleChangeDist}>
                        {this.distList.map((k)=>
                            <option value={k}>{k}</option>)}
                    </select>
                    <br />
                    <div className="question" >Select your param</div>
                    <select className="dropdown" value={this.state.param} onChange={this.handleChangeParam}>
                        {this.paramList.map((k)=>
                            <option value={k}>{k}</option>)}
                    </select>
                    <br />
                </label>
                <input type="submit" value="Submit" />
            </form>
            </div>
                <div className="question">
                    <br />
                    Origin City: {this.state.valueFinal}    <br />
                    Ideal Distance: {this.state.distFinal}    <br />
                    Param: {this.state.paramFinal}
                </div>
            </>
        );
    }
}

export default Choices;