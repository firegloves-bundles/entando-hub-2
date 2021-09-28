import ReactDOM from "react-dom"
import React from "react"
import '../index.css'
import AppCarbon from '../App-carbon'
import {HashRouter as Router} from "react-router-dom"

import {subscribeToWidgetEvent} from '../helpers/widgetEvents'
import {KEYCLOAK_EVENT_TYPE} from './widgetEventTypes'

const getKeycloakInstance = () =>
    (window && window.entando && window.entando.keycloak && {...window.entando.keycloak, initialized: true}) || {
        initialized: false,
    }


class XEhApp extends HTMLElement {
    connectedCallback() {
        this.mountPoint = document.createElement('span')
        this.keycloak = {...getKeycloakInstance(), initialized: true}
        this.unsubscribeFromKeycloakEvent = subscribeToWidgetEvent(KEYCLOAK_EVENT_TYPE, () => {
            this.keycloak = {...getKeycloakInstance(), initialized: true}
            this.render()
        })
        this.render()
    }

    render() {
        ReactDOM.render(<React.StrictMode>
                <Router>
                    <AppCarbon/>
                </Router>
            </React.StrictMode>,
            this.appendChild(this.mountPoint))
    }
}

customElements.get('x-eh-app') || customElements.define("x-eh-app", XEhApp)
