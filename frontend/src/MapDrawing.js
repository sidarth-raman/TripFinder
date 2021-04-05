import './App.css';
import './Navbar'
import React, {useState, useEffect, useRef} from 'react';


function MapDrawing() {
    const canvasRef = useRef(null)

    useEffect(() => {
        const canvas = canvasRef.current
        const ctx = canvas.getContext('2d')
        ctx.canvas.width = 1024
        ctx.canvas.height = 633
        ctx.beginPath();
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

    return (
        <div>
            <canvas ref={canvasRef} />
        </div>
    );
}

export default MapDrawing;
