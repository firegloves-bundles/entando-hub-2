import './index.scss';
import './App-carbon.scss';
import {HashRouter, Route, Switch} from 'react-router-dom';
import CatalogPage from './page/catalog/CatalogPage';
import BundleGroupPage from './page/bundle-group/BundleGroupPage';


function AppCarbon() {
    return (
        <HashRouter>
            <Switch>
                <Route path="**/" component={CatalogPage}/>
                <Route path="**/bundlegroup/:id" component={BundleGroupPage}/>
            </Switch>
        </HashRouter>
    );
}

export default AppCarbon;
