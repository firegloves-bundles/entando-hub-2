import "./custom-element.scss"
import React from "react"
import { createRoot } from 'react-dom/client';
import '../index.css'
import AppCarbon from '../App-carbon'
import {HashRouter as Router} from "react-router-dom"
import * as Locale from '../i18n';
import { ConfigContext } from "../contexts/ConfigContext";
import { KeycloakProvider } from "../auth/Keycloak";

const ATTRIBUTES = {
    config: 'config'
};

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
        if (this.root === undefined) {
            this.root = createRoot(this.mountPoint);
        }
        this.render();
    }

    render() {
        const attributeConfig = this.getAttribute(ATTRIBUTES.config);
        const config = attributeConfig && JSON.parse(attributeConfig);
        const locale = this.getAttribute('locale') || '';

        Locale.setLocale(locale);
        this.root.render(
            <KeycloakProvider>
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
            </KeycloakProvider>
        );
    }
}

customElements.define("x-eh-app", XEhApp)
