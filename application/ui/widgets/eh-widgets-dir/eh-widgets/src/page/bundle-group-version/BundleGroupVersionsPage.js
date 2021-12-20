import EhBreadcrumb from "../../components/eh-breadcrumb/EhBreadcrumb";
import React, {useCallback, useState, useEffect} from "react";
import { getAllBundleGroupVersionByBundleGroupId, getAllCategories, getAllOrganisations } from "../../integration/Integration";
import { isHubUser } from "../../helpers/helpers";
// import BundleGroupStatusFilter from "./bundle-group-status-filter/BundleGroupStatusFilter"
import CatalogPageContent from "../catalog/catalog-page-content/CatalogPageContent";
import { Content } from "carbon-components-react";
import { useParams } from "react-router-dom";


const BundleGroupVersionsPage = () => {

   const {id: bundleGroupId} = useParams();
   
   const [versionList, setVersionList] = useState([]);
   const [isError, setIsError] = useState(null);
   const [loading, setLoading] = useState(true);
   const [statusFilterValue, setStatusFilterValue] = useState("LOADING")

   const [pageModel, setPageModel] = useState({
        bundleGroup: {},
        organisation: null,
        category: null,
       children: []
   })

    const hubUser = isHubUser()

//   const [categories, setCategories] = useState([])
//   const [orgList, setOrgList] = useState([])
//   const [loading, setLoading] = useState(true)
  
//   const [currentUserOrg, setCurrentUserOrg] = useState(null);

//   const [orgLength, setOrgLength] = useState(0);
//   const [portalUserPresent, setPortalUserPresent] = useState(false);
//   const [loaded, setLoaded] = useState(false)

  //signals the reloading need of the right side
  const [reloadToken, setReloadToken] = useState(((new Date()).getTime()).toString())

  //filter the BG query by status (only published by default)
  //LOADING means ho use the filter value has to wait
  

//   /*
//   Callback when the status filter is changed
//   The implementation save the user choice in the component state
//     */
//   const changeStatusFilterValue = useCallback((newValue) => {
//     setStatusFilterValue(newValue)
//   }, [])

  /*
  Callback to the Add and Edit (New Bundle Group) modal form submit
  This implementation ask for bundle groups tiles reloading
    */
  const onAfterSubmit = () => {
    setReloadToken(((new Date()).getTime()).toString()) //internal status change will rerender this component
  }

  useEffect(() => {
     const page = 0;
     const pageSize = 10;

    const getVersionList = async () => {
      const data = (await getAllBundleGroupVersionByBundleGroupId(bundleGroupId, page, pageSize));
      if (data.isError) {
        setIsError(data.isError)
        setLoading(false)
      }
     
      if(data && data.versions && data.versions.payload && data.versions.payload.length) {
        setVersionList(data.versions.payload);
        // setBundleGroupName(data.versions.payload[0].name);
      }
    }
    getVersionList();
  }, [bundleGroupId])

    return(
        <Content className="CatalogPage">
          <div className="CatalogPage-wrapper">
            <div className="bx--grid bx--grid--full-width catalog-page">
              <div className="bx--row">
                <div className="bx--col-lg-16 CatalogPage-breadcrumb">
                    <EhBreadcrumb pathElements={[{
                        // path: pageModel.bundleGroup.name,
                        path: versionList && versionList.length && versionList[0].name,
                        href: window.location.href
                    }]}/>
                </div>
              </div>
              <div className="bx--row">
                {/* <div className="bx--col-lg-4 CatalogPage-section">
                  Categories
                </div> */}
                <h2>Bundle Group Versions Page</h2>
                {/* <div className="bx--col-lg-6 CatalogPage-section">
                  Catalog
                </div> */}
                <div className="bx--col-lg-2 CatalogPage-section">
                  {/*
                    Manage the Add (New Bundle Group) button
                    I will wait fe status filter loading, to avoid double rendering (and use effect) call
                   */}
                  {/* {hubUser && statusFilterValue !== "LOADING" && <ModalAddNewBundleGroup isLoading={loading} orgList={orgList} catList={categories} onAfterSubmit={onAfterSubmit} currentUserOrg={currentUserOrg} />} */}
                </div>
                {/* <div className="bx--col-lg-4 CatalogPage-section">
                  Search
                </div> */}
              </div>
              {/*  If the user is an HUB authenticated one (has HUB roles)
                        can see the status filter
                */}

              {hubUser &&
                <div className="bx--row">
                  <div className="bx--col-lg-4 CatalogPage-section">
                    {/*Empty col4 over checkbox filters */}
                  </div>
                  {/* <div className="bx--col-lg-12 CatalogPage-section">
                    <BundleGroupStatusFilter onFilterValueChange={changeStatusFilterValue} />
                  </div> */}
                </div>
              }
              <div className="bx--row">
                {/* Renders the filters on the left an the result on the main column.
                If I'm not an hub user no statusFilter rendered
                If I'm an hub user I'll wait for status filter loading
                        */}
                {/* {(!hubUser || (hubUser && statusFilterValue !== "LOADING")) && <CatalogPageContent isError={isError} catList={categories} reloadToken={reloadToken} statusFilterValue={statusFilterValue} onAfterSubmit={onAfterSubmit} currentUserOrg={currentUserOrg} />} */}
                {<CatalogPageContent isError={isError} reloadToken={reloadToken} statusFilterValue={statusFilterValue} onAfterSubmit={onAfterSubmit} />}
              </div>
            </div>
          </div>
        </Content>
    );
}

export default BundleGroupVersionsPage;