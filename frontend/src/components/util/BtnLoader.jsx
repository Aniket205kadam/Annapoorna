import React from "react";
import "./BtnLoader.css";

const BtnLoader = ({ color }) => {
  return (
    <svg viewBox="25 25 50 50" className="btn-loader">
      <circle r="20" cy="50" cx="50" style={{ stroke: color}} className="btn-circle"></circle>
    </svg>
  );
};

export default BtnLoader;
