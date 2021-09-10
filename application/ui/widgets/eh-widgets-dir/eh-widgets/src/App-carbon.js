import './index.scss';
import './App-carbon.scss';
import {Route, Switch} from 'react-router-dom';
import CatalogPage from './page/catalog/CatalogPage';


function AppCarbon() {
    return (
        <Switch>
                <Route exact path="/" component={CatalogPage}/>
                <Route exact path="/bundlegroup/:id" component={CatalogPage}/>
        </Switch>
    );
}

export default AppCarbon;
