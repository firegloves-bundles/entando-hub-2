import React from "react";

const Organisation = ({ name, description }) => {
  return (
    <div>
      <h3>{name}</h3>
      <p>{description}</p>
    </div>
  );
};

export default Organisation;
