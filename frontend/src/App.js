import './App.css';
import './Navbar'
import Navbar from "./Navbar";
import Choices from "./Choices";
import About from "./About";
import Itinerary from "./Itinerary";
import {Route} from "react-router-dom";
import MapDrawing from "./MapDrawing";

function App() {
  return (
    <div>
        <Navbar />
        <Route exact path="/about" component={About}/>
        <Route exact path="/itinerary" component={Itinerary}/>
        <Choices />
    </div>
  );
};

export default App;
