import './App.css';
import './Navbar'
import Navbar from "./Navbar";
import Choices from "./Choices";
import About from "./About";
import {Route} from "react-router-dom";
import MapDrawing from "./MapDrawing";

function App() {
  return (
    <div>
        <Navbar />
        <Route exact path="/about" component={About}/>
        <Choices />
        <MapDrawing />
    </div>
  );
};

export default App;
