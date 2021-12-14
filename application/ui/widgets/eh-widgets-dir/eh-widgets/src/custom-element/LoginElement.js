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

class LoginElement extends HTMLElement {
  container

  mountPoint

  unsubscribeFromKeycloakEvent

  keycloak = getKeycloakInstance()

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

  render(lang) {

    window.entando = { ...window.entando, lang: lang ? lang : 'en' }
    const locale = this.getAttribute('locale') || '';
    let engEle = document.getElementById('engLang');
    let itaEle = document.getElementById('itaLang');
    if (engEle && locale === 'en') {
      engEle.click()
    }
    if (itaEle && locale === 'it') {
      itaEle.click()
    }
    Locale.setLocale(locale);
    ReactDOM.render(
      <KeycloakContext.Provider value={this.keycloak}>
        <Login />
        <div style={{ float: "right", "marginTop": ".3em", "marginRight": "0.3em" }}>
                    <a href="/#">
                        <span onClick={() => { this.setAttribute('locale', 'en'); this.render('en'); }}>
                            ENG
                        </span>
                    </a>
                    <span> | </span>
                    <a href="/#">
                        <span onClick={() => { this.setAttribute('locale', 'it'); this.render('it'); }}>
                            ITA
                        </span>
                    </a>
                </div>
      </KeycloakContext.Provider>,
      this.mountPoint
    )
  }
}

customElements.get('x-eh-login') || customElements.define('x-eh-login', LoginElement)
