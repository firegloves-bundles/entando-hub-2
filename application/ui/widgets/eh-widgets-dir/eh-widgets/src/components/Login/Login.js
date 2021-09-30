import React, {Component} from 'react'
import {authenticationChanged, getHigherRole, getUserName, isAuthenticated} from "../../helpers/helpers"
import withKeycloak from "../../auth/withKeycloak"
import {HashRouter, Link} from "react-router-dom"
import {getPortalUserDetails} from "../../integration/api-adapters"
import {ADMIN} from "../../helpers/constants";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCoffee } from '@fortawesome/free-solid-svg-icons'
import { faCode, faHighlighter } from '@fortawesome/free-solid-svg-icons';
import { faHome } from "@fortawesome/free-solid-svg-icons";
import "./login.scss"


const KEYCLOAK_EVENT_ID = 'keycloak'

class Login extends Component {

    constructor(props) {
        super(props)
        this.state = {
            loading: true,
            currentUserName: ""
        }
        this.keycloakEventHandler = this.keycloakEventHandler.bind(this)
    }

    keycloakEventHandler(event) {
        const keycloakEvent = event.detail.eventType
        const {keycloak} = this.props
        switch (keycloakEvent) {
            //Wait until keycloak is ready before displaying the nav elements
            case 'onReady':
                this.setState({
                    loading: false
                })
                break
            case 'onAuthRefreshError':
                keycloak.logout()
                break
            default:
                break
        }
    }

    componentDidMount() {
        window.addEventListener(KEYCLOAK_EVENT_ID, this.keycloakEventHandler)
    }

    componentDidUpdate(prevProps) {
        if (authenticationChanged(this.props, prevProps)) {
            this.setState({
                loading: false,
            })
            getUserName().then(username => {
                this.setState({
                    currentUserName: username,
                })

                getPortalUserDetails(username).then(portalUser => {
                    console.log(portalUser)
                    this.setState({
                        portalUser: portalUser
                    })
                })

            })

        }
    }

    componentWillUnmount() {
        window.removeEventListener(KEYCLOAK_EVENT_ID, this.keycloakEventHandler)
    }


    render() {
        const {keycloak} = this.props
        const loginUrl = window.location
        const handleLogin = () => keycloak.login({redirectUri: loginUrl})
        const handleLogout = () => keycloak.logout({redirectUri: window.location})
        if (!this.state.loading) {
            return (
                <span className="entando-login">
          {!isAuthenticated(this.props) ? (
              <>
                  <button className="log-in" onClick={handleLogin}
                          title={"Login"}>
                      {"Login"}<i className="fas fa-sign-in-alt"/>
                      {/*<FontAwesomeIcon icon={faCoffee} />*/}
                      {/*<FontAwesomeIcon icon={['fas', 'boxing-glove']} />*/}
                      {/*<FontAwesomeIcon icon={faHome} />*/}
                  </button>
                  <div style={{ width: 400 }}>
                  </div>
              </>
          ) : (
              <>
                  {(
                      <>
                          <div>
                              {this.state.currentUserName}
                          </div>
                          <div>
                              {this.state.portalUser && this.state.portalUser.organisation && this.state.portalUser.organisation.name}
                          </div>
                          {getHigherRole() === ADMIN &&
                          <div>
                              <HashRouter>
                                  <Link to="/admin">Admin</Link>
                              </HashRouter>
                          </div>
                          }
                      </>
                  )}
                  <button className="log-out" href="#" onClick={handleLogout}
                          title={"Logout"}>
                      {"Logout"}<i className="fas fa-sign-out-alt"/>
                  </button>
              </>
          )}
        </span>
            )
        } else {
            return null
        }
    }
}

export default withKeycloak(Login)
