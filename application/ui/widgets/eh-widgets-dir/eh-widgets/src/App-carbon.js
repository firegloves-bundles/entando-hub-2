import './index.scss';
import './App-carbon.scss';
import {Route, Switch} from 'react-router-dom';
import CatalogPage from './page/catalog/CatalogPage';
import BundleGroupPage from './page/bundle-group/BundleGroupPage';
import NewBundleGroup from './page/catalog/modal-add-new-bundle-group/new-boundle-group/NewBundleGroup';


function AppCarbon() {
    return (
        <Switch>
                <Route exact path="/" component={CatalogPage}/>
                <Route exact path="/bundlegroup/:id" component={BundleGroupPage}/>
                <Route path="/bgr/new" component={NewBundleGroup}/>
        </Switch>
    );
}

export default AppCarbon;
