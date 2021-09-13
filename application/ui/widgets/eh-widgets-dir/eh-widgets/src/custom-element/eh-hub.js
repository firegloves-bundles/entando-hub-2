import ReactDOM from "react-dom";
import React from "react";
import '../index.css'
import AppCarbon from '../App-carbon';
import {HashRouter as Router} from "react-router-dom";
import reportWebVitals from "../reportWebVitals";


class XEhApp extends HTMLElement {
  connectedCallback() {
    this.mountPoint = document.createElement('span');
    ReactDOM.render(<React.StrictMode>
          <Router>
            <AppCarbon/>
          </Router>
        </React.StrictMode>,
        this.appendChild(this.mountPoint));
    this.appendChild(this.mountPoint);
  }
}
customElements.define("x-eh-app", XEhApp);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
