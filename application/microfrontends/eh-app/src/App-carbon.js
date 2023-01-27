import "./index.scss"
import "./App-carbon.scss"
import {HashRouter, Route, Switch} from "react-router-dom"
import CatalogPage from "./page/catalog/CatalogPage"
import BundleGroupPage from "./page/bundle-group/BundleGroupPage"
import UserManagementPage from "./page/user-management/UserManagementPage"
import OrganisationManagementPage from "./page/organisation-management/OrganisationManagementPage"
import CategoryManagementPage from "./page/category-management/CategoryManagementPage"
import RouteWithGate from "./components/routing/RouteWithGate";
import {isHubAdmin} from "./helpers/helpers";
import NotificationDispatcher from "./components/notification/NotificationDispatcher"
import i18n from "./i18n"
import BundleGroupVersionsPage from "./page/bundle-group-version/bg-version-catalog/BundleGroupVersionsPage"
import { useState } from "react"

function AppCarbon({config}) {
  const [versionSearchTerm, setVersionSearchTerm] = useState('');
  const { systemParams } = config || {};
  const { api } = systemParams || {};
  const apiUrl = api && api['entando-hub-api'].url;

  return (
    <>
      <NotificationDispatcher />
      <HashRouter>
        <Switch>
          <Route path="**/bundlegroup/:id" render={(props) => <BundleGroupPage apiUrl={apiUrl} {...props}/>}/>
          <Route path="**/bundlegroup/versions/:id" render={(props) => <BundleGroupPage apiUrl={apiUrl} {...props}/>}/>
          <Route path="**/versions/:id/:categoryId" render={(props) => <BundleGroupVersionsPage setVersionSearchTerm={setVersionSearchTerm} apiUrl={apiUrl} {...props}/>}/>
          <RouteWithGate gateFunction={isHubAdmin} path="**/admin*" component={UserManagementPage} apiUrl={apiUrl}/>
          <RouteWithGate gateFunction={isHubAdmin} path="**/organisations*" component={OrganisationManagementPage} apiUrl={apiUrl}/>
          <RouteWithGate gateFunction={isHubAdmin} path="**/organisation*" component={OrganisationManagementPage} apiUrl={apiUrl}/>
          <RouteWithGate gateFunction={isHubAdmin} path="**/categories*" component={CategoryManagementPage} apiUrl={apiUrl}/>
          <RouteWithGate gateFunction={isHubAdmin} path="**/category*" component={CategoryManagementPage} apiUrl={apiUrl}/>
          <Route path="**/unauthorized">
            {i18n.t('page.unauthorized')}
          </Route>
          <Route path="**/" render={(props) => <CatalogPage  setVersionSearchTerm={setVersionSearchTerm} versionSearchTerm={versionSearchTerm} apiUrl={apiUrl} {...props} />}/>
        </Switch>
      </HashRouter>
    </>
  )
}

export default AppCarbon
