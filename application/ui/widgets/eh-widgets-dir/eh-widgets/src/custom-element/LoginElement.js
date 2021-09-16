import React from 'react';
import ReactDOM from 'react-dom';

import KeycloakContext from '../auth/KeycloakContext';

import {subscribeToWidgetEvent} from '../helpers/widgetEvents';
import {KEYCLOAK_EVENT_TYPE} from './widgetEventTypes';
import Login from '../components/Login/Login';

const getKeycloakInstance = () =>
  (window && window.entando && window.entando.keycloak && {...window.entando.keycloak, initialized: true}) || {
    initialized: false,
  };

class LoginElement extends HTMLElement {
  container;

  mountPoint;

  unsubscribeFromKeycloakEvent;

  keycloak = getKeycloakInstance();

  connectedCallback() {
    this.mountPoint = document.createElement('span');
    this.keycloak = {...getKeycloakInstance(), initialized: true};

    this.unsubscribeFromKeycloakEvent = subscribeToWidgetEvent(KEYCLOAK_EVENT_TYPE, () => {
      this.keycloak = {...getKeycloakInstance(), initialized: true};
      this.render();
    });
    this.render();
    //retargetEvents(shadowRoot);
    this.appendChild(this.mountPoint);
  }

  render() {
    ReactDOM.render(
      <KeycloakContext.Provider value={this.keycloak}>
        <Login/>
      </KeycloakContext.Provider>,
      this.mountPoint
    );
  }
}

customElements.get('x-eh-login') || customElements.define('x-eh-login', LoginElement);
