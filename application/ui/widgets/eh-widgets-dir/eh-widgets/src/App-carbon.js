import "./index.scss"
import "./App-carbon.scss"
import { HashRouter, Route, Switch } from "react-router-dom"
import CatalogPage from "./page/catalog/CatalogPage"
import BundleGroupPage from "./page/bundle-group/BundleGroupPage"
import UserManagementPage from "./page/user-management/UserManagementPage"
import OrganisationManagementPage from "./page/organisation-management/OrganisationManagementPage"
import Notification from "./components/notification/Notification"

function AppCarbon() {
  return (
    <HashRouter>
      <Notification />
      <Switch>
        <Route path="**/bundlegroup/:id" component={BundleGroupPage} />
        <Route path="**/admin*" component={UserManagementPage} />
        <Route path="**/orgs*" component={OrganisationManagementPage} />
        <Route path="**/" component={CatalogPage} />
      </Switch>
    </HashRouter>
  )
}

export default AppCarbon
