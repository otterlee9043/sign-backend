import React from "react";
import Circle from "./Circle.js";

function SeatCircle({ index, color, changeSeat }) {
  return <Circle key={index} size="small" state={color} emoji="" handler={changeSeat} />;
}

export default SeatCircle;
