import React from "react";
import { Redirect, Route } from "react-router-dom";

function RouteWithGate({ component: Component, gateFunction, apiUrl, ...restOfProps }) {
    return (
        <Route
            {...restOfProps}
            render={(props) =>
                gateFunction() ? <Component {...props} apiUrl={apiUrl}/> : <Redirect to="/unauthorized" />
            }
        />
    );
}

export default RouteWithGate;
