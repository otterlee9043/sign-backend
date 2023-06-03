import React from "react";
import Circle from "./Circle";

function ColorCircle({ color, selectColor }) {
  return (
    <span
      onClick={() => {
        selectColor(color);
      }}
    >
      <Circle size="small" state={color} />
    </span>
  );
}

export default ColorCircle;
