import "./custom-element.scss"
import ReactDOM from "react-dom"
import React from "react"
import '../index.css'
import AppCarbon from '../App-carbon'
import {HashRouter as Router} from "react-router-dom"

import {subscribeToWidgetEvent} from '../helpers/widgetEvents'
import {KEYCLOAK_EVENT_TYPE} from './widgetEventTypes'
import * as Locale from '../i18n';

const getKeycloakInstance = () =>
    (window && window.entando && window.entando.keycloak && {...window.entando.keycloak, initialized: true}) || {
        initialized: false,
    }


class XEhApp extends HTMLElement {
    connectedCallback() {
        this.mountPoint = document.createElement('span')
        this.keycloak = {...getKeycloakInstance(), initialized: true}
        this.unsubscribeFromKeycloakEvent = subscribeToWidgetEvent(KEYCLOAK_EVENT_TYPE, (e) => {
            if(e.detail.eventType==="onReady"){
                this.keycloak = {...getKeycloakInstance(), initialized: true}
                this.render()
            }
        })
    }

    render() {
        // window.entando = { ...window.entando, lang: lang ? lang : 'en' }
        const locale = this.getAttribute('locale') || '';
        Locale.setLocale(locale);
        ReactDOM.render(<React.StrictMode>
            <Router>
                <div className="locale-button hide-button">
                    <span id="engLang" onClick={() => { this.setAttribute('locale', 'en'); this.render(); }}>
                        ENG
                    </span>
                    <span> | </span>
                    <span id="itaLang" onClick={() => { this.setAttribute('locale', 'it'); this.render(); }}>
                        ITA
                    </span>
                </div>
                <AppCarbon />
            </Router>
        </React.StrictMode>,
            this.appendChild(this.mountPoint))
    }
}

customElements.get('x-eh-app') || customElements.define("x-eh-app", XEhApp)
