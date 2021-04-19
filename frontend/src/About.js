import './App.css';
import './Navbar'
import './About.css'
import Navbar from "./Navbar";
import Choices from "./Choices";

function About() {
    return (
        <div className="center">
            <h1>Built by: Sidarth Raman, Mit Ramesh, Daniel Segel, Henry Sowerby</h1>
            <h2>Data sourced from Triposo API, US Census, & US Geological Survey.</h2>
        </div>
    );
}

export default About;