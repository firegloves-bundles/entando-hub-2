import { Content, Search } from "carbon-components-react";
import CatalogPageContent from "./catalog-page-content/CatalogPageContent";
import EhBreadcrumb from "../../components/eh-breadcrumb/EhBreadcrumb";
import { ModalAddNewBundleGroup } from "./modal-add-new-bundle-group/ModalAddNewBundleGroup";
import React, { useCallback, useEffect, useState } from "react";
import i18n from '../../i18n';
import './catalogPage.scss'
import { getAllCategories, getAllOrganisations } from "../../integration/Integration";
import { getUserName, isHubAdmin, isHubUser } from "../../helpers/helpers";
import BundleGroupStatusFilter from "./bundle-group-status-filter/BundleGroupStatusFilter"
import { getPortalUserByUsername } from "../../integration/Integration";
import './catalogPage.scss';
import { SHOW_NAVBAR_ON_MOUNTED_PAGE, BUNDLE_STATUS } from "../../helpers/constants";

/*
This is the HUB landing page
*/
const CatalogPage = ({versionSearchTerm, setVersionSearchTerm}) => {
  const hubUser = isHubUser()

  const [categories, setCategories] = useState([])
  const [orgList, setOrgList] = useState([])
  const [loading, setLoading] = useState(true)
  const [isError, setIsError] = useState(null)
  const [currentUserOrg, setCurrentUserOrg] = useState(null);

  const [orgLength, setOrgLength] = useState(0);
  const [portalUserPresent, setPortalUserPresent] = useState(false);
  const [loaded, setLoaded] = useState(false)
  // worker is a state that handle state of search input when terms comes from versionPage.
  // it helps to handle Api hit on every change Event.
  const [worker, setWorker] = useState(versionSearchTerm ? versionSearchTerm : '')

  //signals the reloading need of the right side
  const [reloadToken, setReloadToken] = useState(((new Date()).getTime()).toString())

  const [searchTerm, setSearchTerm] = useState(versionSearchTerm ? versionSearchTerm : '')
  //filter the BG query by status (only published by default)
  //LOADING means ho use the filter value has to wait
  const [statusFilterValue, setStatusFilterValue] = useState("LOADING")

  /*
  Callback when the status filter is changed
  The implementation save the user choice in the component state
    */
  const changeStatusFilterValue = useCallback((newValue) => {
    newValue !== BUNDLE_STATUS.ARCHIVED ? setStatusFilterValue(newValue) : setStatusFilterValue([])
    // setStatusFilterValue(newValue)
  }, [])

  /*
  Callback to the Add and Edit (New Bundle Group) modal form submit
  This implementation ask for bundle groups tiles reloading
    */
  const onAfterSubmit = () => {
    setReloadToken(((new Date()).getTime()).toString()) //internal status change will rerender this component
  }

  useEffect(() => {
    const getCatOrgList = async () => {
      const data = (await getAllCategories());
      if (data.isError) {
        setIsError(data.isError)
        setLoading(false)
      }
      setCategories(data.categoryList);
      const orgData = (await getAllOrganisations()).organisationList;
      setOrgList(orgData)
    }
    getCatOrgList();
  }, [])

  useEffect(() => {
    let isMounted = true;
    (async () => {
      const username = await getUserName();
      if (username) {
        const portalUserResp = (await getPortalUserByUsername(username));
        if (isMounted && portalUserResp && !portalUserResp.isError && portalUserResp.portalUser && portalUserResp.portalUser.organisations && portalUserResp.portalUser.organisations[0]) {
          setOrgLength(portalUserResp.portalUser.organisations.length);
          setPortalUserPresent(true);
          setCurrentUserOrg(portalUserResp.portalUser.organisations[0]);
        } else if (isMounted && portalUserResp && portalUserResp.isError) {
          setOrgLength(0);
          setPortalUserPresent(false);
        }
        portalUserResp && isMounted && setLoaded(true);
      }
    })()
    return () => {
      isMounted = false;
      setLoaded(true);
    }
  }, [])

  /**
   * @param {*} e Event object.
   * @param {*} field Name of the field
   * @description This will invoke on onKeyDown Event.
   */
  const searchTermHandler = async (e) => {
    if (e.keyCode === 13 && e.nativeEvent.srcElement) {
      setSearchTerm(e.nativeEvent.srcElement.value);
    }
  }

  /**
   * @param {*} e Event object.
   * @param {*} field Name of the field
   * @description This will invoke on onChange Event.
   */
  const onClearHandler = (e) => {
    // clear term on cross click
    if (e.type === 'click') {
      setSearchTerm('')
      setVersionSearchTerm('')
      return
    }
    // if searchTerm come's from version page and we click cross on home page in that case below logic works
    if (e.type === undefined && versionSearchTerm) {
      setSearchTerm('')
      setVersionSearchTerm('')
      return
    }
    if (e.nativeEvent && e.nativeEvent.srcElement) {
      // if searchTerm come's from version page then we assign that term to catalog-page search term.
      if (versionSearchTerm || worker) {
        setWorker(e.nativeEvent.srcElement.value)
      }
      // on clear through backspace refetch to all data.
      if (!e.nativeEvent.srcElement.value) {
        setSearchTerm(e.nativeEvent.srcElement.value)
        return
      }
    }
  }

  return (
    <>
      {window.entando
        && window.entando.keycloak
        && window.entando.keycloak.authenticated
        && window.entando.keycloak.tokenParsed
        && window.entando.keycloak.tokenParsed.preferred_username
        && !isHubAdmin()
        && (portalUserPresent === false || orgLength === 0)
        ?
        loaded && <p className="notify-user-absense">{i18n.t('page.catlogPanel.AuthMessage.notifyGuestPortalUserMsg')}</p>
        :
        <Content className="CatalogPage">
          <div className="CatalogPage-wrapper">
            <div className="bx--grid bx--grid--full-width catalog-page">
              <div className="bx--row">
                <div className="bx--col-lg-16 CatalogPage-breadcrumb">
                  <EhBreadcrumb
                    pathElements={[{
                      page: SHOW_NAVBAR_ON_MOUNTED_PAGE.isCatalogPage
                    }]}
                  />
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
                  {/*
                    Manage the Add (New Bundle Group) button
                    I will wait fe status filter loading, to avoid double rendering (and use effect) call
                   */}
                  {hubUser && statusFilterValue !== "LOADING" && <ModalAddNewBundleGroup isLoading={loading} orgList={orgList} catList={categories} onAfterSubmit={onAfterSubmit} currentUserOrg={currentUserOrg} />}
                </div>
                <div className="bx--col-lg-4 CatalogPage-section">
                  {/*{i18n.t('component.button.search')}*/}
                  {versionSearchTerm && <Search value={worker} placeholder={i18n.t('component.bundleModalFields.searchByOrgNBundleName')} onKeyDown={searchTermHandler} onChange={onClearHandler} labelText={'Search'} size="xl" id="search-1" />}
                  {!versionSearchTerm && <Search placeholder={i18n.t('component.bundleModalFields.searchByOrgNBundleName')} onKeyDown={searchTermHandler} onChange={onClearHandler} labelText={'Search'} size="xl" id="search-1" />}
                </div>
              </div>
              {/*  If the user is an HUB authenticated one (has HUB roles)
                        can see the status filter
                */}

              {hubUser &&
                <div className="bx--row">
                  <div className="bx--col-lg-4 CatalogPage-section">
                    {/*Empty col4 over checkbox filters */}
                  </div>
                  <div className="bx--col-lg-12 CatalogPage-section">
                    <BundleGroupStatusFilter onFilterValueChange={changeStatusFilterValue} />
                  </div>
                </div>
              }
              <div className="bx--row">
                {/* Renders the filters on the left an the result on the main column.
                If I'm not an hub user no statusFilter rendered
                If I'm an hub user I'll wait for status filter loading
                        */}
                {(!hubUser || (hubUser && statusFilterValue !== "LOADING")) && <CatalogPageContent versionSearchTerm={versionSearchTerm} searchTerm={searchTerm} isError={isError} catList={categories} reloadToken={reloadToken} statusFilterValue={statusFilterValue} onAfterSubmit={onAfterSubmit} orgList={orgList} currentUserOrg={currentUserOrg} />}
              </div>
            </div>
          </div>
        </Content>
      }
    </>
  );
};

export default CatalogPage
