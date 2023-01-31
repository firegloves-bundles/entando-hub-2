import "./custom-element.scss"
import React from 'react'
import ReactDOM from 'react-dom'

import KeycloakContext from '../auth/KeycloakContext'

import {subscribeToWidgetEvent} from '../helpers/widgetEvents'
import {KEYCLOAK_EVENT_TYPE} from './widgetEventTypes'
import Login from '../components/Login/Login'
import * as Locale from '../i18n';

const getKeycloakInstance = () =>
  (window && window.entando && window.entando.keycloak && {...window.entando.keycloak, initialized: true}) || {
    initialized: false,
  }

const ATTRIBUTES = {
  config: 'config'
};

class LoginElement extends HTMLElement {
  static get observedAttributes() {
    return Object.values(ATTRIBUTES);
  }
  container

  mountPoint

  unsubscribeFromKeycloakEvent

  keycloak = getKeycloakInstance()


  attributeChangedCallback(attribute, oldValue, newValue) {
    if (!LoginElement.observedAttributes.includes(attribute)) {
      throw new Error(`Untracked changed attributes: ${attribute}`)
    }
    if (this.mountPoint && newValue !== oldValue) {
      this.render();
    }
  }

  connectedCallback() {
    this.mountPoint = document.createElement('span')
    this.keycloak = {...getKeycloakInstance(), initialized: true}

    this.unsubscribeFromKeycloakEvent = subscribeToWidgetEvent(KEYCLOAK_EVENT_TYPE, () => {
      this.keycloak = {...getKeycloakInstance(), initialized: true}
      this.render()
    })
    this.render()
    //retargetEvents(shadowRoot)
    this.appendChild(this.mountPoint)
  }


  render() {
    const locale = this.getAttribute('locale') || '';
    const engBtn = document.getElementById('engLang');
    const itaBtn = document.getElementById('itaLang');
    const attributeConfig = this.getAttribute(ATTRIBUTES.config);
    const config = attributeConfig && JSON.parse(attributeConfig);

    if (engBtn && locale === 'en') {
      engBtn.click()
    }
    if (itaBtn && locale === 'it') {
      itaBtn.click()
    }
    Locale.setLocale(locale);
    ReactDOM.render(
      <KeycloakContext.Provider value={this.keycloak}>
        <Login config={config}/>
        <div className="locale-button">
          <span onClick={() => { this.setAttribute('locale', 'en'); this.render(); }}>
            ENG
          </span>
          <span> | </span>
          <span onClick={() => { this.setAttribute('locale', 'it'); this.render(); }}>
            ITA
          </span>
        </div>
      </KeycloakContext.Provider>,
      this.mountPoint
    )
  }
}

customElements.define("x-eh-login", LoginElement)
