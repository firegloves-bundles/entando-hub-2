import CatalogPageHeader from "./catalog-page-header/CatalogPageHeader";
import {Content} from "carbon-components-react";
import CatalogPageContent from "./catalog-page-content/CatalogPageContent";
import EhBreadcrumb from "../../components/eh-bradcrumb/EhBreadcrumb";
import {ModalAddNewBundleGroup} from "./modal-add-new-bundle-group/ModalAddNewBundleGroup";
import {useState} from "react";


const CatalogPage = () => {
    const [reloadToken, setReloadToken] = useState(((new Date()).getTime()).toString())

    const afterSubmit = ()=>{
        setReloadToken(((new Date()).getTime()).toString())
    }

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
                            <ModalAddNewBundleGroup afterSubmit={afterSubmit}/>
                            Search
                        </div>
                    </div>
                    <div className="bx--row">
                        <CatalogPageContent reloadToken={reloadToken}/>
                    </div>
                </div>
            </Content>

        </>
    );
};

export default CatalogPage;
