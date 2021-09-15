import ReactDOM from "react-dom";
import React from "react";
import '../index.css'
import AppCarbon from '../App-carbon';
import {HashRouter as Router} from "react-router-dom";


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
