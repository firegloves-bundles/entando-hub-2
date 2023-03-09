import {Content, Tile, Row, Column, Button} from "carbon-components-react"
import { useEffect, useState } from "react"
import { useParams } from "react-router"
import { useLocation } from 'react-router-dom';

import {
  getAllBundlesForABundleGroup,
  getBundleGroupDetailsByBundleGroupVersionId,
  getPrivateCatalog,
  getSingleCategory,
  getSingleOrganisation
} from "../../integration/Integration"
import EhBreadcrumb from "../../components/eh-breadcrumb/EhBreadcrumb"
import { ModalInstallInformation } from "./modal-install-information/ModalInstallInformation"
import {ModalContactUsInformation} from "./modal-contact-us-information/ModalContactUsInformation"
import "./bundle-group-page.scss"
import i18n from "../../i18n"
import { SLASH_VERSIONS } from "../../helpers/constants"
import BundlesOfBundleGroup
  from '../../components/forms/BundleGroupForm/update-bundle-group/bundles-of-bundle-group/BundlesOfBundleGroup';
import { useApiUrl } from "../../contexts/ConfigContext";
import { isHubUser } from "../../helpers/helpers";

/*
BUNDLEGROUP:
{
name	string
description	string
descriptionImage	string
documentationUrl	string
status	string
Enum:
Array [ 2 ]
children	[...]
organisationId	string
categories	[...]
bundleGroupId	string
}


BUNDLE
{
name	string
description	string
gitRepoAddress	string
dependencies	[...]
bundleGroups	[...]
bundleId	string
}
 */

const formatLastUpdate = (date) => {
  return new Intl.DateTimeFormat([], { dateStyle: 'full', timeStyle: 'medium' }).format(new Date(date))
}

const BundleGroupPage = () => {
  const [pageModel, setPageModel] = useState({
    bundleGroup: {},
    organisation: null,
    category: null,
    children: []
  });

  const [catalog, setCatalog] = useState(null);

  const apiUrl = useApiUrl();

  const categoryId = pageModel.bundleGroup && pageModel.bundleGroup.categories && pageModel.bundleGroup.categories.length ? pageModel.bundleGroup.categories[0] : null;
  const { id: bundleGroupVersionId, catalogId } = useParams();
  const { pathname: url } = useLocation();

  const isFromVersionPage = isNavigationFromVersionsPage(url);

  // fetches the bundle group
  useEffect(() => {
    const getBundleGroupDetail = async (bundleGroupVersionId) => {
      const pageModel = {}
      const fetchedBundleGroup = (await getBundleGroupDetailsByBundleGroupVersionId(apiUrl, bundleGroupVersionId, { catalogId })).bgVersionDetails
      pageModel["bundleGroup"] = fetchedBundleGroup
      pageModel["organisation"] = fetchedBundleGroup && fetchedBundleGroup.organisationId ? (await getSingleOrganisation(
          apiUrl,fetchedBundleGroup.organisationId)).organisation : null
      pageModel["category"] = fetchedBundleGroup && fetchedBundleGroup.categories && fetchedBundleGroup.categories.length
        > 0 ? (await getSingleCategory(apiUrl,
          fetchedBundleGroup.categories[0])).category : null
      pageModel["children"] =
        fetchedBundleGroup && fetchedBundleGroup.children && fetchedBundleGroup.children.length > 0 && fetchedBundleGroup.bundleGroupId
          ? (await getAllBundlesForABundleGroup(apiUrl, bundleGroupVersionId, { catalogId })).bundleList
          : []
      return pageModel
    };

    (async () => {
      const indexOf = bundleGroupVersionId.indexOf("&") === -1 ? bundleGroupVersionId.length : bundleGroupVersionId.indexOf("&")
      const sanitizedId = bundleGroupVersionId.substring(0, indexOf)
      setPageModel(await getBundleGroupDetail(sanitizedId))
    })()

    const getCatalog = async () => {
      const { data, isError } = await getPrivateCatalog(apiUrl, catalogId);
      if (!isError) {
        setCatalog(data);
      }
    };

    if (catalogId && isHubUser()) {
      getCatalog();
    }
  }, [apiUrl, bundleGroupVersionId, catalogId]);

  // checks the contact-us url for discover
  const checkContactUsModal = (url) => {
    return url && url.includes("discover.entando.com")
  }

  const baseBreadcrumbPathEls = catalog ? [{ path: catalog.name, href: `/catalog/${catalog.id}` }] : [];

  return (
      <>
        <Content className="BundleGroupPage">
          <div className="BundleGroupPage-wrapper">
            <div className="BundleGroupPage-page">
            <div className="bx--row">
              <div className="bx--col-lg-16 BundleGroupPage-breadcrumb">
                {(isFromVersionPage)
                  // Breadcrumb when navigated from Version page
                  ? <EhBreadcrumb pathElements={[...baseBreadcrumbPathEls, {
                    path: `${i18n.t('breadCrumb.version')}`,
                    href: `${SLASH_VERSIONS}/` + pageModel.bundleGroup.bundleGroupId + `/${categoryId}`
                  }, {
                    path: pageModel.bundleGroup.name,
                    href: ""
                  }]} />
                  :
                  // Breadcrumb when navigated from home page
                  <EhBreadcrumb pathElements={[...baseBreadcrumbPathEls, {
                    path: pageModel.bundleGroup?.name,
                    href: window.location.href
                  }]} />}
              </div>
            </div>
              <Row className="bx--grid bx--grid--full-width">
                <Column lg={4} className="BundleGroupPage-tile">
                  <Tile>
                    <div className="BundleGroupPage-image">
                      {pageModel.bundleGroup && pageModel.bundleGroup.descriptionImage && <img
                          src={pageModel.bundleGroup && pageModel.bundleGroup.descriptionImage}
                          alt="BundleGroup Logo" />}
                    </div>

                    {(pageModel.bundleGroup?.displayContactUrl)
                    && (pageModel.bundleGroup.contactUrl && pageModel.bundleGroup.contactUrl.length > 0)
                        && checkContactUsModal(pageModel.bundleGroup.contactUrl) === false
                        ?
                        <>
                          <div className="BundleGroupPage-contact-us">
                            <p>{i18n.t('page.bundleGroupInfo.contactUsInfo')}</p>
                            <Button href={pageModel.bundleGroup.contactUrl} target="_blank">
                              {i18n.t('component.button.contactUs')}
                            </Button>
                          </div>
                          <hr/>
                        </>
                        :
                        (pageModel.bundleGroup?.contactUrl && pageModel.bundleGroup.contactUrl.length > 0)
                        ?
                        <>
                          <div className="BundleGroupPage-contact-us">
                            <p>{i18n.t('page.bundleGroupInfo.contactUsInfo')}</p>
                            <ModalContactUsInformation bundleGroup={pageModel.bundleGroup}
                                                       children={pageModel.children}/>
                          </div>
                          <hr/>
                        </>
                            : []
                    }

                    {(pageModel.children && pageModel.children.length>0) &&
                      <ModalInstallInformation bundleGroup={pageModel.bundleGroup}
                                               children={pageModel.children}/>
                    }

                    <div className="BundleGroupPage-last-update">
                      {i18n.t('page.bundleGroupInfo.lastUpdate')}
                      <p>{pageModel.bundleGroup && pageModel.bundleGroup.lastUpdate && formatLastUpdate(pageModel.bundleGroup.lastUpdate)}</p>
                    </div>
                    <hr/>
                    <div className="BundleGroupPage-docs">
                    {i18n.t('page.bundleGroupInfo.linkToDocument')} <br/>
                       <a href={pageModel.bundleGroup
                                    && pageModel.bundleGroup.documentationUrl}
                                       target="_new">{i18n.t('page.bundleGroupInfo.documentation')}</a>
                    </div>
                  </Tile>
                </Column>
              <Column lg={12}>
                <Tile>
                  <p className="BundleGroupPage-title">
                    {pageModel.bundleGroup && pageModel.bundleGroup.name}
                  </p>

                  <div className="BundleGroupPage-flex">
                    <Column className="BundleGroupPage-specs">
                      {i18n.t('page.bundleGroupInfo.version')}
                      <p>{pageModel.bundleGroup && pageModel.bundleGroup.version}</p>
                    </Column>

                    <Column className="BundleGroupPage-specs">
                      {i18n.t('page.bundleGroupInfo.category')}
                      <p>{pageModel.category && pageModel.category.name}</p>
                    </Column>

                    <Column className="BundleGroupPage-specs">
                      {i18n.t('page.bundleGroupInfo.organisation')}
                      <p>{pageModel.organisation && pageModel.organisation.name}</p>
                    </Column>
                  </div>

                  <div className="BundleGroupPage-description">
                    {pageModel.bundleGroup && pageModel.bundleGroup.description}
                  </div>
                  {(pageModel.children && pageModel.children.length > 0) &&
                  <>
                    <BundlesOfBundleGroup
                        initialBundleList={pageModel.children}
                        disabled={true}
                    />
                  </>
                  }
                </Tile>
              </Column>
              </Row>
            </div>
          </div>
        </Content>
      </>
  )
}

/**
 * Check if the url contains '/versions'
 * @param {*} url
 * @returns
 */
const isNavigationFromVersionsPage = (url) => {
  if (url && url.indexOf(SLASH_VERSIONS) > 0) {
    return true;
  }
  return false;
}

export default BundleGroupPage
