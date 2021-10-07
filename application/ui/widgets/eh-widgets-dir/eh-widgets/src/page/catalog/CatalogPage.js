import {Content} from "carbon-components-react";
import CatalogPageContent from "./catalog-page-content/CatalogPageContent";
import EhBreadcrumb from "../../components/eh-bradcrumb/EhBreadcrumb";
import {ModalAddNewBundleGroup} from "./modal-add-new-bundle-group/ModalAddNewBundleGroup";
import React, {useCallback, useState} from "react";

import './catalogPage.scss'
import {isHubUser} from "../../helpers/helpers"
import BundleGroupStatusFilter from "./bundle-group-status-filter/BundleGroupStatusFilter"

/*
This is the HUB landing page
*/

const CatalogPage = () => {
    //signals the reloading need of the right side
    const [reloadToken, setReloadToken] = useState(((new Date()).getTime()).toString())

    //filter the BG query by status (only published by default)
    //LOADING means ho use the filter value has to wait
    const [statusFilterValue, setStatusFilterValue] = useState("LOADING")

    /*
    Callback when the status filter is changed
    The implementation save the user choice in the component state
     */
    const changeStatusFilterValue = useCallback((newValue) => {
        setStatusFilterValue(newValue)
    }, [])

    /*
    Callback to the Add and Edit (New Bundle Group) modal form submit
    This implementation ask for bundle groups tiles reloading
     */
    const onAfterSubmit = () => {
        setReloadToken(((new Date()).getTime()).toString())
    }

  return (
      <>
        <Content className="CatalogPage">
          <div className="CatalogPage-wrapper">
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
                <div className="bx--col-lg-2 CatalogPage-section">
                  {/*
                    Manage the Add (New Bundle Group) button
                   */}
                  {isHubUser() && <ModalAddNewBundleGroup onAfterSubmit={onAfterSubmit}/>}
                </div>
                <div className="bx--col-lg-4 CatalogPage-section">
                  Search
                </div>
              </div>
              {/*  If the user is an HUB authenticated one (has HUB roles)
                        can see the status filter
                */}

              {isHubUser() &&
              <div className="bx--row">
                <div className="bx--col-lg-4 CatalogPage-section">
                  {/*Empty col4 over checkbox filters */}
                </div>
                <div className="bx--col-lg-12 CatalogPage-section">
                  <BundleGroupStatusFilter onFilterValueChange={changeStatusFilterValue}/>
                </div>
              </div>
              }
              <div className="bx--row">
                {/* Renders the filters on the left an the result on the main column
                        */}
                <CatalogPageContent reloadToken={reloadToken} statusFilterValue={statusFilterValue} onAfterSubmit={onAfterSubmit}/>
              </div>
            </div>
          </div>
        </Content>
      </>
  );
};

export default CatalogPage
