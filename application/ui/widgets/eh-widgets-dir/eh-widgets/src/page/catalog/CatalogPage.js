import CatalogPageHeader from "./catalog-page-header/CatalogPageHeader";
import {Button, Content} from "carbon-components-react";
import CatalogPageContent from "./catalog-page-content/CatalogPageContent";
import EhBreadcrumb from "../../components/eh-bradcrumb/EhBreadcrumb";
import { Add16 } from '@carbon/icons-react';
import {ModalAddNewBundleGroup} from "./modal-add-new-bundle-group/ModalAddNewBundleGroup";


const CatalogPage = () => {
    return (
        <>
            <CatalogPageHeader/>
            <Content>
                <div className="bx--grid bx--grid--full-width catalog-page">
                    <div className="bx--row">
                        <div className="bx--col-lg-16">
                            <EhBreadcrumb/>
                        </div>
                    </div>
                    <div className="bx--row">
                        <div className="bx--col-lg-4">
                            Categories
                        </div>
                        <div className="bx--col-lg-6">
                            Catalog
                        </div>
                        <div className="bx--col-lg-6">
                            <ModalAddNewBundleGroup/>
                            Search
                        </div>
                    </div>
                    <div className="bx--row">
                        <CatalogPageContent/>
                    </div>
                </div>
            </Content>

        </>
    );
};

export default CatalogPage;
