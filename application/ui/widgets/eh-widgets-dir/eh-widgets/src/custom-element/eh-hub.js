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

    render(lang) {
        const locale = this.getAttribute('locale') || '';
        Locale.setLocale(locale);//working from here
        ReactDOM.render(<React.StrictMode>
            <Router>
                <div style={{ float: "right", "margin-top": ".3em", "margin-right": "0.3em" }}>
                    <a href="javascript:void(0);">
                        <span onClick={() => { this.setAttribute('locale', 'en'); this.render('en'); }}>
                            ENG
                        </span>
                    </a>
                    <span> | </span>
                    <a href="javascript:void(0);">
                        <span onClick={() => { this.setAttribute('locale', 'it'); this.render('it'); }}>
                            ITA
                        </span>
                    </a>
                </div>

                <AppCarbon />
            </Router>
        </React.StrictMode>,
            this.appendChild(this.mountPoint))
    }
}

customElements.get('x-eh-app') || customElements.define("x-eh-app", XEhApp)
