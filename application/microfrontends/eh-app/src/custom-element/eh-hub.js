import "./custom-element.scss"
import React from "react"
import { createRoot } from 'react-dom/client';
import '../index.css'
import AppCarbon from '../App-carbon'
import {HashRouter as Router} from "react-router-dom"
import {subscribeToWidgetEvent} from '../helpers/widgetEvents'
import {KEYCLOAK_EVENT_TYPE} from './widgetEventTypes'
import * as Locale from '../i18n';
import { ConfigContext } from "../contexts/ConfigContext";

const ATTRIBUTES = {
    config: 'config'
};
const getKeycloakInstance = () =>
    (window && window.entando && window.entando.keycloak && {...window.entando.keycloak, initialized: true}) || {
        initialized: false,
    }


class XEhApp extends HTMLElement {

    static get observedAttributes() {
        return Object.values(ATTRIBUTES);
    }

    attributeChangedCallback(attribute, oldValue, newValue) {
        if (!XEhApp.observedAttributes.includes(attribute)) {
            throw new Error(`Untracked changed attributes: ${attribute}`)
        }
        if (this.mountPoint && newValue !== oldValue) {
            this.render();
        }
    }
    connectedCallback() {
        this.mountPoint = document.createElement('div');
        this.appendChild(this.mountPoint);
        this.keycloak = {...getKeycloakInstance(), initialized: true}
        if (this.root === undefined) {
            this.root = createRoot(this.mountPoint);
        }
        this.unsubscribeFromKeycloakEvent = subscribeToWidgetEvent(KEYCLOAK_EVENT_TYPE, (e) => {
            if(e.detail.eventType==="onReady") {
                this.root.unmount();
                this.root = createRoot(this.mountPoint);
                this.render();
            }
        })

    }

    render() {
        const attributeConfig = this.getAttribute(ATTRIBUTES.config);
        const config = attributeConfig && JSON.parse(attributeConfig);
        const locale = this.getAttribute('locale') || '';

        Locale.setLocale(locale);
        this.root.render(
            <ConfigContext.Provider value={config}>
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
            </ConfigContext.Provider>
        );
    }
}

customElements.define("x-eh-app", XEhApp)
