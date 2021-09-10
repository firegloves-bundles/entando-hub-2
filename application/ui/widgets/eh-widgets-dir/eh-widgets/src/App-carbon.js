import './index.scss';
import './App-carbon.scss';
import {Route, Switch} from 'react-router-dom';
import CatalogPage from './page/catalog/CatalogPage';
import DetailPage from './page/catalog/DetailPage';


function AppCarbon() {
    return (
        <Switch>
                <Route exact path="/" component={CatalogPage}/>
                <Route exact path="/bundlegroup/:id" component={DetailPage}/>
        </Switch>
    );
}

export default AppCarbon;
