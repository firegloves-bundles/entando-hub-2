import CatalogPageHeader from "./catalog-page-header/CatalogPageHeader";
import CatalogPageFooter from "./catalog-page-footer/CatalogPageFooter";
import {Content} from "carbon-components-react";
import CatalogPageContent from "./catalog-page-content/CatalogPageContent";
import EhBreadcrumb from "../../components/eh-bradcrumb/EhBreadcrumb";
import './catalogPage.scss';

const CatalogPage = () => {
  return (
      <>
        <CatalogPageHeader/>
        <div className="CatalogPage">
          <Content>
            <div className="bx--grid bx--grid--full-width catalog-page">
              <div className="bx--row">
                <div className="bx--col-lg-16 CatalogPage-breadcrumb">
                  <EhBreadcrumb/>
                </div>
              </div>
              <div className="bx--row">
                <div className="bx--col-lg-4 CatalogPage-section">
                  Categories
                </div>
                <div className="bx--col-lg-6 CatalogPage-section">
                  Catalog
                </div>
                <div className="bx--col-lg-6 CatalogPage-section">
                  Search
                </div>
              </div>
              <div className="bx--row">
                <CatalogPageContent/>
              </div>
            </div>
          </Content>
        </div>
        <CatalogPageFooter/>
      </>
  );
};

export default CatalogPage;
