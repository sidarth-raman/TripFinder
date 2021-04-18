import './App.css';
import './Navbar'
import Navbar from "./Navbar";
import Choices from "./Choices";
import About from "./About";
import Itinerary from "./Itinerary";
import {Route} from "react-router-dom";

function App() {
    return (
        <div>
            <Navbar />
            <div className="message">This is a Road Trip Finder. Select your origin city and certain inputs to create a custom road-trip for you!</div>
            <Route exact path="/" component={Choices}/>
            <Route exact path="/about" component={About}/>
            <Route exact path="/itinerary" component={Itinerary}/>
        </div>
    );
};

export default App;
