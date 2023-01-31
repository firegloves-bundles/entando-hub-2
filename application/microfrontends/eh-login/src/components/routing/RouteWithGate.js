import React from "react";
import { Redirect, Route } from "react-router-dom";

function RouteWithGate({ component: Component, gateFunction, ...restOfProps }) {
    return (
        <Route
            {...restOfProps}
            render={(props) =>
                gateFunction() ? <Component {...props} /> : <Redirect to="/unauthorized" />
            }
        />
    );
}

export default RouteWithGate;
