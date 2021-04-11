import './App.css';
import './navbar.css';
import {Route, Link} from "react-router-dom";
import About from "./About";


function Navbar() {
    return (
        <div className="topnav">
            <div><Link to="/">Home</Link></div>
            <div><Link to="/about">About</Link></div>
        </div>

    );
}

export default Navbar;

