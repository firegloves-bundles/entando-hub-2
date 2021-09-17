import './index.scss';
import './App-carbon.scss';
import {HashRouter, Route, Switch} from 'react-router-dom';
import CatalogPage from './page/catalog/CatalogPage';
import BundleGroupPage from './page/bundle-group/BundleGroupPage';
import UpdateBundleGroup from "./page/catalog/modal-update-bundle-group/update-boundle-group/UpdateBundleGroup";


function AppCarbon() {
    return (
        <HashRouter>
            <Switch>
                <Route path="**/bundlegroup/:id" component={UpdateBundleGroup}/>
                <Route path="**/" component={CatalogPage}/>
            </Switch>
        </HashRouter>
    );
}

export default AppCarbon;
