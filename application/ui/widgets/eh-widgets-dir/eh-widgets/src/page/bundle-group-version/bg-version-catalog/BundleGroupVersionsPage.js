import EhBreadcrumb from "../../../components/eh-breadcrumb/EhBreadcrumb";
import React, { useState, useEffect, useCallback } from "react";
import { getAllBundleGroupVersionByBundleGroupId, getAllCategories } from "../../../integration/Integration";
import { Button, Content, Loading, Pagination } from "carbon-components-react";
import { useParams } from "react-router-dom";
import CatalogTiles from "../../catalog/catalog-tiles/CatalogTiles";
import "./bundle-group-versions-page.scss";
import { MESSAGES } from "../../../helpers/constants";
import i18n from "../../../i18n";
import { Add16 } from '@carbon/icons-react'
import CatalogFilterTile from "../../catalog/catalog-filter-tile/CatalogFilterTile";
import BundleGroupStatusFilter from "../../catalog/bundle-group-status-filter/BundleGroupStatusFilter";

const BundleGroupVersionsPage = () => {
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(12)
  const [bgVersionList, setBgVersionList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [categories, setCategories] = useState([])
  const [totalItems, setTotalItems] = useState(12)
  //filter the BG query by status (only published by default)
  //LOADING means ho use the filter value has to wait
  const [statusFilterValue, setStatusFilterValue] = useState("")

  //signals the reloading need of the right side
  const [reloadToken, setReloadToken] = useState(((new Date()).getTime()).toString())

  const { id: bundleGroupId, categoryId } = useParams();
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

  const onPaginationChange = async ({ page, pageSize }) => {
    setPageSize(pageSize)
    setPage(page)
    const response = await getAllBundleGroupVersionByBundleGroupId(bundleGroupId, page, pageSize, statusFilterValue);
    if (response && response.versions) {
      response.versions.payload && setBgVersionList(response.versions.payload)
      setTotalItems(response.versions.metadata.totalItems)
    }
  }

  const changeStatusFilterValue = useCallback(async (newValue) => {
    setStatusFilterValue(newValue);
    const response = await getAllBundleGroupVersionByBundleGroupId(bundleGroupId, page, pageSize, newValue);
    setPageSize(12)
    setPage(1)
    if (response && response.versions) {
      response.versions.payload && setBgVersionList(response.versions.payload)
      setTotalItems(response.versions.metadata.totalItems)
    }
    // setReloadToken()
  },[])

  useEffect(() => {
    const getVersionList = async () => {
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
              <div className="bx--col-lg-12 CatalogPage-breadcrumb">
                <EhBreadcrumb pathElements={[{
                  path: bgVersionList && bgVersionList.length ? bgVersionList[0].name : "",
                  href: window.location.href
                }]} />
              </div>
            </div>

            <div className="bx--row">
                <div className="bx--col-lg-4 CatalogPage-section">
                    {i18n.t('page.catlogPanel.catlogHomePage.categories')}
                </div>
              <div className="bx--col-lg-5 CatalogPage-section">
              {i18n.t('page.catlogPanel.catlogHomePage.catalog')}
              </div>
              <div className="bx--col-lg-3 CatalogPage-section">
                <Button renderIcon={Add16} disabled={true}>{i18n.t('component.button.add')}</Button>
              </div>
              <div className="bx--col-lg-4 CatalogPage-section">
                {i18n.t('component.button.search')}
              </div>
            </div>

            <div className="bx--row">
              <div className="bx--col-lg-4 CatalogPage-section">
              </div>
              <div className="bx--col-lg-12 CatalogPage-section">
                <BundleGroupStatusFilter onFilterValueChange={changeStatusFilterValue} />
              </div>
            </div>

            <div className="bx--row">
              <div className="bx--col-lg-4">
                {categories && categories.length > 0 &&
                  <CatalogFilterTile categories={categories} categoryId={categoryId}/>}
              </div>
              <div className="bx--col-lg-12 CatalogPageContent-wrapper">
                {bgVersionList && bgVersionList.length 
                  ? 
                    <CatalogTiles bundleGroups={bgVersionList} isVersionsPage={IS_VERSIONS_PAGE} categoryDetails={categories} reloadToken={reloadToken} onAfterSubmit={onAfterSubmit}/>
                  : 
                  <div> {MESSAGES.NO_VERSIONS_FOUND_MSG} </div>}
                <Pagination
                  itemsPerPageText={i18n.t("component.pagination.itemsPerPage")}
                  itemRangeText={
                    (min, max, total) => `${min}–${max} ${i18n.t("component.pagination.of")} ${total} ${i18n.t("component.pagination.items")}`
                  }
                  pageSizes={[12, 18, 24]}
                  totalItems={totalItems}
                  onChange={onPaginationChange}
                  backwardText={i18n.t("component.pagination.previousPage")}
                  forwardText={i18n.t("component.pagination.nextPage")}
                  pageRangeText={
                    (total) => `${i18n.t("component.pagination.of")} ${total}
                        ${total === 1 ? `${i18n.t("component.pagination.page")}` : `${i18n.t("component.pagination.pages")}`}`
                  }
                />
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