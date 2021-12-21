import EhBreadcrumb from "../../../components/eh-breadcrumb/EhBreadcrumb";
import React, { useState, useEffect } from "react";
import { getAllBundleGroupVersionByBundleGroupId, getAllCategories } from "../../../integration/Integration";
import { Content, Loading } from "carbon-components-react";
import { useParams } from "react-router-dom";
import CatalogTiles from "../../catalog/catalog-tiles/CatalogTiles";
import "./bundle-group-versions-page.scss";

const BundleGroupVersionsPage = () => {
  
  const [bgVersionList, setBgVersionList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [categories, setCategories] = useState([])

  //signals the reloading need of the right side
  const [reloadToken, setReloadToken] = useState(((new Date()).getTime()).toString())

  const { id: bundleGroupId } = useParams();
  const IS_VERSIONS_PAGE = true;
  const PAGE = 0;
  const PAGE_SIZE = 12;

  /*
  Callback to the Add and Edit (New Bundle Group) modal form submit
  This implementation ask for bundle groups tiles reloading
    */
  const onAfterSubmit = () => {
    setReloadToken(((new Date()).getTime()).toString()) //internal status change will rerender this component
  }

  const loadVersionData = (bundleGroupId, PAGE, PAGE_SIZE) => {
    return (getAllBundleGroupVersionByBundleGroupId(bundleGroupId, PAGE, PAGE_SIZE));
  }

  useEffect(() => {
    const getVersionList = async () => {
      // const data = (await getAllBundleGroupVersionByBundleGroupId(bundleGroupId, PAGE, PAGE_SIZE));
      const data = await loadVersionData(bundleGroupId, PAGE, PAGE_SIZE);
      if (data && data.versions && data.versions.payload && data.versions.payload.length) {
        setBgVersionList(data.versions.payload);
      }
      setLoading(false);
    }
    getVersionList();
  }, [reloadToken, bundleGroupId]);

  useEffect(() => {
    const getCategories = async () => {
      const data = (await getAllCategories());
      if (data.isError) {
        setLoading(false)
      }
      setCategories(data.categoryList);
    }
    getCategories();
  }, [reloadToken])

  return (
    <>
      <Content className="CatalogPage">
        <div className="CatalogPage-wrapper">
          <div className="bx--grid bx--grid--full-width catalog-page">
            <div className="bx--row">
              <div className="bx--col-lg-16 CatalogPage-breadcrumb">
                <EhBreadcrumb pathElements={[{
                  path: bgVersionList && bgVersionList.length ? bgVersionList[0].name : "",
                  href: window.location.href
                }]} />
              </div>
            </div>

            <div className="bx--row">
              <div className="bx--col-lg-4 CatalogPage-section">
                Catalog
              </div>
            </div>

            <div className="bx--row">
              <div className="bx--col-lg-16 CatalogPageContent-wrapper">
                {bgVersionList && bgVersionList.length 
                  ? 
                    <CatalogTiles bundleGroups={bgVersionList} isVersionsPage={IS_VERSIONS_PAGE} categoriesDetails={categories} reloadToken={reloadToken} onAfterSubmit={onAfterSubmit}/>
                  : 
                <div> No versions found </div>}
              </div>
            </div>
          </div>
        </div>
      </Content>
      {loading && <Loading />}
    </>
  );
}

export default BundleGroupVersionsPage;