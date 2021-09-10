import './index.scss';
import './App-carbon.scss';
import {Route, Switch} from 'react-router-dom';
import CatalogPage from './page/catalog/CatalogPage';
import BundleGroup from './page/catalog/BundleGroup';


function AppCarbon() {
    return (
        <Switch>
                <Route exact path="/" component={CatalogPage}/>
                <Route exact path="/bundlegroup/:id" component={BundleGroup}/>
        </Switch>
    );
}

export default AppCarbon;
