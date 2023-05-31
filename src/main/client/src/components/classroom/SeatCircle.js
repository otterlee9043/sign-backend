import React from "react";
import Circle from "./Circle.js";

function SeatCircle({ index, color, changeSeat }) {
  return (
    <span onClick={() => changeSeat(index)}>
      <Circle key={index} size="small" state={color} emoji="" />
    </span>
  );
}

export default SeatCircle;
