import './index.scss';
import './App-carbon.scss';
import {HashRouter, Route, Switch} from 'react-router-dom';
import CatalogPage from './page/catalog/CatalogPage';
import BundleGroupPage from './page/bundle-group/BundleGroupPage';
import UserManagementPage from "./page/user-management/UserManagementPage";


function AppCarbon() {
    return (
        <HashRouter>
            <Switch>
                <Route path="**/bundlegroup/:id" component={BundleGroupPage}/>
                <Route path="**/admin" component={UserManagementPage}/>
                <Route path="**/" component={CatalogPage}/>
            </Switch>
        </HashRouter>
    );
}

export default AppCarbon;
