import './App.css';
import './Navbar'
import Navbar from "./Navbar";
import Choices from "./Choices";
import About from "./About";
import {Route} from "react-router-dom";
import Itinerary from "./Itinerary";
import MapDrawing from "./MapDrawing";

function App() {
    return (
        <div>
            <Navbar />
            <div className="message">This is a Road Trip Finder. Select you origin city and certain inputs to create a custom road-trip for you!</div>
            <Route exact path="/about" component={About}/>
            <Route exact path="/itinerary" component={Itinerary}/>
            <Choices />
        </div>
    );
};

export default App;